FROM eclipse-temurin:21-jdk-alpine

RUN --mount=type=secret,id=GRAFANA_API_TOKEN

ENV OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-eu-north-0.grafana.net/otlp"
ENV OTEL_SERVICE_NAME="time"

ADD https://github.com/grafana/grafana-opentelemetry-java/releases/download/v2.9.0/grafana-opentelemetry-java.jar .
ENV JAVA_TOOL_OPTIONS="-javaagent:grafana-opentelemetry-java.jar"

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]