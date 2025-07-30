package com.supplytrack;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring repository component
public interface UserRepository extends CrudRepository<User, Long> {

    // Custom method to find a user by username
    Optional<User> findByUsername(String username);
}