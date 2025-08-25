# Project Overview

## What is NEZR2?

NEZR2 is a Java-based survey/questionnaire application built with JavaFX and Spring Boot. The application allows users to create, manage, and conduct surveys with various question types.

## Key Features

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

## Getting Started

For installation and setup instructions, please refer to the [Deployment Guide](./07_deployment_guide.md).