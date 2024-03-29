FROM gradle:jdk21 AS build
WORKDIR /app
COPY . /app
RUN gradle clean build -x test

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
