# Dockerfile — ajusté pour utiliser le Maven Wrapper généré par IntelliJ
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# On copie d'abord le wrapper Maven et le pom.xml.
# Docker met en cache les layers — si pom.xml ne change pas,
# il ne re-télécharge pas les dépendances à chaque build.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Rendre le script exécutable (nécessaire sur Linux)
RUN chmod +x mvnw

# Télécharger les dépendances en isolation (optimise le cache Docker)
RUN ./mvnw dependency:go-offline -B

# Copier le code source et construire
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Stage 2 : image de production légère (JRE seul, pas JDK complet)
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S busapp && adduser -S busapp -G busapp
USER busapp

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
EXPOSE 8080