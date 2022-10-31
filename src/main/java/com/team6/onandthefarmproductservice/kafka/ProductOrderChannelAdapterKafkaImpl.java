package com.team6.onandthefarmproductservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmproductservice.dto.product.KafkaConfirmOrderDto;
import com.team6.onandthefarmproductservice.dto.product.ProductStockDto;
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

    @KafkaListener(topics = TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String message, Acknowledgment ack) throws Exception{
        log.info(String.format("Message Received : %s", message));
        ObjectMapper objectMapper = new ObjectMapper();

        KafkaConfirmOrderDto kafkaConfirmOrderDto
                = objectMapper.readValue(message, KafkaConfirmOrderDto.class);

        if(!productService.isAlreadyProcessedOrderId(kafkaConfirmOrderDto.getOrderSerial())){
            ack.acknowledge(); // 중복일 경우 offset을 옮기고 commit한 뒤
            return; // 메시지 처리 종료
        }

        //Long test = Long.valueOf("adsads");
        for(ProductStockDto productStockDto : kafkaConfirmOrderDto.getProductStockDtos()){
            productService.updateStockAndSoldCount(productStockDto);
        }
        // Kafka Offset Manual Commit(수동커밋)
        ack.acknowledge();
    }
}
