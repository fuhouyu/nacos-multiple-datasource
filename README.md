### Nacos Multiple Datasource Plugin

#### 简介

提供Nacos的其它数据源版本，目前暂适配Postgresql。

#### 支持版本

| Nacos版本 | 适配数据库 |                          初始化SQL                           |
| :-------: | :--------: | :----------------------------------------------------------: |
|  >=2.4.0  | postgresql | [init sql](./nacos-postgres-plugin/src/main/resources/db/v2.4.0-postgres-init.sql) |

#### Docker部署

##### 使用已构建好的镜像

* 镜像地址：[fuhouyu/nacos-server-multiple-datasource - Docker Image | Docker Hub](https://hub.docker.com/r/fuhouyu/nacos-server-multiple-datasource/tags)

```shell
export NACOS_TAG=fuhouyu/nacos-server-multiple-datasource:2.4.0.1-postgresql
export POSTGRES_IP=127.0.0.1
export POSTGRES_PORT=5432
export POSTGRES_USERNAME=nacos
export POSTGRES_PASSWORD=nacos
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

* 若服务因网络无法下载docker hub 镜像，可从github上下载相应tar进行处理

例：

```shell
docker load -i nacos-server-multiple-datasource/2.4.0.1-postgresql-arm64.tar
export NACOS_TAG=fuhouyu/nacos-server-multiple-datasource:2.4.0.1-postgresql
export POSTGRES_IP=127.0.0.1
export POSTGRES_PORT=5432
export POSTGRES_USERNAME=nacos
export POSTGRES_PASSWORD=nacos
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



##### 手动构建

```shell
export DATASOURCE_PLUGIN=nacos-postgres-plugin
export NACOS_TAG=test:0.0.1
docker build  --build-arg DATASOURCE_PLUGIN=$DATASOURCE_PLUGIN -t $NACOS_TAG .
```

#### Kubernetes 

* 示例

```yaml
# 提供外部使用
apiVersion: v1
kind: Service
metadata:
  name: nacos-nodeport
  namespace: default
  labels:
    app: nacos-headless
spec:
  type: NodePort
  ports:
    - port: 8848
      name: server
      targetPort: 8848
      nodePort: 30048
    - port: 9848
      name: client-rpc
      targetPort: 9848
      nodePort: 31048
  selector:
    app: nacos
---
# 无头服务，提供容器内部使用
apiVersion: v1
kind: Service
metadata:
  name: nacos-headless
  namespace: default
  labels:
    app: nacos-headless
spec:
  type: ClusterIP
  clusterIP: None
  ports:
    - port: 8848
      name: server
      targetPort: 8848
    - port: 9848
      name: client-rpc
      targetPort: 9848
    - port: 9849
      name: raft-grpc
      targetPort: 9849
    - port: 7848
      name: raft-rpc
      targetPort: 7848
  clusterIP: None
  selector:
    app: nacos
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: nacos-cm
  namespace: default
data:
# 这里修改jdbc数据源配置
  db.url: "jdbc:postgresql://ip/port"
  db.username: "nacos"
  db.password: "nacos"
  db.driver.name: "org.postgresql.Driver"

---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nacos
  namespace: common-component
spec:
  serviceName: nacos-headless
  replicas: 1
  template:
    metadata:
      labels:
        app: nacos
      annotations:
        pod.alpha.kubernetes.io/initialized: "true"
    spec:
      containers:
        - name: k8snacos
          imagePullPolicy: Always
          image: fuhouyu/nacos-server-multiple-datasource:2.4.0.1-postgresql
          # 根据需要修改
          resources:
            requests:
              memory: "2048Mi"
              cpu: "1024m"
            limits:
              memory: "2048Mi"
              cpu: "1024m"
          # Nacos 集群，默认3节点
          env:
            - name: NACOS_REPLICAS
              value: "3"
            - name: DB_URL
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: db.url
            - name: DB_USER
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: db.username
            - name: DB_PASSWORD
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: db.password
            - name: DRIVER_CLASS_NAME
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: db.driver.name
            - name: MODE
              value: "cluster"
            - name: NACOS_SERVER_PORT
              value: "8848"
            - name: PREFER_HOST_MODE
              value: "hostname"
            - name: nacos.naming.data.warmup
              value: "false"
            - name: NACOS_SERVERS
              value: "nacos-0.nacos-headless.default.svc.cluster.local:8848,nacos-1.nacos-headless.default.svc.cluster.local:8848,nacos-2.nacos-headless.default.svc.cluster.local:8848" 
            - name: NACOS_AUTH_TOKEN
              value: NkQzNUUwMjlCOTdGNDk4Mjg2QTJEN0E4RDYzM0EyMzE=
            - name: NACOS_AUTH_IDENTITY_KEY
              value: AE84C5193B6C44728C76AEB73D1A3037
            - name: NACOS_AUTH_IDENTITY_VALUE
              value: D3D2F9F96E58435E802FC34017722057
            - name: SPRING_DATASOURCE_PLATFORM  
              value: "postgres"
            - name: NACOS_AUTH_ENABLE
              value: "true"
  selector:
    matchLabels:
      app: nacos
```

