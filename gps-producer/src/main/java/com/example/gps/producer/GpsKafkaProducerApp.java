package com.example.gps.producer;

import com.example.gps.common.GpsMessage;
import com.example.gps.common.GpsMessageSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GpsKafkaProducerApp {

    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String topic = args.length > 1 ? args[1] : "gps.raw";
        long intervalMs = args.length > 2 ? Long.parseLong(args[2]) : 1000L;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));

        Random random = new Random();
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            while (running.get()) {
                GpsMessage message = nextMessage(random);
                String payload = GpsMessageSerde.toJson(message);
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, message.getDeviceId(), payload);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Send failed: " + exception.getMessage());
                    } else {
                        System.out.printf("sent topic=%s partition=%d offset=%d payload=%s%n",
                                metadata.topic(), metadata.partition(), metadata.offset(), payload);
                    }
                });
                producer.flush();
                Thread.sleep(Duration.ofMillis(intervalMs).toMillis());
            }
        }
    }

    private static GpsMessage nextMessage(Random random) {
        String deviceId = "device-" + (random.nextInt(5) + 1);
        double latitude = 30.0 + random.nextDouble() * 10.0;
        double longitude = 110.0 + random.nextDouble() * 10.0;
        double speedKmh = random.nextDouble() * 160.0;
        long eventTime = System.currentTimeMillis();
        return new GpsMessage(deviceId, latitude, longitude, speedKmh, eventTime);
    }
}
