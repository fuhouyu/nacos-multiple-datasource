### Nacos Multiple Datasource Plugin

#### SQL Init

* [Postres](./nacos-postgres-plugin/src/main/resources/db/v2.4.0-postgres-init.sql)

#### Docker

* build image

```shell
export NACOS_VERSION=2.4.0
export DATASOURCE_PLUGIN=nacos-postgres-plugin
export NACOS_TAG=test:0.0.1
docker build --build-arg NACOS_VERSION=$NACOS_VERSION  --build-arg DATASOURCE_PLUGIN=$DATASOURCE_PLUGIN -t $NACOS_TAG .
```

build arg

NACOS_VERSION: [Releases · alibaba/nacos (github.com)](https://github.com/alibaba/nacos/releases)

DATASOURCE_PLUGIN：nacos-postgres-plugin 即子服务的名称

* docker postgres plugin example

```shell
export NACOS_TAG=test:0.0.1
export POSTGRES_IP=127.0.0.1
export POSTGRES_PORT=5432
export POSTGRES_USERNAME=nacos
export POSTGRES_PASSWORD=nacos
export DATABASE=nacos
docker run -it --name nacos-quick \
-e MODE=standalone \
-e NACOS_AUTH_IDENTITY_KEY="d90ee0d2ef5e452f80c00514c7f30c3e" \
-e NACOS_AUTH_IDENTITY_VALUE="4a7397d7085945d3adcdcaec264abf90" \
-e NACOS_AUTH_TOKEN="N2YyYWZiODJkNWE1NDllNzljODNjYmQ5ZDBlODE3M2Q=" \
-e SPRING_DATASOURCE_PLATFORM="postgres" \
-e DB_URL="jdbc:postgresql://${POSTGRES_IP}:${POSTGRES_PORT}/${DATABASE}" \
-e DB_USER="${POSTGRES_USERNAME}" \
-e DB_PASSWORD="${POSTGRES_PASSWORD}" \
-e NACOS_AUTH_ENABLE="true" \
-e DRIVER_CLASS_NAME="org.postgresql.Driver" \
-p 8848:8848 \
-d $NACOS_TAG
```
