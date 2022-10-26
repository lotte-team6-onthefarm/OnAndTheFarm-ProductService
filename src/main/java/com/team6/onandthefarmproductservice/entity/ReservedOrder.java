package com.team6.onandthefarmproductservice.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 *  주문 생성 때 사용되는 분산 트랜잭션을 위한 예약 테이블
 */
public class ReservedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservedOrderId;

    private String productList;

    private LocalDateTime createdDate;

    private LocalDateTime expireTime;

    private String status;

    public void validate() {
        validateStatus();
        validateExpired();
    }
    private void validateStatus() {
        if(this.getStatus()==null) return;
        if(this.getStatus().equals("CANCEL") || this.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Invalidate Status");
        }
    }
    private void validateExpired() {
        if(LocalDateTime.now().isAfter(this.expireTime)) {
            throw new IllegalArgumentException("Expired");
        }
    }
}
