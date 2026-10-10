FROM gradle:9.1.0-jdk21 AS build
WORKDIR /src
COPY . .
RUN gradle -PbackendOnly :server:installDist --no-daemon
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S eps && adduser -S eps -G eps
WORKDIR /app
COPY --from=build /src/server/build/install/server/ ./
USER eps
EXPOSE 8080
ENTRYPOINT ["/app/bin/server"]
