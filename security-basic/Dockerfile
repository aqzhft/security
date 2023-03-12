FROM openjdk:8-jdk-buster

COPY target/security-basic.jar /app.jar

ENV JAVA_OPTS=""

ENV LC_ALL="C.UTF-8"

EXPOSE 8080

CMD ["sh", "-c" , "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]