# API Platform Service

## Build and run locally

### Prerequisites

The project requires Maven, JDK 17, and a MongoDB instance running on port 27017. Mongo must have user 'user' with password 'password' with 'readWriteAnyDatabase' role and authentication database 'admin'.

If you would like to use _docker-compose_ instead, the docker-compose file in this folder will automatically spin up this service.

### Ensure tests are running successfully
First, make sure MongoDB is running on port 27017.

Second, to ensure both unit/integration tests and API tests are running successfully, run the command: `./mvnw test` in the root directory.

### Build the project
To build the project without running tests, run the command: `./mvnw install -DskipTests=true` in the root directory.

To build the project with tests, run the command: `./mvnw install` in the root directory.

### Running the Spring Boot module
To run the Spring Boot module, run the command: `./mvnw spring-boot:run` in the root directory. This will attempt to connect to a mongodb instance running locally (docker works too with host name _mongo_) on **localhost** with port **27017**.

Spring service will start on **localhost** on port **5000**.

### Swagger and API Spec

Note - Swagger may take a couple of seconds to initialize. If an error is thrown at first, please refresh again after a couple of seconds.

#### Swagger UI for REST API (Relative URL)
`/api-ui`

#### OpenAPI Specification (Relative URL)
`/api-docs`

#### Measurement Scheduling
Measurements can be scheduled based on two schedulers:

*Time based:*
1. Strict, time based, provided probe is available.
2. Measurement tasks are forwarded to a probe based on a scheduled start timestamp.
3. Conflict detection for new measurements is enabled. For a conflicting measurement, status is set to SCHEDULING_FAILED.

*On demand:*
1. FCFS, queue based, provided probe is available.
2. No time guaranteed execution.
3. Measurement task queue is polled every two minutes and the next immediate task for every free probe is forwarded for execution.
4. No conflict detection. All incoming measurements are queued.

This setting can be toggled in application.properties. The property name is *connector.scheduling.algorithm*. This setting needs to match with its counterpart on the connector service.

## Project Structure

The following will detail the most important folders and files and their uses as part of this project.

1. _api-docs.yaml_: This is the latest API documentation for this service, updated everytime there are changes.

2. _pom.xml_: This is the Maven project configuration and dependency management file.

3. _main/.../platformservice/component/security_: This contains files used for authentication and authorization.

4. _main/.../platformservice/config_: This contains configuration files for the database, web security, etc.

5. _main/.../platformservice/exception_: This contains our custom exception logic used to hide error responses and present more readable messages to the user.

6. _main/.../platformservice/model_: This contains all our data models including entities persisted to the database and other POJOs.

7. _main/.../platformservice/repository_: This contains our repository files used to interface with the database.

8. _main/.../platformservice/service_: This contains all our services and encapsulates most or all of the business logic of the application.

9. _main/.../platformservice/web/rest_: This contains our REST endpoint interfaces located in the controller files and their respective data transfer objects sent and received.

10. _main/resources/_: This contains our application.properties files that inject properties on application execution. DB properties are located here.

11. _src/test/.../service_: These tests are unit/integration tests for business logic belonging to the various services. They require a connected database instance.

12. _src/test/.../web/..._: These tests are API tests that hit the endpoints to simulate the working of end to end logic. They require a connected database instance.

13. _src/test/resources_: This contains our application.properties files that inject properties when tests are run. Test DB properties are located here too.
