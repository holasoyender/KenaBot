FROM eclipse-temurin:19-jre-focal

WORKDIR /kena

RUN groupadd -g 322 kenabot && \
    useradd -r -u 322 -g kenabot kenabot && \
    chown -R kenabot:kenabot /kena
USER kenabot

COPY build/libs/KenaBot.jar Kenabot.jar

EXPOSE 5051 5052 5053 5054 5055

ENTRYPOINT java -Xmx8G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -jar Kenabot.jar
