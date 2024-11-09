# 1. Use JDK image as the base
FROM openjdk:17-jdk-alpine AS build

# 2. Set the working directory
WORKDIR /build

# 3. Copy all project files
COPY . .

# 4. Build the project with Gradle
RUN ./gradlew clean build -x test

# 5. Use a new layer for the runtime environment
FROM openjdk:17-jdk-alpine
WORKDIR /app

# 6. Copy the built JAR file to the runtime container
COPY --from=build /build/build/libs/*.jar app.jar

# 7. Expose the application port
EXPOSE 8080

# 8. Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]