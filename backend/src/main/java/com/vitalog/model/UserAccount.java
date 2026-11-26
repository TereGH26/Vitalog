package com.vitalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
public record UserAccount(
        @Id String email,
        String passwordHash,
        Instant createdAt) {
}