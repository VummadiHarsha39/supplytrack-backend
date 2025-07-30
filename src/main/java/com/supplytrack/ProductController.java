package com.supplytrack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // NEW IMPORT
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, EventService eventService, UserRepository userRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // DTO for product creation request
    public static class ProductCreationRequest {
        private String name;
        private String origin;
        private String initialLocation;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getInitialLocation() { return initialLocation; }
        public void setInitialLocation(String initialLocation) { this.initialLocation = initialLocation; }
    }

    // DTO for event logging request
    public static class EventLogRequest {
        private String eventType;
        private String eventDescription;
        private String location;

        // Getters and Setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getEventDescription() { return eventDescription; }
        public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    // DTO for product handover request
    public static class ProductHandoverRequest {
        private Long newOwnerUserId;
        private String handoverLocation;
        private String handoverDescription;

        // Getters and Setters
        public Long getNewOwnerUserId() { return newOwnerUserId; }
        public void setNewOwnerUserId(Long newOwnerUserId) { this.newOwnerUserId = newOwnerUserId; }
        public String getHandoverLocation() { return handoverLocation; }
        public void setHandoverLocation(String handoverLocation) { this.handoverLocation = handoverLocation; }
        public String getHandoverDescription() { return handoverDescription; }
        public void setHandoverDescription(String handoverDescription) { this.handoverDescription = handoverDescription; }
    }

    // DTO for Product Traceability Response
    public static class ProductTraceResponse {
        private Product product;
        private List<Event> eventHistory;

        public ProductTraceResponse(Product product, List<Event> eventHistory) {
            this.product = product;
            this.eventHistory = eventHistory;
        }

        // Getters
        public Product getProduct() { return product; }
        public List<Event> getEventHistory() { return eventHistory; }
    }


    @PostMapping // Handles POST requests to /api/products (creation)
    @PreAuthorize("hasRole('FARMER')") // Only FARMER can create products
    public ResponseEntity<Product> createProduct(
            @RequestBody ProductCreationRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB!"));

        Product createdProduct = productService.createProduct(
                request.getName(),
                request.getOrigin(),
                request.getInitialLocation(),
                user.getId()
        );
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // This is public (permitAll in SecurityConfig)
    @GetMapping("/public/test")
    public ResponseEntity<String> publicTest() {
        return new ResponseEntity<>("This is a public endpoint!", HttpStatus.OK);
    }

    // This requires authentication (anyRequest().authenticated() in SecurityConfig)
    @GetMapping("/protected/data")
    public ResponseEntity<String> getProtectedData() {
        return new ResponseEntity<>("This is protected data accessible by authenticated users!", HttpStatus.OK);
    }

    // This requires authentication (anyRequest().authenticated() in SecurityConfig)
    @PostMapping("/{productId}/log-event")
    public ResponseEntity<?> logProductEvent(
            @PathVariable Long productId,
            @RequestBody EventLogRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB!"));

        try {
            Optional<Event> loggedEvent = eventService.logEvent(
                    productId,
                    request.getEventType(),
                    request.getEventDescription(),
                    request.getLocation(),
                    user.getId() // Actor is the logged-in user
            );

            if (loggedEvent.isPresent()) {
                return new ResponseEntity<>(loggedEvent.get(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(Map.of("message", "Product with ID " + productId + " not found."), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "An unexpected error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Only FARMER or DISTRIBUTOR can handover product
    @PostMapping("/{productId}/handover")
    @PreAuthorize("hasAnyRole('FARMER', 'DISTRIBUTOR')") // Restrict to FARMER or DISTRIBUTOR roles
    public ResponseEntity<?> handoverProduct(
            @PathVariable Long productId,
            @RequestBody ProductHandoverRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        User actingUser = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB!"));

        try {
            Optional<Event> handoverEvent = eventService.logEvent(
                    productId,
                    "HANDOVER",
                    request.getHandoverDescription(),
                    request.getHandoverLocation(),
                    request.getNewOwnerUserId() // New owner is the actor for this event type
            );

            if (handoverEvent.isPresent()) {
                return new ResponseEntity<>(Map.of("message", "Product " + productId + " handed over successfully and updated owner to " + request.getNewOwnerUserId()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Product with ID " + productId + " not found for handover."), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "An unexpected error occurred during handover: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This requires authentication (anyRequest().authenticated() in SecurityConfig)
    @GetMapping("/{productId}/trace")
    public ResponseEntity<?> getProductTrace(@PathVariable Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Product with ID " + productId + " not found."), HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();

        List<Event> eventHistory = eventService.getEventsForProduct(productId);

        ProductTraceResponse response = new ProductTraceResponse(product, eventHistory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // This requires authentication (anyRequest().authenticated() in SecurityConfig)
    @GetMapping // Handles GET requests to /api/products (no specific product ID)
    public ResponseEntity<List<Product>> getAllProductsForCurrentUser(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB!"));

        List<Product> products = productRepository.findByOwnerUserId(user.getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // This requires authentication (anyRequest().authenticated() in SecurityConfig)
    @GetMapping("/{productId}/qrcode-data")
    public ResponseEntity<?> getProductQrCodeData(@PathVariable Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Product with ID " + productId + " not found."), HttpStatus.NOT_FOUND);
        }

        String qrData = String.valueOf(productId);

        return new ResponseEntity<>(Map.of("qrCodeData", qrData), HttpStatus.OK);
    }
}