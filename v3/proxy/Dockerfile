FROM eclipse-temurin:18-jre-focal

RUN groupadd -g 322 proxy && \
    useradd -r -u 322 -g proxy proxy
USER proxy

WORKDIR /opt/proxy

COPY build/libs/proxy-*-all.jar proxy.jar

ENTRYPOINT java -Xmx16G -jar proxy.jar