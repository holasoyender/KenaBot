FROM eclipse-temurin:18-jre-focal

RUN groupadd -g 322 proxy && \
    useradd -r -u 322 -g proxy proxy
USER proxy

WORKDIR /opt/proxy

COPY proxy/build/libs/proxy-*.jar proxy.jar

ENTRYPOINT java -Xmx1G -jar proxy.jar