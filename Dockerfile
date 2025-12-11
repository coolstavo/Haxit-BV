# 1) Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom first (caching dependencies)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# copy source and build
COPY src ./src
RUN mvn -q clean package -DskipTests

# 2) Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the built jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

