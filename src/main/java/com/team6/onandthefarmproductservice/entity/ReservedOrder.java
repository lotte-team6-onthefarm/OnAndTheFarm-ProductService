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

    private String orderSerial;

    private String productList;

    private String createdDate;

    private String expireTime;

    private String status; // cancel / confirm 상태

    private String idempoStatus; // 멱등성을 위한 상태 / true : 처리된 메시지 false : 미처리된 메시지

    public void validate() {
        validateStatus();
        //validateExpired();
    }
    private void validateStatus() {
        if(this.getStatus()==null) return;
        if(this.getStatus().equals("CANCEL") || this.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Invalidate Status");
        }
    }
    private void validateExpired() {
        Integer year = Integer.valueOf(this.expireTime.substring(0,4));
        Integer month = Integer.valueOf(this.expireTime.substring(5,7));
        Integer day = Integer.valueOf(this.expireTime.substring(8,10));
        Integer hh = Integer.valueOf(this.expireTime.substring(11,13));
        Integer mm = Integer.valueOf(this.expireTime.substring(14,16));
        Integer ss = Integer.valueOf(this.expireTime.substring(17,19));

        if(LocalDateTime.now().isAfter(LocalDateTime.of(year,month,day,hh,mm,ss))) {
            log.error("!!!time out!!!");
            throw new IllegalArgumentException("Expired");
        }
    }
}
