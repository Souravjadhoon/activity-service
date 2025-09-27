package com.activity_service.activity_service.kafka;

import com.activity.avro.ActivityIngestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, ActivityIngestionResponse> kafkaTemplate;

    public  void sendMessageToKafka(String requestId, String topicName, ActivityIngestionResponse activityIngestionResponse){
        System.out.println("sending start");
        System.out.println("Sending Avro: " + activityIngestionResponse.toString());
        kafkaTemplate.send(topicName, requestId, activityIngestionResponse)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("✅ Kafka message sent: offset={} " +result.getRecordMetadata().offset());
                    } else {
                        System.out.println(("❌ Kafka send failed"+ ex));
                    }
                });
        System.out.println(("sending end"));




    }
}
