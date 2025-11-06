####
# This Dockerfile is used to build a container that runs the Quarkus application in JVM mode
####
FROM gradle:8.11.1-jdk21 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle clean build -Dquarkus.package.type=uber-jar --no-daemon

####
# Build the final image
####
FROM registry.access.redhat.com/ubi9/openjdk-21:1.20

ENV LANGUAGE='en_US:en'

# Copy the uber jar from the build stage
COPY --from=build /home/gradle/src/bootstrap/build/quarkus-app/quarkus-run.jar /deployments/quarkus-run.jar
COPY --from=build /home/gradle/src/bootstrap/build/quarkus-app/lib/ /deployments/lib/
COPY --from=build /home/gradle/src/bootstrap/build/quarkus-app/app/ /deployments/app/
COPY --from=build /home/gradle/src/bootstrap/build/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]
