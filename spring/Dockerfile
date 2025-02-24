# ./spring/Dockerfile
FROM openjdk:17-jdk-slim
COPY concert-reservation-interfaces.jar /app/concert-reservation-interfaces.jar
WORKDIR /app
CMD ["java", "-jar", "concert-reservation-interfaces.jar"]