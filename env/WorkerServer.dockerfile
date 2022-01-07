FROM jboss/wildfly
COPY war/worker.war /opt/jboss/wildfly/standalone/deployments/
ARG PROFILE="default"
ENV JAVA_OPTS="-Dspring.profiles.active=$PROFILE"
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]