FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*.jar app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.30.0/opentelemetry-javaagent.jar /opt/opentelemetry-agent.jar
ENTRYPOINT java -javaagent:/opt/opentelemetry-agent.jar \
                -Dotel.resource.attributes=service.instance.id=$HOSTNAME \
                -jar /app.jar