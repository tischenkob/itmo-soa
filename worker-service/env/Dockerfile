FROM payara/micro
WORKDIR /opt
ADD war/worker.war worker.war
ENV PAYARA_ARGS --debug
ENV JVM_ARGS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9009
ENTRYPOINT java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9009 -jar /opt/payara/payara-micro.jar --deploy worker.war
