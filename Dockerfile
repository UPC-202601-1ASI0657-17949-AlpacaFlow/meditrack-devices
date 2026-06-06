# ETAPA 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS build
# Copiamos todo el proyecto al contenedor
COPY . .
# Compilamos el proyecto (esto genera la carpeta /target)
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
# Copiamos solo el JAR resultante de la etapa "build"
# Cambia 'nombre-de-tu-proyecto' por el nombre real de tu jar en /target
COPY --from=build /target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]