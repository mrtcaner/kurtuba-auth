FROM --platform=linux/amd64 eclipse-temurin:21-jre-jammy
EXPOSE 8080
COPY target/*.jar /usr/local/app.jar
CMD java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /usr/local/app.jar
