# Deployment Guide

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 7 or higher, macOS 10.10 or higher, Linux (Ubuntu 16.04 or higher)
- **Java Runtime**: Java 8 or higher
- **Disk Space**: 100 MB available space
- **Memory**: 512 MB RAM

### Recommended Requirements
- **Operating System**: Windows 10 or higher, macOS 11 or higher, Linux (Ubuntu 20.04 or higher)
- **Java Runtime**: Java 11 or higher
- **Disk Space**: 500 MB available space
- **Memory**: 2 GB RAM

## Installation

### Option 1: Running from Source Code

#### Prerequisites
1. Install Java 8 or higher
2. Install Maven 3.6 or higher

#### Steps
1. Clone or download the NEZR2 source code
2. Navigate to the project root directory
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Option 2: Running Pre-built JAR

#### Prerequisites
1. Install Java 8 or higher

#### Steps
1. Download the pre-built JAR file
2. Run the application:
   ```bash
   java -jar nezr2.jar
   ```

## Directory Structure

After installation, the application will create the following directory structure:

```
NEZR2/
├── db/                      # Database files
│   └── nezr_v8.mv.db       # Main database file
├── exportExcel/            # Excel export files
├── src/                    # Source code (if built from source)
└── target/                 # Compiled files (if built from source)
```

## Configuration

### Database Configuration
The application uses an H2 database with the following default configuration:
- **URL**: `jdbc:h2:[application_directory]/db/nezr_v8;MODE=MySQL`
- **Username**: `usr`
- **Password**: `Q#DQ8Ka&9Vq6`;s`

The database configuration can be modified in `src/main/resources/application.yml`.

### Application Properties
Key application properties can be configured in `src/main/resources/application.yml`:
- Database connection settings
- Logging levels
- Liquibase settings

## First-Time Setup

### Initial Database Setup
The application will automatically create the database schema on first run using Liquibase migrations.

### Default Data
The application includes initial data for:
- Default locations
- Sample categories
- Sample questionnaires

This data is loaded from `src/main/resources/db/schema/init_data/nezr_db_daten_v8.sql`.

## User Management

### Admin Access
The application supports admin functionality:
- Default admin access is disabled in production
- In development mode, use the dev login feature
- For production, implement proper authentication

## Data Management

### Backup
To backup the application data:
1. Stop the application
2. Copy the `db/nezr_v8.mv.db` file to a safe location

### Restore
To restore the application data:
1. Stop the application
2. Replace the `db/nezr_v8.mv.db` file with the backup
3. Start the application

### Data Export
Survey data can be exported to Excel format:
1. Access the admin panel
2. Select a questionnaire
3. Choose "Excel Export"
4. Select date range
5. Exported files are saved in the `exportExcel/` directory

## Troubleshooting

### Common Issues

#### Database Connection Errors
- Ensure the `db` directory exists and is writable
- Check that the database file is not corrupted
- Verify database credentials in `application.yml`

#### Startup Issues
- Ensure Java is properly installed and in the PATH
- Check the console output for error messages
- Verify that the required ports are available

#### Performance Issues
- For large databases, consider increasing Java heap size:
  ```bash
  java -Xmx2g -jar nezr2.jar
  ```

### Log Files
The application logs to the console. For production deployments, consider redirecting output to a file:
```bash
java -jar nezr2.jar > application.log 2>&1
```

## Updating

### Updating from Source
1. Pull the latest source code
2. Rebuild the application:
   ```bash
   mvn clean install
   ```
3. Restart the application

### Database Migrations
Database schema updates are handled automatically by Liquibase when the application starts.

## Security Considerations

### Database Security
- Change the default database password in production
- Restrict access to the `db` directory
- Regularly backup the database

### Application Security
- Disable development mode in production
- Implement proper user authentication for admin functions
- Keep Java and dependencies up to date

## Performance Tuning

### JVM Settings
For better performance, consider adjusting JVM settings:
```bash
java -Xms512m -Xmx2g -jar nezr2.jar
```

### Database Optimization
- Regularly backup and compact the H2 database
- Monitor database size and performance
- Consider archiving old survey data

## Uninstalling

### Windows
1. Delete the application directory
2. Remove any shortcuts from the Start menu
3. Delete desktop shortcuts if any

### macOS/Linux
1. Delete the application directory
2. Remove any symbolic links
3. Delete desktop entries if any

## Support

For support, please contact the development team or refer to the documentation in the `.doku` directory.