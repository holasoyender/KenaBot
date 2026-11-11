FROM rust:1.75.0 as builder

RUN USER=root cargo new --bin kena
WORKDIR /kena

RUN rustup override set nightly

##opus needs cmake, smh
RUN apt-get update && apt-get install -y cmake

ADD . ./

RUN cargo build --release --profile release

# ===========================
FROM debian:12.4-slim

ARG APP=/usr/src/app

ENV TZ=Etc/UTC \
    APP_USER=pworker

RUN groupadd $APP_USER \
    && useradd -g $APP_USER $APP_USER \
    && mkdir -p ${APP}

COPY --from=builder /kena/target/release/kena ${APP}/kena

RUN chown -R $APP_USER:$APP_USER ${APP}
RUN ldd --version

RUN apt-get update && apt-get install -y openssl

# EXPOSE 8000

USER $APP_USER
WORKDIR ${APP}

ENV RUST_LOG=info

CMD ["./kena"]