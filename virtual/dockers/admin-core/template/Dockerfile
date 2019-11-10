FROM life-admin:${admin.version}
MAINTAINER devops <apolyakov@bivgroup.com>
COPY war/ /opt/jboss/wildfly/standalone/deployments/
COPY admin/ /data/html/admin/
COPY config/ /data/
ADD standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml