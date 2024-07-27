FROM maven:3.8.6-openjdk-8 as postgresPlugin
WORKDIR  /home/nacos/plugins
ARG DATASOURCE_PLUGIN
ADD . .
# 执行maven打包
RUN --mount=type=cache,target=~/.m2/repository \
#    mvn clean install  -Dmaven.test.skip=true -Dmaven.source.skip=true \
    mv ./${DATASOURCE_PLUGIN}/target/${DATASOURCE_PLUGIN}*.jar /home/nacos/plugins/${DATASOURCE_PLUGIN}.jar

FROM alpine:latest
# 安装依赖
RUN apk add --no-cache openjdk8-jre-base curl iputils ncurses vim libcurl
ARG NACOS_VERSION
ARG DATASOURCE_PLUGIN
COPY --from=postgresPlugin /home/nacos/plugins/${DATASOURCE_PLUGIN}.jar /home/nacos/plugins/${DATASOURCE_PLUGIN}.jar
ENV DOWNLOAD_URL="https://github.com/alibaba/nacos/releases/download/${NACOS_VERSION}/nacos-server-${NACOS_VERSION}.tar.gz"
# set environment
ENV MODE="cluster" \
    PREFER_HOST_MODE="ip"\
    BASE_DIR="/home/nacos" \
    CLASSPATH=".:/home/nacos/conf:$CLASSPATH" \
    CLUSTER_CONF="/home/nacos/conf/cluster.conf" \
    FUNCTION_MODE="all" \
    JAVA_HOME="/usr/lib/jvm/java-1.8-openjdk" \
    NACOS_USER="nacos" \
    JAVA="/usr/lib/jvm/java-1.8-openjdk/bin/java" \
    JVM_XMS="1g" \
    JVM_XMX="1g" \
    JVM_XMN="512m" \
    JVM_MS="128m" \
    JVM_MMS="320m" \
    NACOS_DEBUG="n" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai"
WORKDIR $BASE_DIR
RUN \
    # 软件源配置
    sed -i 's#dl-cdn.alpinelinux.org#mirrors.aliyun.com#g' /etc/apk/repositories; \
    apt update -y && apt install -y wget;\
    ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone; \
    # 设置ll
    echo "alias ll='ls $LS_OPTIONS -l'" >> /root/.bashrc &&  . /root/.bashrc;
RUN wget "$DOWNLOAD_URL" -O nacos-server.tar.gz && tar -xvf nacos-server.tar.gz --strip-components=1 -C ./ && rm -rf *.gz;
RUN mkdir -p logs 	&& touch logs/start.out 	&& ln -sf /dev/stdout start.out 	&& ln -sf /dev/stderr start.out

ADD conf/application.properties conf/application.properties
ADD bin/docker-startup.sh bin/docker-startup.sh
RUN chmod +x bin/docker-startup.sh
EXPOSE 8848
ENTRYPOINT ["sh","bin/docker-startup.sh"]

