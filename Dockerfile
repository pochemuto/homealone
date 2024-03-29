FROM openjdk:17-alpine AS build
WORKDIR workspace

ARG VERSION=no-version
ENV VERSION=${VERSION}

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
RUN ./gradlew build --refresh-dependencies
COPY src src

RUN ./gradlew installBootDist
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

FROM openjdk:17-alpine

RUN apk add -U tzdata \
    && cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime \
    && echo "Europe/Moscow" >  /etc/timezone

ARG CERT="YandexCA.crt"
COPY $CERT .
RUN keytool -importcert -file $CERT -alias $CERT -cacerts -storepass changeit -noprompt

COPY --from=build workspace/dependencies/ .
COPY --from=build workspace/snapshot-dependencies/ .
COPY --from=build workspace/spring-boot-loader/ .
COPY --from=build workspace/application/ .

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

