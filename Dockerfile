# =========================
# 1. Etapa de build
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src

# Build da aplicação sem testes
RUN mvn clean package -DskipTests


# =========================
# 2. Imagem final
# =========================
FROM eclipse-temurin:21-jdk-jammy

# Instalar utilitários mínimos (como curl para health check)
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

WORKDIR /app

# Copiar o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Definir variáveis de ambiente diretamente no Dockerfile
ENV SERVER_PORT=13000 \
    POSTGRES_URL=jdbc:postgresql://136.112.152.217:5432/postgres \
    POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=4@OaOAPLv}v0(T1i \
    MONGO_URI=mongodb+srv://S13:mjtbAvnoBRS9MDAq@monolito.wrvc4cl.mongodb.net/monolito?retryWrites=true&w=majority&appName=monolito \
    ALLOWED_ORIGINS=* \
    SECRET=secret-0000000000000000000000000000 \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Expor a porta
EXPOSE 13000

# Health check com tolerância inicial
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:13000/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
