# ---------- STAGE 1: Build ----------
FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar --no-daemon


# ---------- STAGE 2: Runtime ----------
FROM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

USER nonroot:nonroot

ENTRYPOINT ["app.jar"]
