FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy only the files needed for dependency resolution
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies first (this layer will be cached unless build.gradle changes)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Copy source code and build the application
COPY src ./src
RUN ./gradlew build -x test --no-daemon

# Second stage - runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]