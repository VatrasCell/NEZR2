# Developer Guide

## Project Overview

NEZR2 is a JavaFX desktop application built with Spring Boot. It uses an H2 database for data storage and Liquibase for database schema management.

## Technology Stack

- **Language**: Java 8+
- **Framework**: Spring Boot 2.x
- **UI Framework**: JavaFX 8+
- **Database**: H2 Database
- **Build Tool**: Maven
- **Database Migration**: Liquibase
- **Excel Export**: Apache POI
- **UI Design**: FXML

## Project Structure

The project follows a standard Maven structure with Java source files organized in packages according to functionality. The main directories are:

- `src/main/java/` - Contains all Java source files
- `src/main/resources/` - Contains application resources like FXML files, CSS styles, images, and database scripts
- `src/test/java/` - Contains unit and integration tests

Packages are organized by feature or functionality to maintain a clear separation of concerns. Common package patterns include:
- Feature-specific packages for UI components (controllers, services, models)
- Shared utility packages for common functionality
- Resource packages for UI layouts, styles, and images

## Setting Up the Development Environment

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher
- IDE with JavaFX support (IntelliJ IDEA recommended)

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
mvn spring-boot:run
```

Or run the main class: `de.vatrascell.nezr.application.Main`

## Code Structure

### Main Application Classes

#### Main.java
The entry point of the application. It initializes Spring Boot and JavaFX.

#### ApplicationStarter.java
Extends JavaFX Application class and handles the application lifecycle.

#### ApplicationConfiguration.java
Spring configuration class that enables component scanning.

#### Database.java
Base class for database configuration and connection management.

### Key Components

#### Controllers
Controllers handle UI interactions and coordinate between the UI and service layers:
- Follow the naming convention `*Controller.java`
- Use `@Component` and `@FxmlView` annotations
- Inject services through constructor injection

#### Services
Services contain business logic and data processing:
- Follow the naming convention `*Service.java`
- Use `@Service` annotation
- Handle database operations and business rules

#### Models
Data models represent the application's data structures:
- Follow the naming convention `*.java`
- Use Lombok annotations for boilerplate code reduction
- Represent both database entities and UI data

#### Repositories
Direct database access is handled through JDBC in service classes rather than separate repository classes.

## Database Management

### Liquibase
The project uses Liquibase for database schema management. Changelog files are located in `src/main/resources/db/`.

### Database Schema
The H2 database files are stored in the `db/` directory. The main database file is `nezr_v8.mv.db`.

### SQL Statements
SQL statements are defined in the `SqlStatement` class in the application package.

## UI Development

### FXML Files
UI layouts are defined in FXML files located in `src/main/resources/view/`.

### Controllers
Each FXML file has a corresponding controller class that handles user interactions.

### CSS Styling
CSS styles are defined in `src/main/resources/style/application.css`.

## Testing

### Unit Tests
Unit tests are located in the `src/test/java/` directory.

### Running Tests
```bash
mvn test
```

## Adding New Features

### 1. Create a New Controller
1. Create a new FXML file in `src/main/resources/view/`
2. Create a corresponding controller class in the appropriate package
3. Annotate the controller with `@Component` and `@FxmlView`
4. Implement the required functionality

### 2. Create a New Service
1. Create a new service class in the appropriate package
2. Annotate the service with `@Service`
3. Implement the business logic
4. Inject the service into controllers that need it

### 3. Add Database Schema Changes
1. Create a new Liquibase changelog file in the appropriate directory under `src/main/resources/db/schema/`
2. Add the changelog to `master-changelog.xml`
3. Update the model classes if needed
4. Update the service classes to use the new schema

### 4. Add New UI Components
1. Update the FXML file to include new UI elements
2. Add corresponding fields in the controller class
3. Implement event handlers for new UI elements
4. Update CSS if needed for styling

## Code Style and Conventions

### Naming Conventions
- Classes: PascalCase
- Methods: camelCase
- Variables: camelCase
- Constants: UPPER_SNAKE_CASE

### Package Structure
- Group related functionality in packages
- Use descriptive package names
- Follow the existing package structure for consistency

### Annotations
- Use Spring annotations for dependency injection
- Use Lombok annotations to reduce boilerplate code
- Use JavaFX annotations for UI binding

## Debugging

### Logging
The application uses standard Java logging. Check the console output for error messages.

### Database Debugging
- Use the H2 console to inspect the database directly
- Enable SQL logging to see executed queries

### UI Debugging
- Use the JavaFX Scenic View tool for UI debugging
- Enable FXML validation in the IDE

## Deployment

For deployment instructions, see the [Deployment Guide](./07_deployment_guide.md).

## Contributing

### Code Reviews
All code changes should be reviewed before merging.

### Testing
Ensure all tests pass before submitting changes.

### Documentation
Update documentation when adding or modifying features.