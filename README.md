# # GetAwayAPI

## 📖 Overview

This project is a backend-only microservices architecture that provides the foundation for future client connections. The system comprises three key services: the **API Gateway**, the **DAL (Data Access Layer)**, and the **Parser**. Each service has a distinct role in processing, storing, and managing data while ensuring scalability and efficiency.

The **API Gateway** acts as a gatekeeper, routing incoming requests to the appropriate microservices. The **DAL** service manages data persistence with efficient handling of large data through BLOB storage, while the **Parser** service processes content in a multithreaded manner, ensuring fast and accurate word mapping.

---

## 🏛️ Architecture

The project follows a **microservices architecture**, where each service operates independently but communicates through the **API Gateway**.

### Key Components:
- **API Gateway**: The central service that routes requests to the DAL and Parser microservices, acting as the entry point for all backend communications.
- **DAL Service**: Responsible for managing data with Hibernate ORM, handling three primary tables, and supporting efficient data retrieval and persistence.
- **Parser Service**: Processes content in a multithreaded fashion, receiving data from the gateway and performing parsing tasks, including word mapping.

---

## ⚙️ Implementation Details

### Technologies Used:

- **Java 17**: The primary language used across all services.
- **Spring Boot**: The framework for building the microservices with RESTful APIs.
- **Spring Cloud Gateway**: Provides routing for the API Gateway.
- **Hibernate ORM**: Manages object-relational mapping for the DAL service.
- **MySQL**: The database used for storing data, with BLOB storage for large content.
- **Maven**: For build automation and dependency management.

---

## 📚 Service Breakdown

### 1. **API Gateway Service**

#### Mission:
The **API Gateway** serves as the central entry point for routing all incoming requests. It is designed to manage and forward requests to the appropriate microservices (DAL or Parser) and handle any future communication with clients.

#### Key Responsibilities:
- **Routing**: Directs incoming API calls to the respective services (DAL or Parser).
- **Request Management**: Acts as a gatekeeper for future client communications, managing requests and ensuring they are handled correctly.
- **Security & Scalability**: In the future, the Gateway can be extended to include authentication, rate limiting, and other cross-cutting concerns.

---

### 2. **DAL (Data Access Layer) Service**

#### Mission:
The **DAL Service** is responsible for managing all data persistence and retrieval operations. It handles three main tables, supports complex operations using Hibernate ORM, and ensures data consistency with cascade operations.

#### Key Features:
- **Three Main Tables**:
  - **Articles Table**: Stores article metadata such as ID, name, author, created_at, size, and status.
  - **Content Table**: Stores large article content in **BLOB** format for efficient storage and retrieval.
  - **Word Mapping Table**: Maps words to their corresponding articles, with offset data stored in JSON format.
- **BLOB Storage**: Handles large content storage, such as articles, using BLOB format, ensuring that content can be efficiently stored and retrieved.
- **Cascade Operations**: The service uses Hibernate's cascade operations to ensure that related data (e.g., articles and their content or word mappings) are managed consistently when CRUD operations occur.
- **Status Updates**: When the Parser service sends word mappings, the DAL service updates the article’s status from `pending` to `indexed`.

#### Database:
- **Articles Table**:
  - `id`, `name`, `author`, `created_at`, `size`, `status` (`pending` or `indexed`).
- **Article Content Table**:
  - `article_id` (foreign key), `compressed_content` (stored as BLOB).
- **Word Mapping Table**:
  - `word` (primary key), `article_id` (foreign key), `offsets` (JSON array).

---

### 3. **Parser Service**

#### Mission:
The **Parser Service** is responsible for parsing large article content, extracting word mappings, and providing this data to the DAL service. It is designed to handle content parsing efficiently using a multithreaded approach.

#### Key Features:
- **Multithreaded Parsing**: The service processes large content by dividing it into chunks and assigning each chunk to a separate thread. This ensures that large articles are parsed efficiently and quickly.
- **Word Mapping Generation**: For each parsed chunk, the service extracts words, calculates their offsets within the article, and generates word mappings.
- **Communication with DAL**: Once the word mappings are generated, the service communicates with the DAL to store these mappings in the Word Mapping Table and triggers the status update for the corresponding article.

#### Key Responsibilities:
- Receive requests from the API Gateway to parse article content.
- Perform multithreaded parsing by dividing content into manageable chunks.
- Communicate word mappings and offsets back to the DAL service.



## 🚀 Running the Project

### Running Locally:

To run each service locally, you can use Maven to start each Spring Boot application individually:

```bash
mvn spring-boot:run

