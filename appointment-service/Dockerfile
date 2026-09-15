FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /build

COPY pom.xml .
COPY appointment-service ./appointment-service

RUN apt-get update && apt-get install -y maven
RUN mvn -DskipTests clean package -f appointment-service/pom.xml

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/appointment-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
