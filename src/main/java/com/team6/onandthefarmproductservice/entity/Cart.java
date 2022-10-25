package com.team6.onandthefarmproductservice.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SequenceGenerator(
        name="CART_SEQ_GENERATOR",
        sequenceName = "CART_SEQ",
        initialValue = 100000, allocationSize = 1
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "CART_SEQ_GENERATOR")
    private Long cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    private Long userId;

    private Integer cartQty;
    private Boolean cartIsActivated;
    private Boolean cartStatus;
    private String cartCreatedAt;
}
