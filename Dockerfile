FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar backend.jar
COPY secrets.properties /app/secrets.properties
ENTRYPOINT ["java", "-jar", "backend.jar", "--spring.config.additional-location=file:/app/secrets.properties"]