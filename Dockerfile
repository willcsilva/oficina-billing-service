# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
# Copia o pom.xml e baixa as dependências (otimização de cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem Final (Mais leve)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia o .jar gerado no estágio anterior
COPY --from=builder /app/target/*.jar app.jar
# Expõe a porta 8080
EXPOSE 8080
# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]