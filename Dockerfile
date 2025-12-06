FROM ubuntu:22.04
RUN apt-get update && apt-get install -y openjdk-17-jdk maven
WORKDIR /app
COPY . .
RUN mvn clean compile jar:jar -DskipTests
CMD ["java", "-cp", "target/bunny-memo-bot-1.0.jar", "Main"]