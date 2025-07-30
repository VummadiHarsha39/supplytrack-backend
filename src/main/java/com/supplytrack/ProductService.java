package com.supplytrack;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Marks this class as a Spring service component
public class ProductService {

    private final ProductRepository productRepository;
    private final EventRepository eventRepository; // Will use this later for initial event logging

    public ProductService(ProductRepository productRepository, EventRepository eventRepository) {
        this.productRepository = productRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional // Ensures methods are executed within a database transaction
    public Product createProduct(String name, String origin, String initialLocation, Long ownerUserId) {
        // Create the new product
        Product newProduct = Product.createNewProduct(name, origin, "HARVESTED", initialLocation, ownerUserId);
        Product savedProduct = productRepository.save(newProduct);

        // Log the initial "HARVESTED" event for the product
        Event initialEvent = Event.createNewEvent(
                savedProduct.getId(),
                "HARVESTED",
                "Product initially harvested and created.",
                initialLocation,
                ownerUserId
        );
        eventRepository.save(initialEvent);

        return savedProduct;
    }

    // You can add more product-related business logic methods here later,
    // e.g., getProductDetails, updateProductStatus, etc.
}
