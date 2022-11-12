package com.team6.onandthefarmproductservice.kafka.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    private Long reserved_order_id;

    private String created_date;

    private String expire_time;

    private String order_serial;

    private String product_list;

    private String status;

    private String idempo_status;
}
