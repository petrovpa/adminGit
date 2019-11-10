FROM ${wildfly.version}
MAINTAINER devops <apolyakov@bivgroup.com>

ENV JAVA_OPTS -Dlog4j.configurationFile=log4j2.xml -Xms1024m -Xmx4096m -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT
ENV TERM xterm

RUN apt-get update -y
RUN apt-get install mc nano nginx -y
RUN systemctl enable nginx.service
RUN /opt/jboss/wildfly/bin/add-user.sh	admin 123qweQWE --silent

COPY fonts/ /usr/share/fonts/
COPY config/ /data/
COPY log/ /opt/jboss/wildfly/standalone/configuration/

RUN rm -f /etc/nginx/sites-enabled/default
RUN rm -rf /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/*
COPY hibernate/ /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/
COPY modules/ /opt/jboss/wildfly/modules/
COPY nginx.conf /etc/nginx/conf.d/
COPY /smsCert /$JAVA_HOME/jre/lib/security
COPY admin/ /data/html/admin/
RUN cd /$JAVA_HOME/jre/lib/security && echo yes | keytool -keystore cacerts -importcert -alias gateway_api_sc_CA -file gateway_api_sc.pem -keystore cacerts -storepass changeit
RUN cd /$JAVA_HOME/jre/lib/security && echo yes | keytool -keystore cacerts -importcert -alias 3dsec_CA -file digicert.crt -keystore cacerts -storepass changeit
VOLUME ["/data/html/"]
EXPOSE 7000
COPY war/ /opt/jboss/wildfly/standalone/deployments/

EXPOSE 7000
