FROM openjdk:17-slim

WORKDIR /app
COPY . .

RUN apt-get update && apt-get install -y maven

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/bunny-memo-bot-1.0-SNAPSHOT.jar"]