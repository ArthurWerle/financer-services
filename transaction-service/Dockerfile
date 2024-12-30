FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN ./gradlew build -x test --no-daemon

RUN apt-get update && apt-get install -y curl && apt-get clean
RUN cp build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
