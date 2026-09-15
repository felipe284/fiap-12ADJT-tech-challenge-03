FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /build

COPY pom.xml .
COPY notification-service ./notification-service

RUN apt-get update && apt-get install -y maven
RUN mvn -DskipTests clean package -f notification-service/pom.xml

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/notification-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
