FROM azul/zulu-openjdk:17
WORKDIR /app
COPY target/json-mockr-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "json-mockr-0.0.1-SNAPSHOT.jar"]