# Stage 1: jar 빌드하기
FROM gradle:8.3-jdk17 AS builder

WORKDIR /app

COPY . .

RUN gradlew bootJar

# Stage 2: app 실행
FROM openjdk:17-jdk-slim
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
