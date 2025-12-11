FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY hospital-backend-user-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
CMD ["java","-jar","app.jar"]