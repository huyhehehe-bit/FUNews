package com.funews.backend.controller;

import com.funews.backend.dto.ApiDtos.DashboardResponse;
import com.funews.backend.dto.ApiDtos.NewsRequest;
import com.funews.backend.dto.ApiDtos.NewsResponse;
import com.funews.backend.service.NewsService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Huy - bai viet va dashboard. Phan quyen da khai trong SecurityConfig cua QA. */
@RestController
@RequestMapping("/api")
public class NewsController {
    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    @GetMapping("/news")
    public List<NewsResponse> news() {
        return service.listNews();
    }

    @GetMapping("/news/{id}")
    public NewsResponse article(@PathVariable Long id) {
        return service.getNews(id);
    }

    @PostMapping("/news")
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsRequest request,
                                                   Authentication authentication) {
        // authentication.getName() la email cua tai khoan dang dang nhap, do SecurityConfig dat vao.
        NewsResponse created = service.createNews(request, authentication.getName());
        return ResponseEntity.created(URI.create("/api/news/" + created.id())).body(created);
    }

    @PutMapping("/news/{id}")
    public NewsResponse updateNews(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        return service.updateNews(id, request);
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        service.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
}
