FROM openjdk:8-jre-alpine
ADD target/ /opt/
WORKDIR /opt
EXPOSE 8182
ENTRYPOINT exec java $JAVA_OPTS -jar kafka-consumer.jar
