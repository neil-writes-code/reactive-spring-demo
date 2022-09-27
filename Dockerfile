FROM dyniri/liberica-nik:java-17

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

RUN chmod +x mvnw  && \
    ./mvnw -ntp dependency:go-offline

COPY src/ src/

RUN ./mvnw package -Pnative -DskipTests

FROM scratch

COPY --from=0 /tmp /tmp
COPY --from=0 /app/target/hr-service .

ENTRYPOINT ["./hr-service"]

