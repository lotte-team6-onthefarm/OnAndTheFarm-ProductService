package com.team6.onandthefarmproductservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmproductservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductOrderChannelAdapterKafkaImpl implements ProductOrderChannelAdapter {
    private final String TOPIC = "product-order";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ProductService productService;


    public void producer(String message) {
        this.kafkaTemplate.send(TOPIC, message);
    }

    @KafkaListener(topics = TOPIC)
    public void consumer(String message, Acknowledgment ack) {
        log.info(String.format("Message Received : %s", message));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Object> productStockDtos = objectMapper.readValue(message, List.class);
            for(Object productStockDto : productStockDtos){
                productService.updateStockAndSoldCount(productStockDto);
            }
            // Kafka Offset Manual Commit(수동커밋)
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
