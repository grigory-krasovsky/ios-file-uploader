# Build stage (Maven)
FROM maven:3.8.2-openjdk-17-slim AS build
WORKDIR /app

# Copy POM first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage (Lightweight JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# ✅ Copy the wait script into the image
COPY wait-for-db.sh /app/wait-for-db.sh

# ✅ Make it executable
RUN chmod +x /app/wait-for-db.sh

EXPOSE 8081

# ✅ Change entrypoint to run the script instead of launching the JAR directly
ENTRYPOINT ["/app/wait-for-db.sh"]