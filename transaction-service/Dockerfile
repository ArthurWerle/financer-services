FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN ./gradlew build -x test --no-daemon

# Just for debug
RUN ls -al build/libs

RUN cp build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
