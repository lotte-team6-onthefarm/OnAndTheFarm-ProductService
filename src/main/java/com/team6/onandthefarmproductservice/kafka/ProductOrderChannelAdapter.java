package com.team6.onandthefarmproductservice.kafka;

import org.springframework.kafka.support.Acknowledgment;

public interface ProductOrderChannelAdapter {
    void producer(String message);

    void consumer(String message, Acknowledgment ack) throws Exception;
}
