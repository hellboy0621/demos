package com.example.gps.flink;

import com.example.gps.common.GpsMessage;
import com.example.gps.common.GpsMessageSerde;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class GpsFilterJob {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "gps.raw";
        String outputTopic = args.length > 2 ? args[2] : "gps.filtered";
        double minSpeedKmh = args.length > 3 ? Double.parseDouble(args[3]) : 80.0;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId("gps-filter-demo-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.builder()
                                .setTopic(outputTopic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build())
                .build();

        DataStream<String> filteredJson = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "gps-kafka-source")
                .map(GpsMessageSerde::fromJson)
                .filter(message -> isTargetMessage(message, minSpeedKmh))
                .map(GpsMessageSerde::toJson)
                .name("gps-filter-map");

        filteredJson.sinkTo(sink).name("gps-kafka-sink");

        env.execute("GPS Kafka Flink Filter Demo");
    }

    private static boolean isTargetMessage(GpsMessage message, double minSpeedKmh) {
        return message.getSpeedKmh() >= minSpeedKmh
                && message.getLatitude() >= 33.0
                && message.getLatitude() <= 38.0
                && message.getLongitude() >= 113.0
                && message.getLongitude() <= 118.0;
    }
}
