# Tourplanner

## Description

Tourplanner is a JavaFX-based application that allows users to create and manage tours (bike, hike, running, or vacation) and their associated logs and statistical data. The application follows the Presentation-Model pattern and implements a layer-based architecture with a UI Layer, a business layer (BL), and a data access layer (DAL). It uses design patterns, reusable UI components, and stores tour data and logs in a PostgreSQL database, with images stored externally on the filesystem. The application also includes logging, reporting, unit tests, and configuration management.

## Features

- **Tour Management**: Create, modify, delete, and list tours with details such as name, description, from, to, transport type, distance, estimated time, and route information (image with the tour map).
- **Tour Logs**: Create, modify, delete, and list tour logs with details such as date/time, comment, difficulty, total distance, total time, and rating.
- **Data Retrieval**: Retrieve distance and time via REST request using the OpenRouteservice.org API and map using Leaflet.
- **Computed Attributes**: Automatically compute tour attributes like popularity and child-friendliness.
- **Full-Text Search**: Perform full-text search in tour and tour-log data, including computed values.
- **Import/Export**: Import and export tour data in a file format of your choice.
- **Reports**: Generate tour reports with all information of a single tour and its logs, and summary reports for statistical analysis.
- **Unique Feature**: Add a unique feature to enhance the application.

## Goals

- Implement a graphical user interface based on JavaFX.
- Apply the Presentation-Model pattern.
- Implement a layer-based architecture with UI, BL, and DAL layers.
- Use design patterns and reusable UI components.
- Store data using an O/R-mapper in a PostgreSQL database.
- Use a logging framework like log4j.
- Generate reports using an appropriate library.
- Create unit tests with JUnit.
- Manage configuration in a separate config file.
- Document the application architecture and development process using UML and wireframes.

## How to run

### Windows
Run:
```shell
  .\mvnw.cmd javafx:run -f client/pom.xml
```
Test:
```shell
  .\mvnw.cmd test -f client/pom.xml
```
### Linux
Run:
```shell
  ./mvnw javafx:run -f client/pom.xml
```
```shell
  ./mvnw test -f client/pom.xml
```

## Documentation

- [Documentation](./Protokoll_Intermediate_TP_Lampart_Unger.pdf)