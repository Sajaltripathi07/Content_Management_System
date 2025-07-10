# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY wait-for-it.sh ./wait-for-it.sh
RUN chmod +x wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
