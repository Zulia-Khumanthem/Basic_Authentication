package com.example.login.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    // Custom query method for finding by ID
    Optional<User> findById(Integer id);

    // Delete user by ID
    void deleteById(Integer id);
}
