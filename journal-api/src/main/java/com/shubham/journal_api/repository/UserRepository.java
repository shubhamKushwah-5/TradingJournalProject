package com.shubham.journal_api.repository;

import com.shubham.journal_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    //find user by username
    Optional<User> findByUsername(String username);

    //find user by email
    Optional<User> findByEmail(String email);

    //check if username exists
    Boolean existsByUsername(String username);

    //check if email exists
    Boolean existsByEmail(String email);
}
