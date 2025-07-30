package com.supplytrack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks this class as a REST controller
@RequestMapping("/api") // Base path for all endpoints in this controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring will inject UserRepository and PasswordEncoder
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // DTO for user registration request (nested class for simplicity)
    public static class RegistrationRequest {
        private String username;
        private String password;
        private String role; // e.g., "ROLE_FARMER", "ROLE_DISTRIBUTOR", "ROLE_RESTAURANT"

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    @PostMapping("/register") // Handles POST requests to /api/register
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        // Check if user already exists
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
        }

        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        // Create new user, ensure role starts with "ROLE_"
        String role = registrationRequest.getRole().toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role; // Prepend "ROLE_" if missing
        }

        // Use the static factory method to create a new User
        User newUser = User.createNewUser(registrationRequest.getUsername(), encodedPassword, role);
        userRepository.save(newUser); // Save the user to the database

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    // PUBLIC TEST ENDPOINT (Permitted in SecurityConfig)
    @GetMapping("/public/test") // Handles GET requests to /api/public/test
    public ResponseEntity<String> publicTest() {
        return new ResponseEntity<>("This is a public endpoint!", HttpStatus.OK);
    }

    // NEW PROTECTED TEST ENDPOINT (Requires authentication as per SecurityConfig's anyRequest().authenticated())
    @GetMapping("/protected/data") // Handles GET requests to /api/protected/data
    public ResponseEntity<String> getProtectedData() {
        return new ResponseEntity<>("This is protected data accessible by authenticated users!", HttpStatus.OK);
    }
}