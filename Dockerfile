FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
