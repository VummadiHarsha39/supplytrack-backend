# SupplyTrack - A Modular Food Supply Chain Tracking System (Backend)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://openjdk.java.net/projects/jdk/17/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.5-316192?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=white)](https://react.dev/)

## Table of Contents
1. [About the Project](#about-the-project)
2. [Features](#features)
3. [Architecture Overview](#architecture-overview)
4. [Tech Stack](#tech-stack)
5. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Backend Setup](#backend-setup)
    - [Database Setup](#database-setup)
    - [Running the Backend](#running-the-backend)
    - [Initial Testing (Postman/cURL)](#initial-testing-postmancurl)
6. [Role-Based Access Control](#role-based-access-control)
7. [Future Enhancements](#future-enhancements)
8. [Contact](#contact)
9. [License](#license)

---

## 1. About the Project

`SupplyTrack` is a robust, web-based food supply chain tracking system designed to provide end-to-end traceability for food products, from their origin (farm) to the customer. Its primary goal is to enhance transparency, accountability, and efficiency in food logistics by meticulously tracking product movement and ownership.

Built with Spring Boot, it handles complex real-world supply chain scenarios including multi-party interactions (farmers, distributors, restaurants), detailed product lifecycle events, and chain of custody. This project demonstrates a strong understanding of full-stack development principles, enterprise-level architecture, and secure application design.

## 2. Features

This backend API provides the following key functionalities:

### Core Management
- **User Authentication & Authorization:** Secure registration and login (HTTP Basic Auth) with comprehensive role-based access control (`FARMER`, `DISTRIBUTOR`, `RESTAURANT`, `ADMIN`).
- **Product Management:** APIs to create new food products with initial details (name, origin, location).
- **Event Logging:** Generic API to log various events (e.g., `SHIPPED`, `RECEIVED`, `QUALITY_CHECK`, `DAMAGED`, `SOLD`) for any product, recording timestamp, location, and the user who performed the action.
- **Product Handover Logic:** Specific functionality to transfer product ownership between authorized parties in the supply chain (e.g., from Farmer to Distributor, Distributor to Restaurant).

### Traceability & Querying
- **Full Lifecycle Traceability:** Retrieve the complete chronological history of any product, detailing every event it underwent from creation to its current state.
- **Dashboard Data:** API to fetch products currently owned by the authenticated user, useful for personalized dashboard overviews.
- **QR Code Data Generation (Backend):** Provides a clean product ID (as a string) to be encoded into a QR code for quick traceability lookup, linking physical items to their digital history.

## 3. Architecture Overview

The backend follows a classic **layered architecture** (Controller -> Service -> Repository), adhering to the principles of separation of concerns for maintainability and scalability. This design promotes clear responsibilities, easier testing, and flexibility for future expansions:

-   **Controllers (`ProductController`, `UserController`):** Act as the entry point for incoming HTTP requests. They handle request mapping, input validation, and delegate business logic execution to services, returning formatted API responses (JSON).
-   **Services (`ProductService`, `EventService`, `CustomUserDetailsService`):** Encapsulate the core business logic of the application. They orchestrate complex operations across multiple repositories, manage transactional boundaries (`@Transactional`), and enforce application-specific business rules (e.g., product status transitions, role-based action authorization).
-   **Repositories (`UserRepository`, `ProductRepository`, `EventRepository`):** Responsible for data persistence. Utilizing Spring Data JDBC, they provide abstract interfaces for standard CRUD (Create, Read, Update, Delete) operations and custom query methods directly interacting with the PostgreSQL database, minimizing boilerplate code.
-   **Security (`SecurityConfig`):** Configures the application's security policies. This includes CORS (Cross-Origin Resource Sharing) setup, password encoding (`BCrypt`), and leveraging Spring Security's powerful method-level (`@EnableMethodSecurity`, `@PreAuthorize`) annotations for fine-grained, role-based access control.

The backend exposes **RESTful APIs** which are designed to be consumed by a separate frontend application (available in the [supplytrack-frontend](https://github.com/VummadiHarsha39/supplytrack-frontend) repository).

## 4. Tech Stack

-   **Backend:**
    -   [Spring Boot](https://spring.io/projects/spring-boot) (v3.5.4): Framework for rapid Spring application development.
    -   [Spring Security](https://spring.io/projects/spring-security) (v6.x): Comprehensive security framework for authentication and authorization.
    -   [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc): Simplifies data access and persistence with relational databases.
    -   [PostgreSQL](https://www.postgresql.org/) (v17.5): Robust, open-source relational database.
    -   [Maven](https://maven.apache.org/): Dependency management and build automation tool.
    -   Java 17: Modern Long-Term Support (LTS) version of the Java platform.
-   **Development & Testing Tools:**
    -   [IntelliJ IDEA](https://www.jetbrains.com/idea/) (IDE)
    -   [pgAdmin 4](https://www.pgadmin.org/) (GUI for PostgreSQL management)
    -   [Postman](https://www.postman.com/) or `cURL` (CLI tool for API testing)
-   **Frontend (Separate Repository):**
    -   [ReactJS](https://react.dev/): JavaScript library for building user interfaces.
    -   [Vite](https://vitejs.dev/): Next-generation frontend tooling for fast development.
    -   [Ant Design](https://ant.design/): Enterprise-class UI design language and React UI library.
    -   [Axios](https://axios-http.com/): Promise-based HTTP client for the browser and Node.js.
    -   [React Router DOM](https://reactrouter.com/en/main): Declarative routing for React.
    -   [html5-qrcode](https://www.npmjs.com/package/html5-qrcode): Library for QR code scanning.

## 5. Getting Started

Follow these steps to get the SupplyTrack backend up and running on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:
-   **Java Development Kit (JDK) 17 or higher**
-   **Maven**
-   **PostgreSQL (v17.5 recommended)**
-   **pgAdmin 4**
-   **IntelliJ IDEA (Community Edition)**
-   **Postman** or `cURL`

### Backend Setup

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/VummadiHarsha39/supplytrack-backend.git](https://github.com/VummadiHarsha39/supplytrack-backend.git)
    cd supplytrack-backend
    ```
2.  **Configure Database Connection:**
    -   Open the `src/main/resources/application.properties` file in your IDE.
    -   Update the database connection details to match your local PostgreSQL setup:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/supplytrack_db
        spring.datasource.username=postgres
        spring.datasource.password=your_postgres_password # <<< REPLACE WITH YOUR POSTGRESQL PASSWORD (e.g., 'abc')
        spring.datasource.driver-class-name=org.postgresql.Driver
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        ```
    -   **Important:** Replace `your_postgres_password` with the actual password you set for your `postgres` superuser during PostgreSQL installation.

### Database Setup

1.  **Start PostgreSQL Server:** Ensure your PostgreSQL server is running. You can check its status via Windows Services (`postgresql-x64-17` service).
2.  **Create Database:**
    -   Open **pgAdmin 4**.
    -   Connect to your PostgreSQL 17 server (if prompted, use `postgres` username and your password, default port 5432).
    -   In the Object Explorer, right-click on the `postgres` database, select `Query Tool`.
    -   Execute the following SQL to create the database:
        ```sql
        CREATE DATABASE supplytrack_db
            WITH
            OWNER = postgres
            ENCODING = 'UTF8';
        ```
    -   Right-click on `Databases` node and `Refresh` to confirm `supplytrack_db` appears.
3.  **Create Tables:** The `spring.jpa.hibernate.ddl-auto=update` setting *should* automatically create the `users`, `products`, and `events` tables when the Spring Boot application starts for the first time with an empty database. However, if they don't appear (which can happen occasionally), you can manually create them via pgAdmin's `Query Tool` for `supplytrack_db`:
    -   **`users` table:**
        ```sql
        CREATE TABLE users (
            id BIGSERIAL PRIMARY KEY,
            username VARCHAR(255) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            role VARCHAR(50) NOT NULL
        );
        ```
    -   **`products` table:**
        ```sql
        CREATE TABLE products (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            origin VARCHAR(255) NOT NULL,
            current_status VARCHAR(50) NOT NULL,
            current_location VARCHAR(255) NOT NULL,
            created_date TIMESTAMP NOT NULL,
            owner_user_id BIGINT NOT NULL
        );
        ```
    -   **`events` table:**
        ```sql
        CREATE TABLE events (
            id BIGSERIAL PRIMARY KEY,
            product_id BIGINT NOT NULL,
            event_type VARCHAR(50) NOT NULL,
            event_description VARCHAR(255),
            timestamp TIMESTAMP NOT NULL,
            location VARCHAR(255) NOT NULL,
            actor_user_id BIGINT NOT NULL
        );
        ```
    -   Execute each `CREATE TABLE` statement separately. Refresh the `Tables` node under `supplytrack_db` -> `Schemas` -> `public` to verify their creation.

### Running the Backend

1.  **Open in IntelliJ IDEA:**
    -   Open IntelliJ IDEA.
    -   Select `File` -> `Open` and navigate to your `supplytrack-backend` folder.
    -   Allow Maven to import dependencies (reload if necessary in Maven tool window).
2.  **Run the Application:**
    -   Open `src/main/java/com/supplytrack/SupplytrackApplication.java`.
    -   Click the green "Play" (Run) arrow next to `public static void main(String[] args)`.
    -   The application should start and be accessible at `http://localhost:8080`.

### Initial Testing (Postman/cURL)

With the backend running, you can test its various API endpoints:

1.  **Register a User (e.g., FARMER):**
    -   **Endpoint:** `POST` `http://localhost:8080/api/register`
    -   **Body (raw JSON):** `{"username": "farmer1", "password": "pass123", "role": "FARMER"}`
    -   **Expected:** `201 Created`, body: `"User registered successfully!"`
2.  **Register other Roles:** (`DISTRIBUTOR`, `RESTAURANT`).
3.  **Get Protected Data (Login Test):**
    -   **Endpoint:** `GET` `http://localhost:8080/api/protected/data`
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Expected:** `200 OK`, body: `"This is protected data accessible by authenticated users!"`
4.  **Create Product (as FARMER):**
    -   **Endpoint:** `POST` `http://localhost:8080/api/products`
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Body:** `{"name": "Organic Tomatoes", "origin": "Green Valley Farm", "initialLocation": "Farm Barn"}`
    -   **Expected:** `201 Created`, returns product details.
    -   *(Test with `DISTRIBUTOR` or `RESTAURANT` roles for `403 Forbidden` confirmation.)*
5.  **Log Event:**
    -   **Endpoint:** `POST` `http://localhost:8080/api/products/{productId}/log-event` (use an existing product's ID)
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Body:** `{"eventType": "SHIPPED", "eventDescription": "Shipped to warehouse.", "location": "Warehouse A"}`
    -   **Expected:** `201 Created`, returns created event JSON.
6.  **Handover Product:**
    -   Get `distributor1`'s `id` from pgAdmin (`SELECT id FROM users WHERE username = 'distributor1';`).
    -   **Endpoint:** `POST` `http://localhost:8080/api/products/{productId}/handover`
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Body:** `{"newOwnerUserId": <distributor1_id>, "handoverLocation": "Distributor Depot", "handoverDescription": "Product delivered to distributor."}`
    -   **Expected:** `200 OK`, body: `{"message": "Product ... handed over successfully..."}`
    -   *(Test with `RESTAURANT` role for `403 Forbidden` confirmation.)*
7.  **Trace Product:**
    -   **Endpoint:** `GET` `http://localhost:8080/api/products/{productId}/trace`
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Expected:** `200 OK`, returns product details and full event history JSON.
8.  **Get Products for User:**
    -   **Endpoint:** `GET` `http://localhost:8080/api/products`
    -   **Authorization:** Basic Auth (`username: distributor1`, `password: pass123`)
    -   **Expected:** `200 OK`, returns array of products owned by `distributor1`.
9.  **Get QR Code Data:**
    -   **Endpoint:** `GET` `http://localhost:8080/api/products/{productId}/qrcode-data`
    -   **Authorization:** Basic Auth (`username: farmer1`, `password: pass123`)
    -   **Expected:** `200 OK`, body: `{"qrCodeData": "1"}` (where "1" is the product ID).

---

## 6. Role-Based Access Control

The application implements granular role-based access control using Spring Security's `@EnableMethodSecurity` and `@PreAuthorize` annotations. This ensures robust security for specific operations:

-   **`ROLE_FARMER`**: Solely authorized to `createProduct`.
-   **`ROLE_FARMER` / `ROLE_DISTRIBUTOR`**: Authorized to `handoverProduct`.
-   All other authenticated users (including `ROLE_RESTAURANT` and `ROLE_ADMIN`) and actions (like viewing protected data, logging generic events, tracing products, fetching owned products, or getting QR code data) are covered by general authentication rules. Unauthorized attempts to restricted endpoints will result in a `403 Forbidden` response.

## 7. Future Enhancements

-   **JWT (JSON Web Token) Authentication:** Replace HTTP Basic Auth for a more stateless, scalable, and secure token-based authentication mechanism.
-   **Advanced Role-Based UI:** Dynamically show/hide frontend UI elements (buttons, forms, navigation items) based on the logged-in user's specific role.
-   **Product Image Uploads:** Integrate image storage (e.g., AWS S3) for product photos, enhancing visual traceability.
-   **Notifications/Alerts:** Implement a real-time notification system (e.g., using WebSockets) for supply chain events.
-   **Mapping/Geolocation:** Integrate interactive maps to visualize product current locations and historical routes.
-   **Batch Operations:** Develop functionalities for logging events or performing handovers for multiple products simultaneously.
-   **Advanced Search & Filtering:** Implement more robust search, filtering, and sorting capabilities for products and events.
-   **Admin Dashboard:** Create a dedicated administrative interface for user management, system health monitoring, and data oversight.

## 8. Contact

Feel free to connect with me for questions, collaborations, or discussions about this project!

-   GitHub: [VummadiHarsha39](https://github.com/VummadiHarsha39)
-   LinkedIn: [Your LinkedIn Profile URL Here] (https://www.linkedin.com/in/harsha-vardhan-reddy-vummadi-63b464143/)

## 9. License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.












