# Estágio 1: Build da aplicação (Usando Maven e Java 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o pom.xml e baixa as dependências (ajuda no cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e faz o build
COPY src ./src
# Pula os testes para agilizar o build da imagem
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação (Imagem mais leve, apenas com JRE)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
# NOTA: Adicionado o --enable-preview pois você ativou no maven-compiler-plugin
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]