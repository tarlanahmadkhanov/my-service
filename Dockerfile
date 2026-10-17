# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Сначала только pom.xml: слой с зависимостями кешируется,
# пока не меняется pom.xml
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# Потом исходники
COPY src src
RUN mvn -B package -DskipTests

# ---------- Stage 2: extract layers ----------
FROM eclipse-temurin:17-jre AS extract
WORKDIR /builder
COPY --from=build /workspace/target/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ---------- Stage 3: runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /application

# Непривилегированный пользователь с числовым UID
# (Kubernetes с runAsNonRoot проверяет только числовой UID)
RUN groupadd --system --gid 10001 spring \
 && useradd --system --uid 10001 --gid spring spring

# Слои от редко меняющихся к часто меняющимся
COPY --from=extract /builder/extracted/dependencies/ ./
COPY --from=extract /builder/extracted/spring-boot-loader/ ./
COPY --from=extract /builder/extracted/snapshot-dependencies/ ./
COPY --from=extract /builder/extracted/application/ ./

USER 10001
EXPOSE 8080

# JVM берёт до 75% памяти контейнера (из memory limit в k8s)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "application.jar"]
