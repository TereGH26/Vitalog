package com.vitalog.repository;

import com.vitalog.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
}