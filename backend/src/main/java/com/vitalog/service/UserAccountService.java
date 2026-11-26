package com.vitalog.service;

import com.vitalog.dto.RegisterRequest;
import com.vitalog.model.UserAccount;
import com.vitalog.repository.UserAccountRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.existsById(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        UserAccount account = new UserAccount(
                email,
                passwordEncoder.encode(request.password()),
                Instant.now());

        try {
            return userAccountRepository.save(account);
        } catch (DuplicateKeyException exception) {
            throw new EmailAlreadyRegisteredException();
        }
    }
}