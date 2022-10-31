package com.team6.onandthefarmproductservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaConfirmOrderDto {
    private List<ProductStockDto> productStockDtos;

    private String orderSerial;
}
