# Content Management System (Backend)

A Spring Boot backend for managing articles with user authentication, supporting create, view, update, delete, and paginated list operations. Tracks 'recently viewed' articles per user (in-memory, not persisted). Fully dockerized with MySQL.

---

## Implementation Summary

### Core Funtionalities
- [x] All functionality exposed over RESTful APIs
- [x] Data is persistent in a MySQL database (via JPA/Hibernate)
- [x] 'Recently viewed' feature uses only basic collections/primitives (in-memory, not persisted)
- [x] Dockerfile provided to containerize the service
- [x] docker-compose.yml to run the full stack (separate DB container)


### Additional Functionalities
- [x] Authentication: Simple in-memory token-based auth so each user only sees their own articles and recent views
- [x] Pagination for list articles API
- [x] Unit tests for service layer (see `src/test/java/com/example/crud_assignment/service/`)

---

## Unit Test Coverage

- `ArticleServiceTest`: Tests article creation and listing logic
- `UserServiceTest`: Tests user registration and login logic

To run tests:
```bash
mvn test
```

---

## Features
- User registration and login (in-memory token-based auth)
- RESTful CRUD API for articles (per-user access)
- Paginated article listing
- In-memory 'recently viewed' tracking per user (max 5, resets on restart)
- MySQL database persistence
- Dockerized for easy local or production deployment

---

## Project Structure

- `CrudAssignmentApplication.java` — Main Spring Boot entrypoint
- `model/` — JPA entities: `User`, `Article`
- `repository/` — Spring Data JPA repositories
- `service/` — Business logic interfaces and implementations
- `controller/` — REST API endpoints
- `Dockerfile`, `docker-compose.yml` — Containerization and orchestration

---

## Requirements
- Java 17+
- Maven
- Docker & Docker Compose

---

## Running Locally (without Docker)

1. **Configure DB**: Edit `src/main/resources/application.properties` if needed.
2. **Build and run:**
   ```bash
   mvn clean package
   java -jar target/crud_assignment-0.0.1-SNAPSHOT.jar
   ```

---

## Running with Docker Compose

1. **Start everything (no need to pre-build):**
   ```sh
   docker-compose up --build
   ```
   This will:
   - Build the app JAR inside Docker
   - Start MySQL and the app, waiting for DB readiness

2. **If you encounter a port conflict error (e.g., port 3306 is in use):**
   - Change the MySQL port mapping in `docker-compose.yml` from `3306:3306` to `3307:3306` (or another free port).
   - Make sure no local MySQL service is running on port 3306. You can check with:
     ```sh
     netstat -ano | findstr :3306
     ```
   - Stop the local MySQL service if needed (via Windows Services app).

3. **Check container status:**
   - Use Docker Desktop to see if both containers (`crud_assignment_app` and `article_db`) are running.
   - Or run:
     ```sh
     docker ps -a
     ```

4. **View logs for troubleshooting:**
   - For the app:
     ```sh
     docker logs crud_assignment_app
     ```
   - For the database:
     ```sh
     docker logs article_db
     ```

5. **If MySQL container exits immediately:**
   - Run only the DB to check logs:
     ```sh
     docker-compose up db
     ```
   - If it stays running, open a new terminal and start the app:
     ```sh
     docker-compose up app
     ```

6. **Stop all containers:**
   - Press `Ctrl+C` in the terminal running Docker Compose.
   - Or run:
     ```sh
     docker-compose down -v
     ```

7. **Access the API:**
   - App: [http://localhost:8080](http://localhost:8080)
   - MySQL: `localhost:3307`, DB: `article_db`, user: `root`, password: `12@rusellll`

8. **Common issues and solutions:**
   - If the app cannot connect to the DB, make sure MySQL is running and ready for connections (check logs for `ready for connections`).
   - Ensure the app uses `jdbc:mysql://db:3306/article_db` as the datasource URL (not `localhost`).
   - If you see `UnknownHostException: db`, make sure you are running the app inside Docker Compose, not from your IDE.

---

## API Endpoints

### User
- `POST /api/users/register` — Register a new user
  - Body: `{ "username": "...", "password": "..." }`
- `POST /api/users/login` — Login, returns token
  - Body: `{ "username": "...", "password": "..." }`
  - Response: token string (use as `token` header for article endpoints)

### Article (all require `token` header)
- `POST   /api/articles`         — Create article
- `GET    /api/articles/{id}`    — Get article by ID
- `GET    /api/articles/recent`  — Get recently viewed article IDs for user
- `GET    /api/articles`         — List all articles (paginated, user-specific)
  - Query params: `page`, `size`
- `PUT    /api/articles/{id}`    — Update article (must own)
- `DELETE /api/articles/{id}`    — Delete article (must own)

---

## Authentication
- Register and login to receive a token
- Pass the token as a `token` header for all article endpoints
- Token management is in-memory (resets on restart)

---

## Data Model

### User
- `id` (Long, auto-generated)
- `username` (String, unique)
- `password` (String)

### Article
- `id` (Long, auto-generated)
- `title` (String)
- `content` (String)
- `author` (String)
- `userId` (Long, owner)

