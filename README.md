# Flink + Kafka GPS Demo（Maven 多模块）

这是一个 Java Maven 多模块示例项目，包含：

1. `gps-producer`：模拟产生 GPS 数据并发送到 Kafka 输入 Topic。  
2. `flink-gps-filter-job`：Flink 流任务消费输入 Topic，按条件过滤后写入输出 Topic。  
3. `gps-common`：公共模型与 JSON 序列化工具。

## 目录结构

```text
.
├── pom.xml
├── gps-common
├── gps-producer
└── flink-gps-filter-job
```

## 默认 Topic

- 输入：`gps.raw`
- 输出：`gps.filtered`

## 过滤规则

Flink 任务会保留同时满足以下条件的消息：

- 速度 `speedKmh >= 80`
- 纬度在 `[33, 38]`
- 经度在 `[113, 118]`

## 构建

```bash
mvn clean package
```

## 启动生产者（模拟 GPS 消息）

```bash
mvn -pl gps-producer exec:java \
  -Dexec.args="localhost:9092 gps.raw 1000"
```

参数含义：

1. `bootstrapServers`（默认 `localhost:9092`）
2. `topic`（默认 `gps.raw`）
3. `intervalMs` 发送间隔毫秒（默认 `1000`）

## 运行 Flink 过滤任务

先打包：

```bash
mvn -pl flink-gps-filter-job -am package
```

提交到 Flink 集群（示例）：

```bash
flink run flink-gps-filter-job/target/flink-gps-filter-job-1.0.0-SNAPSHOT.jar \
  localhost:9092 gps.raw gps.filtered 80
```

参数含义：

1. `bootstrapServers`（默认 `localhost:9092`）
2. `inputTopic`（默认 `gps.raw`）
3. `outputTopic`（默认 `gps.filtered`）
4. `minSpeedKmh`（默认 `80`）

## 本地快速验证（可选）

如果你有本地 Kafka，可在另一个终端查看输出 Topic：

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic gps.filtered --from-beginning
```

