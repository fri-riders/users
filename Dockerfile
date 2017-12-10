FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/users-1.0-SNAPSHOT.jar /app

EXPOSE 8082

CMD ["java", "-jar", "users-1.0-SNAPSHOT.jar"]
