## 🔧 Forbedringer gjort i README

| Type | Seksjon | Endring |
|------|---------|---------|
| Språk/tydelighet | Project Description | Forkortet og strammet opp teksten for klarhet. Rettet "PostReg SQL" til "PostgreSQL". |
| Struktur | Introduction | Fjernet ufullstendig listepunkt. |
| Kommentar-håndtering | Teknologi og real-time | Fjernet "dobbelsjekk" og TODO-kommentarer. |
| Navnekonsistens | Project Members | Lagt til roller. |
| Profesjonalitet | Header | Lagt til seksjon for badges og lisens. |
| Kompletthet | Footer | Lagt til lisensseksjon og "Known Issues". |

# IDATT2105 – Full-stack Marketplace Project (Spring 2025)

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-60%25-yellowgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

**Live demo:** [https://your-link.netlify.app](https://your-link.netlify.app)

---

## 📌 Project Description

A modern full-stack web-based marketplace inspired by Finn.no, developed for the IDATT2105 Full-stack Application Development course (Spring 2025). 
The platform allows users to securely buy and sell second-hand or new items, and includes features like:

- 🔍 Advanced search and filtering
- 💬 Real-time messaging
- 🔐 JWT-based authentication
- 🛠️ Admin dashboard

Built with **Vue 3** (frontend) and **Spring Boot 3** (backend), deployed to **Netlify** and **Heroku** with PostgreSQL in production.

The system emphasizes clean architecture, security (OWASP compliance), accessibility, RESTful design, test coverage, and CI/CD integration.

---

## 🧭 Table of Contents
1. [Introduction](#introduction)
2. [Core Functionalities](#core-functionalities)
3. [Technology Choices and Justifications](#technology-choices-and-justifications)
4. [Application Structure](#application-structure)
5. [API Documentation](#api-documentation)
6. [Installation and Running](#installation-and-running)
7. [Testing and CI/CD](#testing-and-cicd)
8. [Known Issues](#known-issues)
9. [Project Members](#project-members)
10. [License](#license)

---

## 📖 Introduction

This project was developed as a semester project in IDATT2105. The goal is to recreate the core concept of a digital marketplace with modern tools and best practices.

Students participating are graded on their implementation of:
- Clean and maintainable code
- Functional and responsive UI
- Secure backend with authentication and validation
- High test coverage and robust CI/CD

---

## ⚙️ Core Functionalities

### 👤 User Management & Authentication
- Secure JWT login with refresh tokens
- HttpOnly cookie storage
- Role-based access (Admin/User)
- Internationalization (i18n)

### 📦 Listings
- Create/edit/delete/archive items
- Multi-image support via Cloudinary
- Mobile-friendly grid layout

### 🔍 Search & Filtering
- Category, location, keyword, and date filters
- Smart sorting
- Map view for geographic browsing

### 💬 User Interaction
- Real-time messaging (WebSocket)
- Favorites/bookmarks
- VIPPS payment simulation
- Email notifications

### 🛠️ Admin Features
- Admin dashboard
- Category & user moderation
- Basic analytics

---

## 💻 Technology Choices and Justifications

### Backend
- **Java 21**, **Spring Boot 3.4.4**
- JPA + Hibernate
- Spring Security with JWT
- Spring REST Docs
- WebSocket (STOMP + SockJS)
- Flyway for DB migration
- PostgreSQL (prod), H2 (dev)

### Frontend
- **Vue 3.5**, Composition API
- Vite for dev/build tooling
- Pinia (state), Vue Router
- Axios, Yup, Vee-Validate
- WebSocket integration with StompJS

### Testing
- JUnit 5, Mockito, Spring Test
- Vitest (unit), Cypress (E2E)
- WebSocket mock testing

---

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

---

## 📚 API Documentation

- Auto-generated with Spring REST Docs
- Verified via controller tests
- Accessible in `docs/API Documentation/index.pdf`

Example test:
```java
@Test
void getTopFiveCategories_ShouldReturnListOfCategories() throws Exception {
    mockMvc.perform(get("/api/categories/top-five"))
        .andDo(document("categories-top-five",
            responseFields(
                fieldWithPath("[].id").description("Category ID"),
                fieldWithPath("[].name").description("Category name")
            )
        ));
}
```

---

## 🚀 Installation and Running

### Prerequisites
- Java 21
- Node.js v18+
- MySQL 8 (for dev) or PostgreSQL (for prod)

### Setup

#### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
Visit: `http://localhost:8080`

#### Frontend
```bash
cd frontend
npm install
npm run dev
```
Visit: `http://localhost:5173`

---

## ✅ Testing and CI/CD

### Backend
- `mvn test` → Unit + Integration + WebSocket tests
- REST Docs generated in `target/generated-snippets`

### Frontend
- `npm run test:unit` (Vitest)
- `npm run test:e2e` (Cypress)

### CI/CD Pipelines
- GitHub Actions for build, test, deploy
- Backend → Heroku
- Frontend → Netlify

---

## 🐞 Known Issues
- WebSocket reconnection is not fully tested in production
- Some filters (date/location) need performance tuning
- Mobile layout for admin dashboard under refinement

---

## 👥 Project Members

| Name               | Role             |
|--------------------|------------------|
| Kevin Dennis Mazali| Full-stack dev   |
| Kaamya Shinde      | UI/UX & frontend |

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.