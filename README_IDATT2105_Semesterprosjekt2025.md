# IDATT2105 - Semester Project 2025

## 📌 Project Description

A modern full-stack web-based marketplace inspired by Finn.no, developed for the IDATT2105 Full-stack Application Development course (Spring 2025). 
The platform allows users to securely buy and sell second-hand or new items, and includes features like:

- 🔍 Advanced search and filtering
- 💬 Real-time messaging
- 🔐 JWT-based authentication
- 🛠️ Admin dashboard

Built with **Vue 3** (frontend) and **Spring Boot 3** (backend), deployed to **Netlify** and **Heroku** with PostgreSQL in production.

The system emphasizes clean architecture, security (OWASP compliance), accessibility, RESTful design, test coverage, and CI/CD integration.



**Live demo:** [https://your-link.netlify.app](https://your-link.netlify.app)

## Table of Contents
1. [Introduction](#introduction)
2. [Technology Choices and Justifications](#technology-choices-and-justifications)
3. [Application Structure](#application-structure)
4. [Security and Authentication](#security-and-authentication)
5. [API Documentation](#api-documentation)
6. [Installation and Running](#installation-and-running)
7. [Testing and CI/CD](#testing-and-cicd)
8. [Usage Examples](#usage-examples)
9. [Project Members](#project-members)

## Introduction
Explain what the project is about and what it aims to solve. Consider including:

- Background of the project

This project was developed as a semester project in IDATT2105. The goal is to recreate the core concept of a digital marketplace with modern tools and best practices.

Students participating are graded on their implementation of:
- Clean and maintainable code
- Functional and responsive UI
- Secure backend with authentication and validation
- High test coverage and robust CI/CD


## Core Functionalities

### User Management & Authentication
- 👤 User registration and profile management
- 🔐 Secure login with JWT and Spring Security
- 👥 Role-based access control (Admin/Standard users)
- 🌐 Internationalization (i18n) support

### Listing Management
- 📝 Create, update, delete, and archive item listings
- 📸 Multi-image gallery support for listings
- 📱 Responsive thumbnail grid for item exploration

### Search & Discovery
- 🔍 Advanced filtering system
  - Category-based filtering
  - Location-based search
  - Keyword search
  - Date range filtering
- 📊 Smart sorting options
- 🗺️ Interactive map view for geographical browsing

### User Interaction
- 💬 Real-time messaging system between buyers and sellers
- ❤️ Favorites/Bookmarking system for items
- 💳 VIPPS payment simulation for transactions
- 📨 Email notifications for important updates

### Admin Features
- 🎛️ Comprehensive admin dashboard
- 📁 Category management system
- 👥 User management and moderation
- 📊 Basic analytics and reporting

### Technical Features
- 🔒 Secure authentication with JWT?
- 📱 Mobile-responsive design
- 🔄 CI/CD pipeline with >50% test coverage
- 📚 REST API with RestDocs documentation
- 🛡️ OWASP security compliance

## Technology Choices and Justifications

### Database Architecture

- **Development Environment: H2 Database**
  - In-memory/in-file database ideal for rapid prototyping
  - Fast startup and auto-schema generation

- **Production Environment: PostgreSQL on Heroku**
  - Chosen for its scalability and JSON support
  - Integrated with Heroku for managed backups and monitoring

- **Data Access Layer** 
  - Spring Data JPA for ORM functionality
  - Type-safe queries using JPA Criteria API
  - Prepared statements to prevent SQL injection
  - Custom repository implementations for complex queries

  ### Security Architecture

- **Authentication**
  - JWT-based stateless authentication
  - Refresh token rotation
  - Secure token storage in HttpOnly cookies
  - Role-based access control (RBAC)

- **API Security**
  - CORS configuration with specific origins
  - CSRF protection for state-changing operations
  - Rate limiting for API endpoints
  - Request validation using DTOs

- **Data Protection**
  - Password hashing with BCrypt
  - Input sanitization
  - XSS protection headers
  - Content Security Policy (CSP)
  - Secure file upload handling


  ### Real-time Communication

- **WebSocket Implementation**
  - STOMP protocol over WebSocket
  - SockJS fallback for legacy browsers
  - Message broker for scalable communication
  - Real-time updates for: --- dobbelsjekk
    - Chat messages
    - Listing status changes
    - Price updates
    - Notification delivery

## Application Structure

### Backend Directory
```
backend/
├── category/
├── user/
├── item/
├── message/
├── transaction/
├── review/
├── favorite/
├── location/
├── itemimage/
├── shippingoption/
├── common/
```
Each domain includes: `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `mapper/`, `exception/`

### Frontend Directory
```
frontend/
├── api/
├── assets/
├── components/
├── router/
├── stores/
├── types/
├── utils/
├── views/
├── websocket/
└── __tests__/
```

### Key Architectural Decisions
1. **Backend**
   - Spring Boot 3.4.4 with Java 21
   - JPA for data access
   - H2 for development, PostgreSQL for production
   - Cloudinary for image management
   - REST API with Spring REST Docs
   - WebSocket with SockJS/STOMP
   - JWT for authentication

2. **Frontend**
   - Vue 3.5.13 with Composition API
   - Vite 6.2.5 for build tooling
   - TypeScript 5.8 for type safety
   - Pinia for state management
   - Vue Router for navigation
   - SockJS/StompJS for WebSocket
   - Vee-validate/Yup for form validation

3. **Testing**
   - Controller tests with Spring REST Docs
   - Service layer unit tests
   - Frontend component tests with Vitest
   - E2E tests with Cypress

### Architecture Diagrams - fiks senere


### Database Entity Relationship Diagram
The following diagram illustrates the database model of the application, showing all entities and their relationships:

![E-commerce Platform Data Model](docs/images/database_diagram.png)

This entity relationship diagram shows the core entities of our application:
- **Users**: Central entity storing user information including authentication details
- **Items**: Products listed for sale with their detailed attributes
- **Categories**: Hierarchical classification system for items
- **Transactions**: Records of sales between users
- **Messages**: Communication between users about specific items
- **Locations**: Geographic data for items and shipping
- **Shipping Options**: Available shipping methods and their details
- **Item Images**: Photos associated with listings
- **Verification/Password Tokens**: Security tokens for account management

The diagram illustrates both the entity attributes and the relationships between them, including foreign key constraints and cardinality.

## API Documentation

Our API documentation is automatically generated using Spring REST Docs, which ensures that the documentation stays in sync with the actual code through test-driven documentation.

### Implementation Overview

1. **Test-Driven Documentation**
   - Documentation is generated through controller tests
   - Each endpoint's documentation is verified during test execution
   - Examples are automatically generated from actual request/response pairs

2. **Documentation Structure**
   ```
   backend/
   ├── src/test/java/
   │   └── .../*ControllerTest.java    # Test files that generate docs
   ├── target/generated-snippets/      # Generated documentation snippets
   └── index.adoc                      # Main documentation file
   |___index.pdf                       # PDF of documentation
  
   ```

3. **Example Documentation Test**
   ```java
   @Test
   void getTopFiveCategories_ShouldReturnListOfCategories() throws Exception {
       mockMvc.perform(get("/api/categories/top-five"))
           .andDo(document("categories-top-five",
               responseFields(
                   fieldWithPath("[].id").description("Category ID"),
                   fieldWithPath("[].name").description("Category name"),
                   // ... other fields
               )
           ));
   }
   ```

### Documentation Generation Process

1. **Test Execution**: Run tests
   ```bash
   # Run from project root
   cd backend
   mvn test
   ```

2. **Snippet Generation**: Tests generate documentation snippets in `target/generated-snippets/`

3. **Index Generation**: Python script combines snippets into final documentation
   ```bash
   # Run from project root
   python docs/API\ Documentation/generate_index.py --snippets-dir backend/target/generated-snippets --output docs/API\ Documentation/index.adoc
   ```
### Documentation Content
- Request/response formats with examples
- Detailed field descriptions and types
- Sample request/response payloads
- HTTP status codes and their meanings
- Authentication requirements per endpoint

### Accessing Documentation

The documentation can be accessed through the index.pdf file within the docs folder

## Installation and Running

### Terminal Setup
For the most effective execution of commands in this README, use Git Bash terminal:

1. Open Git Bash terminal in the project root folder
2. All commands in this documentation are designed to be run from Git Bash
3. When using IDE run configurations, make sure to set Git Bash as the terminal type
4. If using Windows Command Prompt or PowerShell, some commands may need adjustment
5. **Important:** Use separate Git Bash terminals for each run configuration (backend, frontend, testing, etc.) as each will need to remain active during development

### Prerequisites
- Java JDK 21
- Node.js v18+
- MySQL 8.0
- Maven 3.8+

### Quick Start

After cloning the project

> **Note:** All commands below should be run in Git Bash terminal for consistent execution across environments.

> **Important:** Open a new Git Bash terminal for each component (backend, frontend) as they need to run simultaneously.

1. **Backend Setup**
   ```bash
   # Run from project root
   cd backend
   
   # Production mode
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```
   Backend will run on `http://localhost:8080`

2. **Frontend Setup**
   ```bash
   # Run from project root in a NEW Git Bash terminal
   cd frontend
   npm install
   npm run dev
   ```
   Frontend will be available on `http://localhost:5173`

The application should now be running and accessible through your browser.

## Testing and CI/CD

### Testing Strategy

#### Backend Testing
- **Unit Tests** (JUnit 5): Service layers, repositories, utility classes
- **Integration Tests** (Spring Test): REST endpoints, database, security
- **WebSocket Tests**: Real-time communication, connection handling, event delivery

#### Frontend Testing
- **Unit Tests** (Vitest): Components, stores, utilities
- **WebSocket Tests**: Client connection, messaging, subscription management
- **E2E Tests** (Cypress): User flows, integration scenarios, cross-browser support

### Continuous Integration/Deployment

#### Backend CI/CD Pipeline (.github/workflows/backend.yml)
1. **Build**
   - Java 21 setup with Temurin distribution
   - Maven build and compilation
   - Artifact preservation
2. **Package**
   - JAR packaging
   - Artifact upload
3. **Deployment**
   - Automatic deployment to Heroku
   - Environment configuration
   - Database migration handling


   link: 

#### Frontend CI/CD Pipeline (.github/workflows/frontend.yml)
1. **Build & Quality**
   - Node.js 18 setup
   - Dependency installation
   - Code formatting (Prettier)
   - Type checking
2. **Testing**
   - Unit test execution with Vitest
   - Build artifact preservation
3. **Deployment**
   - Netlify deployment
   - Production build optimization

   link: 

### Local Testing

#### Backend Testing
```bash
# Run from project root
cd backend

# Run all tests
mvn test
```

#### Frontend Testing
```bash
# Run from project root
cd frontend

# Run unit tests
npm run test:unit

# Run E2E tests
npm run test:e2e

```

#### WebSocket Testing
```bash
# Run from project root
cd backend
# Run WebSocket integration tests
mvn test -Dtest=WebSocketIntegrationTest

# Run from project root
cd frontend
# Run WebSocket client tests
npm run test:websocket
```

#### Manual WebSocket Testing
To test WebSocket functionality using the HTML test client:

> **Note:** This requires multiple Git Bash terminals - one for the backend server and another for opening the test client.

1. First, start the backend server in production mode:
   ```bash
   # Run from project root
   cd backend
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

2. Once the backend server is running, open the WebSocket test HTML file in your browser:
   ```bash
   # Run in a NEW Git Bash terminal while the backend is running
   # Open the file in your default browser
   # Windows
   start backend/src/test/java/stud/ntnu/no/backend/message/websocket/websocket-test.html
   
   # macOS
   open backend/src/test/java/stud/ntnu/no/backend/message/websocket/websocket-test.html
   ```

3. In the WebSocket test client:
   - Verify the server URL is set to `http://localhost:8080/ws`
   - Click "Connect" to establish a WebSocket connection
   - Send test messages between users
   - Monitor real-time WebSocket communication

For detailed information on WebSocket testing, refer to the [WebSocket Testing Guide](websocket%20testing.pdf) in the project root.


## Project Members
- Names and roles

Kevin Dennis Mazali

- pic

Kaamya Shinde

- pic
