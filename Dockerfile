# Multi-stage build para otimizar o tamanho da imagem
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Imagem final
FROM openjdk:17-jdk-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

WORKDIR /app

# Copiar o JAR da aplicação
COPY --from=build /app/target/*.jar app.jar

# Criar arquivo de propriedades para produção
RUN echo "# Configurações de produção para GKE" > .env.prod.properties

# Variáveis de ambiente com valores padrão
ENV SERVER_PORT=13000
ENV POSTGRES_URL=jdbc:postgresql://postgres-service:5432/postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV MONGO_URI=mongodb://admin:admin@mongo-service:27017/monolito?authSource=admin
ENV ALLOWED_ORIGINS=*
ENV SECRET=secret-0000000000000000000000000000
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Expor a porta
EXPOSE 13000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:13000/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
