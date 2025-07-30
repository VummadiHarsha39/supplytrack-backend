package com.supplytrack;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // NEW IMPORT: for List

@Repository // Marks this interface as a Spring repository component
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Custom method to find all products currently owned by a specific user
    List<Product> findByOwnerUserId(Long ownerUserId);
}