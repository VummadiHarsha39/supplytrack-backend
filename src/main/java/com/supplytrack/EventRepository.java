package com.supplytrack;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // New import for custom query

@Repository // Marks this interface as a Spring repository component
public interface EventRepository extends CrudRepository<Event, Long> {

    // Custom method to find all events for a specific product, ordered by timestamp
    List<Event> findByProductIdOrderByTimestampAsc(Long productId);
}