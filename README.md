# SupplyTrack - A Modular Food Supply Chain Tracking System (Backend)

## Table of Contents
1. [About the Project](#about-the-project)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Architecture](#architecture)
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

`SupplyTrack` is a robust, web-based food supply chain tracking system designed to provide end-to-end traceability for food products, from their origin (farm) to the customer. This backend repository powers the core logic, data management, and secure API endpoints for the entire system.

Built with Spring Boot, it handles complex real-world supply chain scenarios including multi-party interactions (farmers, distributors, restaurants), detailed product lifecycle events, and chain of custody. This project demonstrates a strong understanding of full-stack development principles, enterprise-level architecture, and secure application design.

## 2. Features

This backend API provides the following key functionalities:

### Core Management
- **User Authentication & Authorization:** Secure registration and login (HTTP Basic Auth) with role-based access control (`FARMER`, `DISTRIBUTOR`, `RESTAURANT`).
- **Product Management:** APIs to create new food products with initial details (name, origin, location).
- **Event Logging:** Generic API to log various events (e.g., `SHIPPED`, `RECEIVED`, `QUALITY_CHECK`, `DAMAGED`, `SOLD`) for any product, recording timestamp, location, and the user who performed the action.
- **Product Handover Logic:** Specific functionality to transfer product ownership between authorized parties in the supply chain (e.g., from Farmer to Distributor, Distributor to Restaurant).

### Traceability & Querying
- **Full Lifecycle Traceability:** Retrieve the complete chronological history of any product, detailing every event it underwent from creation to its current state.
- **Dashboard Data:** API to fetch products currently owned by the authenticated user, useful for dashboard overviews.
- **QR Code Data Generation (Backend):** Provides a clean product ID (or a URL to the trace endpoint) to be encoded into a QR code for quick traceability lookup.

## 3. Tech Stack

-   **Backend:**
    -   [Spring Boot](https://spring.io/projects/spring-boot) (v3.5.4)
    -   [Spring Security](https://spring.io/projects/spring-security) (v6.x) for Authentication & Authorization
    -   [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) for Database Interaction
    -   [PostgreSQL](https://www.postgresql.org/) (v17.5) as the Relational Database
    -   [Maven](https://maven.apache.org/) for Dependency Management
    -   Java 17
-   **Database Tools:**
    -   [pgAdmin 4](https://www.pgadmin.org/) (GUI for PostgreSQL)

## 4. Architecture

The backend follows a classic **layered architecture** (Controller -> Service -> Repository) adhering to the principles of separation of concerns:

-   **Controllers (`ProductController`, `UserController`):** Handle incoming HTTP requests, map them to appropriate service methods, and return API responses.
-   **Services (`ProductService`, `EventService`, `CustomUserDetailsService`):** Encapsulate business logic, orchestrate operations across multiple repositories, and manage transactions.
-   **Repositories (`UserRepository`, `ProductRepository`, `EventRepository`):** Interact directly with the PostgreSQL database using Spring Data JDBC, providing CRUD operations and custom query methods.
-   **Security (`SecurityConfig`):** Configures HTTP security rules, CORS, password encoding, and leverages Spring Security's method-level (`@PreAuthorize`) annotations for fine-grained role-based access control.

The backend exposes **RESTful APIs** that are consumed by a separate frontend application.

## 5. Getting Started

Follow these steps to get the SupplyTrack backend up and running on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:
-   **Java Development Kit (JDK) 17 or higher**
-   **Maven** (usually bundled with IDEs like IntelliJ IDEA)
-   **PostgreSQL (v17.5 recommended)**
-   **pgAdmin 4** (for PostgreSQL management)
-   **IntelliJ IDEA (Community Edition)** or another compatible IDE
-   **Postman** or `cURL` for API testing

### Backend Setup

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/](https://github.com/)<YOUR_GITHUB_USERNAME>/supplytrack-backend.git
    cd supplytrack-backend
    ```
2.  **Configure Database Connection:**
    -   Open the `src/main/resources/application.properties` file.
    -   Update the database connection details:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/supplytrack_db
        spring.datasource.username=postgres
        spring.datasource.password=your_postgres_password # REPLACE WITH YOUR PASSWORD (e.g., 'abc')
        spring.datasource.driver-class-name=org.postgresql.Driver
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        ```
    -   **Important:** Replace `your_postgres_password` with the actual password you set for your `postgres` user.

### Database Setup

1.  **Start PostgreSQL Server:** Ensure your PostgreSQL server is running.
2.  **Create Database:**
    -   Open pgAdmin 4.
    -   Connect to your PostgreSQL 17 server (using `postgres` username and your password, default port 5432).
    -   Right-click on the `postgres` database, select `Query Tool`.
    -   Execute the following SQL to create the database:
        ```sql
        CREATE DATABASE supplytrack_db
            WITH
            OWNER = postgres
            ENCODING = 'UTF8';
        ```
    -   Right-click on `Databases` and `Refresh` to confirm `supplytrack_db` appears.
3.  **Create Tables:** Spring Data JDBC (with `ddl-auto=update`) *should* auto-create tables on first run, but if not, use the following:
    -   Open `Query Tool` for `supplytrack_db` in pgAdmin.
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
    -   Execute each `CREATE TABLE` statement. Refresh `Tables` under `public` schema to verify.

### Running the Backend

1.  **Open in IntelliJ IDEA:**
    -   Open IntelliJ IDEA.
    -   Select `File` -> `Open` and navigate to your `supplytrack-backend` folder.
    -   Let Maven import dependencies.
2.  **Run the Application:**
    -   Open `src/main/java/com/supplytrack/SupplytrackApplication.java`.
    -   Click the green "Play" (Run) arrow next to `public static void main(String[] args)`.
    -   The application should start on `http://localhost:8080`.

### Initial Testing (Postman/cURL)

With the backend running, you can test its endpoints:

1.  **Register a User (e.g., FARMER):**
    -   `POST` `http://localhost:8080/api/register`
    -   Body (raw JSON): `{"username": "farmer1", "password": "pass123", "role": "FARMER"}`
    -   Expected: `201 Created`, "User registered successfully!"
2.  **Register other Roles (Distributor, Restaurant):**
    -   `POST` `http://localhost:8080/api/register`
    -   Body: `{"username": "distributor1", "password": "pass123", "role": "DISTRIBUTOR"}`
    -   Body: `{"username": "restaurant1", "password": "pass123", "role": "RESTAURANT"}`
3.  **Get Protected Data (Login Test):**
    -   `GET` `http://localhost:8080/api/protected/data`
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Expected: `200 OK`, "This is protected data accessible by authenticated users!"
4.  **Create Product (as FARMER):**
    -   `POST` `http://localhost:8080/api/products`
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Body: `{"name": "Organic Tomatoes", "origin": "Green Valley Farm", "initialLocation": "Farm Barn"}`
    -   Expected: `201 Created`, returns product details.
    -   *Try as `distributor1` (expected: `403 Forbidden`)*
5.  **Log Event (as FARMER or DISTRIBUTOR):**
    -   `POST` `http://localhost:8080/api/products/1/log-event` (use your product's ID)
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Body: `{"eventType": "SHIPPED", "eventDescription": "Shipped to warehouse.", "location": "Warehouse A"}`
    -   Expected: `201 Created`, returns event details.
6.  **Handover Product (as FARMER or DISTRIBUTOR):**
    -   Get `distributor1`'s `id` from pgAdmin (`SELECT * FROM users;`). Let's say it's `3`.
    -   `POST` `http://localhost:8080/api/products/1/handover` (use your product's ID)
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Body: `{"newOwnerUserId": 3, "handoverLocation": "Distributor Depot", "handoverDescription": "Product delivered to distributor."}`
    -   Expected: `200 OK`, message about handover success.
    -   *Try as `restaurant1` (expected: `403 Forbidden`)*
7.  **Trace Product:**
    -   `GET` `http://localhost:8080/api/products/1/trace` (use your product's ID)
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Expected: `200 OK`, returns product details and event history.
8.  **Get Products for User:**
    -   `GET` `http://localhost:8080/api/products`
    -   Authorization: Basic Auth (`username: distributor1`, `password: pass123`)
    -   Expected: `200 OK`, returns array including the product `distributor1` owns.
9.  **Get QR Code Data:**
    -   `GET` `http://localhost:8080/api/products/1/qrcode-data` (use your product's ID)
    -   Authorization: Basic Auth (`username: farmer1`, `password: pass123`)
    -   Expected: `200 OK`, returns `{"qrCodeData": "1"}` (just the ID).

---

## 6. Role-Based Access Control

The application implements granular role-based access control using Spring Security's `@PreAuthorize` annotations:

-   **`ROLE_FARMER`**: Authorized to `createProduct`.
-   **`ROLE_FARMER` / `ROLE_DISTRIBUTOR`**: Authorized to `handoverProduct`.
-   All other authenticated users (including `ROLE_RESTAURANT`, `ROLE_ADMIN`, and the above roles) can view product details, log generic events (e.g., `RECEIVED`, `SOLD`), trace products, and get QR code data. Unauthorized actions will result in a `403 Forbidden` response.

## 7. Future Enhancements

-   **JWT (JSON Web Token) Authentication:** Replace HTTP Basic Auth with JWT for more secure and scalable session management.
-   **Advanced Role-Based UI:** Dynamically show/hide frontend UI elements (buttons, forms) based on the logged-in user's role.
-   **Product Image Uploads:** Integrate image storage (e.g., AWS S3) for product photos.
-   **Notifications/Alerts:** Implement a system for real-time updates (e.g., product received notification).
-   **Mapping/Geolocation:** Integrate maps to visualize product locations and routes.
-   **Batch Operations:** Allow logging events or handovers for multiple products at once.
-   **Search & Filtering:** Add more robust search and filtering capabilities for products and events.
-   **Admin Dashboard:** Dedicated admin interface for user management and overall system oversight.

## 8. Contact

Feel free to connect with me for questions or collaborations!

-   GitHub: [\<YOUR_GITHUB_USERNAME>](https://github.com/<YOUR_GITHUB_USERNAME>)
-   LinkedIn: [\<YOUR_LINKEDIN_PROFILE>](https://www.linkedin.com/in/<YOUR_LINKEDIN_PROFILE>)

## 9. License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.













