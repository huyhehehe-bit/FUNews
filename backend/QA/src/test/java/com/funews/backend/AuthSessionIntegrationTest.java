package com.funews.backend;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthSessionIntegrationTest extends BackendIntegrationSupport {
    @Test
    void browserCanObtainCsrfLoginRestoreSessionAndLogout() throws Exception {
        MvcResult csrfResponse = mvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.headerName").isNotEmpty())
                .andReturn();
        JsonNode csrfToken = body(csrfResponse);
        MockHttpSession session = session(csrfResponse);
        String anonymousSessionId = session.getId();

        MvcResult loginResponse = mvc.perform(post("/api/auth/login")
                        .session(session)
                        .header(csrfToken.path("headerName").asText(), csrfToken.path("token").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "Admin", "password", "Admin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@fu.edu.vn"))
                .andExpect(jsonPath("$.role").value(1))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();
        session = session(loginResponse);
        assertThat(session.getId()).isNotEqualTo(anonymousSessionId);

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(body(loginResponse).path("id").asInt()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        // Authentication rotates the token; the browser must fetch a new one.
        JsonNode logoutToken = body(mvc.perform(get("/api/auth/csrf").session(session))
                .andExpect(status().isOk()).andReturn());
        assertThat(logoutToken.path("token").asText()).isNotEqualTo(csrfToken.path("token").asText());
        mvc.perform(post("/api/auth/logout").session(session)
                        .header(csrfToken.path("headerName").asText(), csrfToken.path("token").asText()))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/logout").session(session)
                        .header(logoutToken.path("headerName").asText(), logoutToken.path("token").asText()))
                .andExpect(status().isNoContent());
        assertThat(session.isInvalid()).isTrue();
        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidCredentialsAndUnauthenticatedApiRequests() throws Exception {
        mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "a@fu.edu.vn", "password", "wrong-password"))))
                .andExpect(status().isUnauthorized());
        for (String endpoint : new String[]{"/api/auth/me", "/api/accounts", "/api/categories", "/api/news", "/api/dashboard"}) {
            mvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
        }
    }

    @Test
    void allowsViteCorsPreflightAndRejectsUnknownOrigins() throws Exception {
        mvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type,x-csrf-token"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));

        mvc.perform(options("/api/auth/login")
                        .header("Origin", "https://untrusted.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void authenticatedWritesRequireCsrf() throws Exception {
        MockHttpSession admin = login("a@fu.edu.vn", "123");
        mvc.perform(post("/api/categories").session(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(category("CSRF protected", 0))))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/logout").session(admin))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/auth/me").session(admin))
                .andExpect(status().isOk());
    }

    @Test
    void staffCanReadNewsAndAuthorNamesButCannotManageAccountsOrCategories() throws Exception {
        MockHttpSession staff = login("b@fu.edu.vn", "123");
        mvc.perform(get("/api/news").session(staff)).andExpect(status().isOk());
        MvcResult categories = mvc.perform(get("/api/categories").session(staff))
                .andExpect(status().isOk()).andReturn();
        long categoryId = body(categories).get(0).path("id").asLong();
        mvc.perform(get("/api/accounts/lookup").session(staff))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
        mvc.perform(get("/api/accounts").session(staff)).andExpect(status().isForbidden());
        mvc.perform(post("/api/accounts").session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(account("Forbidden", "forbidden@fu.edu.vn", "123", 1))))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/accounts/{id}", currentUserId(staff)).session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(account("Forbidden", "b@fu.edu.vn", "123", 1))))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/accounts/{id}", currentUserId(staff)).session(staff).with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/categories").session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(category("Forbidden", 0))))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/categories/{id}", categoryId).session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(category("Forbidden", 0))))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/categories/{id}", categoryId).session(staff).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void newsCrudUsesAuthenticatedAuthorAndProtectsReferencedCategory() throws Exception {
        MockHttpSession admin = login("a@fu.edu.vn", "123");
        long categoryId = createCategory(admin, "Fixture news category", 0);
        MockHttpSession staff = login("b@fu.edu.vn", "123");
        long staffId = currentUserId(staff);
        LocalDate dateBeforeCreate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        MvcResult created = mvc.perform(post("/api/news").session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(news("Fixture article", categoryId, currentUserId(admin), 1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdById").value((int) staffId))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andReturn();
        long articleId = body(created).path("id").asLong();
        String createdDate = body(created).path("createdDate").asText();
        assertThat(LocalDate.parse(createdDate)).isBetween(dateBeforeCreate,
                LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        mvc.perform(put("/api/news/{id}", articleId).session(staff).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(news("Updated article", categoryId, currentUserId(admin), 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated article"))
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.createdById").value((int) staffId))
                .andExpect(jsonPath("$.createdDate").value(createdDate));
        JsonNode persisted = findById(body(mvc.perform(get("/api/news").session(staff))
                .andExpect(status().isOk()).andReturn()), articleId);
        assertThat(persisted).isNotNull();
        assertThat(persisted.path("title").asText()).isEqualTo("Updated article");
        assertThat(persisted.path("categoryId").asLong()).isEqualTo(categoryId);

        mvc.perform(delete("/api/categories/{id}", categoryId).session(admin).with(csrf()))
                .andExpect(status().isConflict());
        mvc.perform(delete("/api/news/{id}", articleId).session(staff).with(csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/news/{id}", articleId).session(staff))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/categories/{id}", categoryId).session(admin).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
