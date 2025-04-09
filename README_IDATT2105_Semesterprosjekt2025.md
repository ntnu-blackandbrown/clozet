# IDATT2105 - Semester Project 2025

**Project Description:**   

A web-based marketplace platform inspired by finn.no, developed as part of the IDATT2105 Full-stack Application Development course in Spring 2025. 
The platform allows users to buy and sell items through a rich user interface, equipped with features like advanced filtering, full-text search, messaging between users. 
The system supports both standard users and administrators, where the former can list and manage items, while the latter have access to category and user management. 
The frontend is built with Vue 3, while the backend is implemented using Java 21 and Spring Boot 3, with MySQL/H2 as the database engine during development,
which is then migrated to PostReg SQL during deployment with Heroku.

The project emphasizes clean architecture, modern security practices, accessibility, and test coverage, and is delivered with CI/CD pipelines and full documentation.


**Link to application (optional):**  
Insert link here (when available).

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

- - Background of the project

  The digital marketplace is a well-established concept, yet there is always room for improving user experience, security, and functionality. 
  This project aims to recreate and modernize the core concept of a platform like finn.no – allowing users to list, search for, and buy second-hand or new items – while also emphasizing best practices in full-stack development.

  This project was developed in the context of the IDATT2105 course, where students are tasked with creating a complete full-stack web application using modern technologies. 
  The project is also a voluntary assignment for students seeking to improve their course grade from a C to a B or A, and thus requires high quality in all aspects: code, UI/UX, functionality, security, testing, and documentation.

- Main functionalities

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
Comment and edit:

### Database Architecture
- **Development Environment: H2 Database**
  - light-weight in-file database perfect for development and testing
  - No separate installation required
  - Automatic schema creation and fast startup
  - Supports both SQL mode and JPA operations

- **Production Environment: PostgreSQL on Heroku**
  - Enterprise-grade reliability and performance
  - Excellent support for JSON data types
  - Automatic backups and monitoring through Heroku
  - Seamless scaling capabilities

- **Data Access Layer** --- Dobbelsjekk
  - Spring Data JPA for ORM functionality
  - Type-safe queries using JPA Criteria API
  - Prepared statements to prevent SQL injection
  - Custom repository implementations for complex queries
  - Database migration handled by Flyway


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

### Deployment Infrastructure

- **Backend Deployment: Heroku**
  - Automated deployment through Git integration
  - Built-in SSL/TLS security
  - Automatic load balancing
  - Easy environment variable management
  - Integrated logging and monitoring
  - Dyno-based scaling for cost efficiency

- **Frontend Deployment: Netlify**
  - Continuous deployment from Git
  - Automatic build optimization
  - Global CDN distribution
  - Built-in form handling
  - Instant cache invalidation


## Application Structure
Describe the architecture and structure of your project. Include:
- Folders and packages
- Class diagrams and ER diagrams (remember illustrations!)
- Brief explanation of Controller, Service, Repository, DTOs, and Models.



## API Documentation
Provide information on how the API is documented:

- RestDocs - explain how it works and how to use it

## Installation and Running
Step-by-step guide for setup:
- **Prerequisites:** Java version, Node version, MySQL
- **How to clone the repository**
- **How to start the backend**
- **How to start the frontend**
- How to add test data

## Testing and CI/CD
Document how testing and CI/CD are configured:
- Unit testing, integration tests
- Test coverage requirements (at least 50%)
- Links to CI/CD pipelines in GitLab
- Explanation of the deployment process (Azure/Netlify)

- How to run tests locally - manually with EmialProdConfig

## Usage Examples
Describe a typical user scenario:
- Registration
- Login
- Publishing an advertisement
- Contacting seller/buyer
- Transactions using VIPPS

## Project Members
- Names and roles

Kevin Dennis Mazali

- pic

Kaamya Shinde

- pic


---

## Comments for editing (can be deleted later):
- Remember to update all links and information as the project progresses.
- Add illustrations, diagrams, and images where appropriate.
- Thoroughly test instructions before submission.