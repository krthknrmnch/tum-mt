# Connector Service

## Build and run locally

### Prerequisites

The project requires Maven, JDK 17, and a MongoDB instance running on port 27017. Mongo must have user 'user' with password 'password' with 'readWriteAnyDatabase' role and authentication database 'admin'.

Please ensure following environment variables exist:
1. CONNECTOR_REGION - One of EU, NA, SA, AS, OC, AF
2. CONNECTOR_IP_PORT - This machine's IP:Port string. "192.168.0.150:3000" for example. This has to be publicly reachable.
3. CONNECTOR_OAKESTRA_IP_PORT - If deploying using Oakestra, make sure this is the round-robin network address for this service instance, else make sure this is set to a blank string

If you would like to use _docker-compose_ instead, the docker-compose file in this folder will automatically spin up this service.

### Ensure tests are running successfully
First, make sure MongoDB is running on port 27017.

Second, to ensure tests are running successfully, run the command: `./mvnw test` in the root directory.

### Build the project
To build the project without running tests, run the command: `./mvnw install -DskipTests=true` in the root directory.

To build the project with tests, run the command: `./mvnw install` in the root directory.

### Running the Spring Boot module

To run the Spring Boot module, run the command: `./mvnw spring-boot:run` in the root directory.
Spring service will start on **localhost** on port **3000**.

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

This setting can be toggled in application.properties. The property name is *connector.scheduling.algorithm*. This setting needs to match with its counterpart on the platform service.

## Project Structure

The following will detail the most important folders and files and their uses as part of this project.

1. _pom.xml_: This is the Maven project configuration and dependency management file.

2. _main/.../connectorservice/config_: This contains configuration files for web security, database, etc.

3. _main/.../connectorservice/exception_: This contains our custom exception logic used to hide error responses and present more readable messages to the user.

4. _main/.../connectorservice/model_: This contains all our data models including entities persisted to the database and other POJOs.

5. _main/.../connectorservice/repository_: This contains our repository files used to interface with the database.

6. _main/.../connectorservice/service_: This contains all our services and encapsulates most or all of the business logic of the application.

7. _main/.../connectorservice/web/rest_: This contains our REST endpoint interfaces located in the controller files and their respective data transfer objects sent and received.

8. _main/resources/_: This contains our application.properties files that inject properties on application execution. DB properties are located here.

9. _src/test/.../service_: These tests are unit/integration tests for business logic belonging to the various services. They require a connected database instance.

10. _src/test/resources_: This contains our application.properties files that inject properties when tests are run. Test DB properties are located here too.
