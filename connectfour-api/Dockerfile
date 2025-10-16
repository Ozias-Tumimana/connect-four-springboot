FROM openjdk:17-jdk-slim

WORKDIR /app

COPY connectfour-api/.mvn/ .mvn
COPY connectfour-api/mvnw connectfour-api/pom.xml ./

RUN ./mvnw dependency:go-offline

COPY connectfour-api/src ./src

RUN ./mvnw package -DskipTests

CMD ["java", "-jar", "target/connectfour-api-0.0.1-SNAPSHOT.jar"]