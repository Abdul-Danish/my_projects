# 1️⃣ Use a base OS + JDK (OS info comes from here)
FROM eclipse-temurin:17-jre

# 2️⃣ Set the working directory inside the container
WORKDIR /app

# 3️⃣ Copy the built JAR from your machine into the container
COPY target/*.jar app.jar

# 4️⃣ Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

