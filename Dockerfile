FROM eclipse-temurin:17 AS BUILD
COPY . /app/
WORKDIR /app/
RUN ./mvnw clean test package -Pproduction
RUN ls -la /app/target

FROM eclipse-temurin:17
COPY --from=BUILD /app/target/wc-api-0.0.1-SNAPSHOT /app/
WORKDIR /app/
EXPOSE 8080
ENTRYPOINT java -jar wc-api-0.0.1-SNAPSHOT 8080