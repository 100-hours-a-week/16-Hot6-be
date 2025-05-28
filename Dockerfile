FROM eclipse-temurin:21-jre
WORKDIR /app
COPY backend.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
