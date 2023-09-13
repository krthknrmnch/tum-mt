# Build the application
FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /app

# Copy files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Build all the dependencies in preparation to go offline.
# This is a separate step so the dependencies will be cached unless
# the pom.xml file has changed.
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src

# Package
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Run the application
FROM alpine
USER root
ARG DEPENDENCY=/app/target/dependency
RUN apk update
RUN apk add --no-cache openjdk17-jre
RUN apk add --no-cache docker
RUN apk add --no-cache openrc
RUN apk add --no-cache --repository=http://dl-cdn.alpinelinux.org/alpine/edge/testing grpcurl
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.tum.in.cm.probeservice.ProbeServiceApplication"]