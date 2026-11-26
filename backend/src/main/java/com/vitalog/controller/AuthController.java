package com.vitalog.controller;

import com.vitalog.dto.AccountResponse;
import com.vitalog.dto.RegisterRequest;
import com.vitalog.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        var account = userAccountService.register(request);
        return new AccountResponse(account.email());
    }

    @GetMapping("/auth/me")
    public AccountResponse currentAccount(@AuthenticationPrincipal UserDetails userDetails) {
        return new AccountResponse(userDetails.getUsername());
    }
}