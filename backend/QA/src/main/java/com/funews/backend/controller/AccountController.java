package com.funews.backend.controller;

import com.funews.backend.dto.ApiDtos.AccountLookup;
import com.funews.backend.dto.ApiDtos.AccountRequest;
import com.funews.backend.dto.ApiDtos.AccountResponse;
import com.funews.backend.dto.ApiDtos.ProfileRequest;
import com.funews.backend.service.AccountService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponse> accounts() {
        return service.listAccounts();
    }

    @GetMapping("/accounts/lookup")
    public List<AccountLookup> accountLookup() {
        return service.lookupAccounts();
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse account(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        AccountResponse created = service.createAccount(request);
        return ResponseEntity.created(URI.create("/api/accounts/" + created.id())).body(created);
    }

    @PutMapping("/accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return service.updateAccount(id, request);
    }

    @DeleteMapping("/accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, Authentication authentication) {
        service.deleteAccount(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public AccountResponse profile(Authentication authentication) {
        return service.currentAccount(authentication.getName());
    }

    @PutMapping("/profile")
    public AccountResponse updateProfile(Authentication authentication, @Valid @RequestBody ProfileRequest request) {
        return service.updateProfile(authentication.getName(), request);
    }
}
