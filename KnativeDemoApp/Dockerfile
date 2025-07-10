# Use the official maven/Java 8 image to create a build artifact: https://hub.docker.com/_/maven
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM openjdk:24-ea-17-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/KnativeDemoApp-*.jar /KnativeDemoApp.jar

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/KnativeDemoApp.jar"]