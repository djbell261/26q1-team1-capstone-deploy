# 26q1-team1-capstone
# AI-Powered Interview Prep Platform

## Overview

The AI-Powered Interview Prep Platform is a full-stack application designed to help aspiring software engineers prepare for technical interviews through coding challenges, behavioral practice, and AI-driven feedback.

Unlike traditional practice platforms, this system provides:
- Real-time AI evaluation of submissions  
- Structured feedback for improvement  
- Performance tracking over time  
- Personalized recommendations based on weaknesses  

---

## Problem Statement

Early-career developers often practice interview questions without receiving structured feedback. This leads to repeated mistakes and missed opportunities.

This platform solves that by:
- Evaluating submissions with AI  
- Identifying weak areas  
- Guiding users toward targeted improvement  

---

## Key Features

### Authentication & Security
- JWT-based authentication  
- Role-based access (USER, ADMIN)  
- Secure endpoints with authorization  

---

### Coding Interview Practice
- Coding challenges imported via external API  
- Timed assessment sessions  
- Code submission endpoint  
- AI evaluates:
  - Correctness  
  - Time complexity  
  - Code quality  
  - Edge case handling  

---

### Behavioral Interview Practice
- Library of behavioral questions  
- Users submit written responses  
- AI evaluates using STAR method:
  - Situation  
  - Task  
  - Action  
  - Result  

---

### AI Feedback System
Each submission generates structured feedback:

 
{
  "aiScore": 7.0,
  "summary": "...",
  "strengths": "...",
  "weaknesses": "...",
  "recommendations": "..."
}
---

## Performance Tracking

Tracks user performance across both domains:

- Average coding score  
- Average behavioral score  
- Overall performance score  
- Coding performance by difficulty  
- Recent submissions  
- Weak areas detection  

---

## Recommendation Engine

Generates personalized recommendations based on:

- AI feedback analysis  
- Performance metrics  
- Weakness categorization  

Examples:

- Improve time complexity  
- Handle edge cases better  
- Strengthen STAR structure  
- Highlight measurable impact  

---

## System Architecture

### Backend (Spring Boot)

- RESTful API  
- PostgreSQL database  
- OpenAI integration for AI evaluation  
- Layered architecture:  
  Controller → Service → Repository → Entity  

### Frontend (React + Vite)

- Dashboard with analytics  
- Recommendation display  
- Submission tracking  
- Authentication flow  

---

## Core Workflow

### Coding Flow

 
User submits code
→ Stored in database
→ AI evaluates
→ Feedback generated
→ Score assigned
→ Performance updated
→ Recommendations updated

### Behavioral Flow

User submits response
→ Stored in database
→ AI evaluates (STAR method)
→ Feedback generated
→ Score assigned
→ Performance updated
→ Recommendations updated


---

## API Endpoints

### Authentication
- POST /api/auth/register  
- POST /api/auth/login  

### Coding
- GET /api/coding-challenges  
- POST /api/coding-submissions  
- GET /api/coding-submissions/me  

### Behavioral
- GET /api/behavioral-questions  
- POST /api/behavioral-submissions  
- GET /api/behavioral-submissions/me  

### Performance
- GET /api/performance/me  

### Recommendations
- POST /api/recommendations/generate/me  
- GET /api/recommendations/me  

### Sessions
- POST /api/sessions  
- GET /api/sessions/me  

---

## Tech Stack

### Backend
- Java 17+  
- Spring Boot  
- Spring Security (JWT)  
- Spring Data JPA (Hibernate)  
- PostgreSQL  
- OpenAI Java SDK  

### Frontend
- React  
- Vite  
- Axios  

---

## Project Structure

backend/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── mapper/
├── security/
├── config/
└── util/

frontend/
├── pages/
├── api/
├── components/
└── context/


---

## Testing

- Backend tested using Postman  
- Full API flow verified:
  - Authentication  
  - Coding submission → AI → feedback  
  - Behavioral submission → AI → feedback  
  - Performance tracking  
  - Recommendation generation  

---

## Setup Instructions

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run

### Configure

- PostgreSQL database  
- OpenAI API key in `application.yml`  

---

## Frontend

```bash
cd frontend
npm install
npm run dev

## Current Status

### Completed
- Full backend system (production-level logic)
- AI integration for coding and behavioral evaluation
- Performance tracking system
- Recommendation engine with weakness parsing
- Frontend dashboard connected to backend APIs

### In Progress / Future Improvements
- Chart visualization (performance over time)
- Coding challenge UI (editor + submission)
- Behavioral practice UI
- Enhanced feedback viewer UI
- Mobile responsiveness
- Deployment (AWS / Docker)

---

## Screenshots

(Add screenshots of dashboard, recommendations, submissions here)

---

## What I Learned

- Designing scalable backend architectures with Spring Boot
- Integrating AI into real-world applications
- Building a recommendation engine from user data
- Handling authentication and security with JWT
- Connecting full-stack systems end-to-end

---

## Why This Project Matters

This project goes beyond basic CRUD applications by:

- Using AI for real evaluation
- Tracking long-term performance
- Generating intelligent recommendations
- Simulating real interview environments

---

## Team

- Backend Engineering: Derwin 
- Frontend Engineering: Henrrietta
- Scrum Master, TDD: Jamiir

---

## License

This project is for educational and portfolio use.
