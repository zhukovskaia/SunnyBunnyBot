FROM openjdk:17-alpine
WORKDIR /app
COPY . .
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/bunny-memo-bot-1.0-SNAPSHOT.jar"]