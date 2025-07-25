# Content Management System (Backend)

A Spring Boot backend for managing articles with user authentication, supporting create, view, update, delete, and paginated list operations. Tracks 'recently viewed' articles per user (in-memory, not persisted). Fully dockerized with MySQL.

---

## Implementation Summary

### Core Functionalities
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
  - **Note:** Pagination is now handled at the database level and returns only the current user's articles, correctly paginated. For example, `GET /api/articles?page=1&size=5` returns the second page of 5 articles belonging to the authenticated user.
  - **Example response:**
    ```json
    {
      "content": [
        { "id": 6, "title": "...", "content": "...", "author": "...", "userId": 2 },
        { "id": 7, "title": "...", "content": "...", "author": "...", "userId": 2 }
      ],
      "pageable": { ... },
      "totalPages": 3,
      "totalElements": 13,
      "last": false,
      "size": 5,
      "number": 1,
      ...
    }
    ```
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

---

## API Usage & Testing

> **Note:** You only need to register a user once. After registration, you can log in with your credentials and access the API any number of times. Registration is a one-time action per username; login can be repeated as needed to obtain a token for authenticated requests.

### How Pagination Works
- The `GET /api/articles` endpoint supports pagination using `page` and `size` query parameters.
- Example: `GET /api/articles?page=0&size=3` returns the first page with 3 articles for the authenticated user.
- If you omit `size`, it defaults to 10. If you omit `page`, it defaults to 0 (the first page).
- **Do not use duplicate query parameters** (e.g., `?page=0&page=3`). Only the last value will be used.

### How 'Recently Viewed' Articles Work
- The `GET /api/articles/recent` endpoint returns the IDs of the last 5 articles you have viewed (using the detail endpoint).
- Only articles accessed via `GET /api/articles/{id}` are tracked as 'recently viewed'.
- If you have viewed fewer than 5 articles, only those will be shown.
- The list is in most-recent-first order and is user-specific.
- Example usage:
  1. View articles: `GET /api/articles/10`, `GET /api/articles/12`, ...
  2. Check recent: `GET /api/articles/recent` → `[12, 10, ...]`

### How to Test the APIs

You can use **Postman**, **curl**, or any HTTP client. Below are example requests for each endpoint:

#### 1. Register a User
```sh
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```

#### 2. Login
```sh
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```
- The response will be a token string. Use this token in the `token` header for all article requests.

#### 3. Create an Article
```sh
POST http://localhost:8080/api/articles
Content-Type: application/json
token: <your_token>

{
  "title": "Sample Article",
  "content": "This is a test.",
  "author": "Author Name"
}
```

#### 4. List Articles (Paginated)
```sh
GET http://localhost:8080/api/articles?page=0&size=3
token: <your_token>
```

#### 5. Get Article by ID
```sh
GET http://localhost:8080/api/articles/10
token: <your_token>
```

#### 6. Get Recently Viewed Articles
```sh
GET http://localhost:8080/api/articles/recent
token: <your_token>
```

#### 7. Update an Article
```sh
PUT http://localhost:8080/api/articles/10
Content-Type: application/json
token: <your_token>

{
  "title": "Updated Title",
  "content": "Updated content.",
  "author": "New Author"
}
```

#### 8. Delete an Article
```sh
DELETE http://localhost:8080/api/articles/10
token: <your_token>
```

---

**Tips:**
- Always include the `token` header for article endpoints.
- Use the correct `page` and `size` parameters for pagination.
- To see 5 articles in `/recent`, you must view 5 different articles using the detail endpoint.
- The API returns paginated results in a standard Spring Data format.

