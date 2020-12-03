FROM openjdk:8-jre-alpine as builder
LABEL stage=githubMonsterApiBuilder
RUN apk update && apk upgrade && apk add openjdk8
ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER
RUN mkdir /app
WORKDIR /app
RUN chown -R $APPLICATION_USER /app
USER $APPLICATION_USER
COPY . .
RUN export JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk/jre
RUN ./gradlew build

FROM alpine
RUN apk update && apk upgrade && apk add openjdk8
RUN export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
COPY --from=builder ./app/build/libs/docker-hometask.jar .
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "docker-hometask.jar"]
