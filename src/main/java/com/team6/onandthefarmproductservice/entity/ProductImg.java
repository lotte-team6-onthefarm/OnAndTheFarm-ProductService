package com.team6.onandthefarmproductservice.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class ProductImg {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productImgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    private String productImgSrc;

    @Builder
    public ProductImg(String productImgSrc, Product product){
        this.productImgSrc = productImgSrc;
        this.product = product;
    }

    public void updateProductImg(String productImgSrc) {
        this.productImgSrc = productImgSrc;
    }
}
