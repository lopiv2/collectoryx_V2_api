FROM maven:3.8.6-amazoncorretto-18 as builder

COPY . /

RUN mvn clean package -DskipTests=true

FROM amazoncorretto:18-alpine

WORKDIR /app

RUN mkdir /app/uploads

COPY --from=builder /target/Collectoryx-Api-*.jar ./Collectoryx-Api.jar

EXPOSE 8080

CMD ["java", "-jar", "Collectoryx-Api.jar"]
