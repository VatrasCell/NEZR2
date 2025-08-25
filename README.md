# NEZR2

NEZR2 is a Java-based survey/questionnaire application built with JavaFX and Spring Boot. The application allows users to create, manage, and conduct surveys with various question types.

## Features

- Create and manage questionnaires
- Multiple question types (Multiple Choice, Short Answer)
- Survey administration and data collection
- Data export functionality (Excel)
- User-friendly graphical interface
- Database storage using H2 database

## Technology Stack

- **Language**: Java
- **Framework**: Spring Boot, JavaFX
- **Database**: H2 Database
- **Build Tool**: Maven
- **UI Framework**: JavaFX with FXML
- **Data Export**: Apache POI (Excel)

## Project Structure

The application is organized into several packages:

- `admin`: Administrative functionality
- `application`: Core application components
- `category`: Category management
- `export`: Data export functionality
- `flag`: Question flagging system
- `gratitude`: Thank you page functionality
- `headline`: Headline management
- `location`: Location management
- `login`: Authentication system
- `message`: Message handling
- `model`: Data models
- `multipleChoiceQuestion`: Multiple choice question handling
- `question`: Question management
- `questionList`: Question list functionality
- `react`: React functionality (conditional questions)
- `relation`: Database relations
- `start`: Start screen functionality
- `survey`: Survey functionality
- `validation`: Data validation

## Documentation

Comprehensive documentation is available in the [.doku](./.doku) directory:

1. [Project Overview](./.doku/01_project_overview.md) - General information about the NEZR2 application
2. [Architecture](./.doku/02_architecture.md) - Layered architecture of the application
3. [Database Schema](./.doku/03_database_schema.md) - ER diagram of the database schema
4. [API Documentation](./.doku/04_api_documentation.md) - Internal APIs documentation
5. [User Guide](./.doku/05_user_guide.md) - Instructions for end users
6. [Developer Guide](./.doku/06_developer_guide.md) - Information for developers
7. [Deployment Guide](./.doku/07_deployment_guide.md) - System requirements and installation instructions

## Getting Started

For installation and setup instructions, please refer to the [Deployment Guide](./.doku/07_deployment_guide.md).

## Contributing

Please read the [Developer Guide](./.doku/06_developer_guide.md) for information on how to contribute to this project.

## License

This project is proprietary software. All rights reserved.

## Support

For support, please contact the development team or refer to the documentation in the [.doku](./.doku) directory.