package com.supplytrack;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List; // NEW IMPORT: for List
import java.util.Optional;

@Service // Marks this class as a Spring service component
public class EventService {

    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<Event> logEvent(Long productId, String eventType, String eventDescription, String location, Long actorUserId) {
        // 1. Verify Product exists
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            // Product not found, cannot log event
            return Optional.empty();
        }
        Product product = productOptional.get();

        // 2. Verify Actor User exists (optional, but good practice for data integrity)
        if (userRepository.findById(actorUserId).isEmpty()) {
            throw new IllegalArgumentException("Actor user with ID " + actorUserId + " not found.");
        }

        // 3. Create the new event
        Event newEvent = Event.createNewEvent(productId, eventType, eventDescription, location, actorUserId);
        Event savedEvent = eventRepository.save(newEvent);

        // 4. Update Product's current status and location based on event type
        // This is a simplified logic. Real-world might use more complex state machines.
        product.setCurrentStatus(eventType); // Update status to the new event type
        product.setCurrentLocation(location); // Update product's current location
        product.setOwnerUserId(actorUserId); // For handover events, the actor becomes the new owner
        productRepository.save(product); // Save the updated product

        return Optional.of(savedEvent);
    }

    // NEW METHOD: Get events for a product (called by ProductController)
    public List<Event> getEventsForProduct(Long productId) {
        return eventRepository.findByProductIdOrderByTimestampAsc(productId);
    }
}