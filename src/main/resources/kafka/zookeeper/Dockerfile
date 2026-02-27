#FROM paymetv/base
FROM alpine:3.14.10

LABEL authors="richardrenaud"

ENV ZOOKEEPER_VERSION=3.9.4

#Download Zookeeper
RUN wget -q http://mirror.vorboss.net/apache/zookeeper/zookeeper-3.9.4/apache-zookeeper-3.9.4-bin.tar.gz && \
wget -q http://mirror.vorboss.net/apache/zookeeper/zookeeper-${ZOOKEEPER_VERSION}/apache-zookeeper-${ZOOKEEPER_VERSION}.tar.gz

#Install
RUN tar -xzf apache-zookeeper-${ZOOKEEPER_VERSION}.tar.gz -C /opt

#Configure
RUN mv /opt/apache-zookeeper-${ZOOKEEPER_VERSION}/conf/zoo_sample.cfg /opt/apache-zookeeper-${ZOOKEEPER_VERSION}/conf/zoo.cfg

ENV JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
ENV ZK_HOME=/opt/apache-zookeeper-${ZOOKEEPER_VERSION}
RUN sed  -i "s|/tmp/zookeeper|$ZK_HOME/data|g" $ZK_HOME/conf/zoo.cfg; mkdir $ZK_HOME/data

COPY start-zk.sh /usr/bin/start-zk.sh
RUN chmod +x /usr/bin/start-zk.sh

EXPOSE 2181 2888 3888

WORKDIR /opt/apache-zookeeper-${ZOOKEEPER_VERSION}
VOLUME ["/opt/apache-zookeeper-${ZOOKEEPER_VERSION}/conf", "/opt/apache-zookeeper-${ZOOKEEPER_VERSION}/data"]

CMD /usr/sbin/sshd && bash /usr/bin/start-zk.sh