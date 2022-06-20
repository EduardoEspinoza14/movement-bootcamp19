FROM openjdk:8
#VOLUME /tmp
EXPOSE 8093
ADD target/movement.jar movement.jar
ENTRYPOINT ["java", "-jar", "movement.jar"]
