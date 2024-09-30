FROM openjdk:21-jdk-bookworm

# Copy the jar into the container
COPY target/load-0.0.1-SNAPSHOT.jar load-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD java -Xmx2G -jar load-0.0.1-SNAPSHOT.jar
