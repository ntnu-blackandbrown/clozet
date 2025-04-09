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

The core functionalities of the platform include:

User registration and authentication, including role-based access control (admin vs. normal users)

Item listing, including add/update/delete/archive operations

Filtering and full-text search based on category, location, keyword, date, etc.

Favorites/bookmarking of items

Messaging system between users (e.g., buyer contacting seller)

Item detail pages with image galleries and VIPPS-based payment simulation.

Admin interface for managing categories and performing elevated operations

Map view and responsive thumbnail grid for item exploration

Mobile-friendly design with internationalization (i18n) support

Secure login with JWT and Spring Security

Continuous Integration and Delivery (CI/CD) setup with test coverage > 50%

REST API with Swagger documentation


- Overall requirements

he project adheres to the following technical and documentation requirements:

Frontend: Vue.js 3 with clean CSS (no CSS frameworks like Tailwind)

Backend: Spring Boot 3 with Java 17, 21, or 24; REST-based API

Database: MySQL 8 and/or H2

Security: JWT-based authentication and secure access controls

Testing: Minimum 50% code coverage; test data and test users must be included

CI/CD: Automated build, test, and deployment workflows

Documentation: Complete API documentation with Swagger, system architecture diagrams, and README for local setup

Accessibility & Security: Follows OWASP and universal design principles

Submission: Delivered as a zip file with runnable code, documentation, test data, and a 15–20 minute demo video

The project encourages creativity in design and implementation, and groups are advised to prioritize features realistically, ensuring completed parts are fully functional and of high quality.

## Technology Choices and Justifications
Comment and edit:


- **Database:** MySQL/H2 + , explain the reasons and advantages of this combination. - ORM, JPA and SQL-injections
// TODO: other things to justify
-  Netlify
-  Heroku

- Websocket implementation
- Spring security setup—main topics, the token setup, HTTP and XSS defence



## Application Structure
Describe the architecture and structure of your project. Include:
- Folders and packages
- Class diagrams and ER diagrams (remember illustrations!)
- Brief explanation of Controller, Service, Repository, DTOs, and Models.

## Security and Authentication
Describe how security is handled:
- JWT authentication
- Refresh Tokens
- Authorization levels (user/admin)
- Password encryption

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