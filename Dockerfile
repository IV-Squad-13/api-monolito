
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app


COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jdk-jammy


RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*


RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar


RUN echo "# Configurações de produção para GKE" > .env.prod.properties


ENV SERVER_PORT=13000 \
    POSTGRES_URL=jdbc:postgresql://136.112.152.217:5432/postgres \
    POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=4@OaOAPLv}v0(T1i \
    MONGO_URI=mongodb+srv://S13:mjtbAvnoBRS9MDAq@monolito.wrvc4cl.mongodb.net/monolito?retryWrites=true&w=majority&appName=monolito \
    ALLOWED_ORIGINS=* \
    SECRET=secret-0000000000000000000000000000 \
    JAVA_OPTS="-Xmx512m -Xms256m"


EXPOSE 13000


HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:13000/actuator/health || exit 1


ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

