FROM hseeberger/scala-sbt:11.0.2_2.12.8_1.2.8

COPY . /app

WORKDIR /app

RUN sbt compile

EXPOSE 9000

CMD sbt run
