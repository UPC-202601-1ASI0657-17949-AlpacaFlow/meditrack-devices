FROM maven:3.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/*.jar app.jar
# Render usa la variable PORT, la exponemos
ENTRYPOINT ["java", "-jar", "/app.jar"]