FROM ubuntu:22.04

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven && \
    apt-get clean

WORKDIR /app
COPY . .

# Собираем проект
RUN mvn clean package -DskipTests

# Запускаем (JAR теперь будет bunny-memo-bot.jar)
CMD ["java", "-jar", "target/bunny-memo-bot.jar"]