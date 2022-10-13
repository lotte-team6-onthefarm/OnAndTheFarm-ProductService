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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    private Long userId;

    private Long sellerId;

    private String reviewContent;

    private String reviewCreatedAt;

    private String reviewModifiedAt;

    private Integer reviewLikeCount;

    private Integer reviewRate;

    private String reviewStatus;

    private Long orderProductId;


    public Long updateReview(String reviewContent, Integer reviewRate){
        this.reviewContent = reviewContent;
        this.reviewRate = reviewRate;

        return reviewId;
    }
}
