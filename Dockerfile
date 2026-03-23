FROM gradle:jdk21 AS build
WORKDIR /home/gradle/project

COPY settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
COPY src src
COPY docker/application.yaml src/main/resources/application.yaml

RUN gradle --no-daemon installDist

FROM eclipse-temurin:21-jre
WORKDIR /opt/ticket-service

COPY --from=build /home/gradle/project/build/install/TicketService/ /opt/ticket-service/

EXPOSE 8080 8085

CMD ["/opt/ticket-service/bin/TicketService"]
