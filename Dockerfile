FROM --platform=linux/amd64 openjdk:21
LABEL authors="ropold"
EXPOSE 8080
COPY backend/target/practicehub.jar practicehub.jar
ENTRYPOINT ["java", "-jar", "practicehub.jar"]