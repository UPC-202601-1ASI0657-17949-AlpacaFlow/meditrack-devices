# Usamos Java 21
FROM openjdk:21-jdk-slim
FROM eclipse-temurin:21-jdk-alpine

# Definimos dónde está el archivo compilado
ARG JAR_FILE=target/*.jar