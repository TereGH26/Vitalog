package com.vitalog.service;

import com.vitalog.dto.RegisterRequest;
import com.vitalog.model.UserAccount;
import com.vitalog.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTests {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Test
    void storesNormalizedEmailAndBcryptHash() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
        when(userAccountRepository.existsById("ana@example.com")).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccountService service = new UserAccountService(userAccountRepository, passwordEncoder);
        UserAccount account = service.register(new RegisterRequest("Ana@Example.com", "VitaLog1!"));

        assertThat(account.email()).isEqualTo("ana@example.com");
        assertThat(account.passwordHash()).startsWith("$2");
        assertThat(account.passwordHash()).isNotEqualTo("VitaLog1!");
        assertThat(passwordEncoder.matches("VitaLog1!", account.passwordHash())).isTrue();
        verify(userAccountRepository).save(account);
    }

    @Test
    void rejectsAnEmailThatAlreadyExists() {
        when(userAccountRepository.existsById("ana@example.com")).thenReturn(true);

        UserAccountService service = new UserAccountService(
                userAccountRepository,
                new BCryptPasswordEncoder(4));

        assertThatThrownBy(() -> service.register(new RegisterRequest("Ana@Example.com", "VitaLog1!")))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
    }
}