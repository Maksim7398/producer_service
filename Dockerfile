FROM openjdk:17-oracle

WORKDIR /app

ADD target///////////// app.jar

CMD ["java","-jar","app.jar"]