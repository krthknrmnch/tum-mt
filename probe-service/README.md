# Probe Service

## Build and run locally

### Prerequisites

The project requires a Linux OS, docker along with the sysbox container runtime, Maven and JDK 17.

Please ensure following environment variables exist:
1. PROBE_ID - Received when a probe is created using the API from the platform service.
2. PROBE_API_KEY - Received from the config file using the API from the platform service.
3. CONNECTOR_IP_PORT - IP:Port string of the machine running the connector service. "192.168.0.150:3000" for example. Also received from the config file using the API from the platform service.
4. STARLINK_DISH_METRICS_COLLECTION - Either true or false. Enables or disables Starlink dish metadata collection from the Dishy API.
5. STARLINK_DISH_LOCATION_COLLECTION - Either true or false. Enables or disables Starlink dish location collection from the Dishy API.
6. IS_WIRED_INTERFACE_ACTIVE, IS_WIFI_INTERFACE_ACTIVE, IS_CELLULAR_INTERFACE_ACTIVE - Either true or false based on the network interfaces active on this machine
7. STARLINK_ACTIVE_INTERFACE - Takes one of these four values - [wired, wifi, cellular, none] - depending on which interface the Starlink connection is connected to

### Run the service

To build and run the project, please use the docker-compose file in this folder. It will automatically spin up the probe service. Above environment variables have to be set for this to work too. Note that this method will require the sysbox runtime to be installed on your Linux machine. We use sysbox to spawn a top level system container with support for docker in docker.

Service will start on **localhost** on port **8000**.

Logging is currently set to ERROR level within the _application.properties_ file in _main/resources_ folder. Can be changed to INFO for the default logging level.

## Project Structure

The following will detail the most important folders and files and their uses as part of this project.

1. _pom.xml_: This is the Maven project configuration and dependency management file.

2. _main/.../probeservice/config_: This contains configuration files for the task scheduler.

3. _main/.../probeservice/service_: This contains all our services and encapsulates most or all of the business logic of
   the application. The Measurement Service has the core measurement logic for spawning measurement containers. The MetricService contains a scheduled method for extracting Starlink dish data.

4. _main/.../probeservice/web/util_: This contains both the Constants and Utils classes. The latter contains helper methods and methods for handling Connector service REST calls.

5. _main/.../probeservice/web/rest_: This contains our REST endpoint interfaces located in the controller files and their
   respective data transfer objects sent and received.

6. _main/.../probeservice/web/ws_: This contains our websocket endpoint interfaces located in the controller files and their
   respective data transfer objects sent and received.

7. _main/resources_: This contains our application.properties files that inject properties on application execution.
