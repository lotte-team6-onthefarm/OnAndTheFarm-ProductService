package com.team6.onandthefarmproductservice.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
        name="PRODUCT_QNA_SEQ_GENERATOR",
        sequenceName = "PRODUCT_QNA_SEQ",
        initialValue = 100000, allocationSize = 1
)
public class ProductQna {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "PRODUCT_QNA_SEQ_GENERATOR")
    private Long productQnaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    private Long userId;

    private Long sellerId;

    private String productQnaContent;

    private String productQnaCreatedAt;

    private String productQnaModifiedAt;

    private String productQnaStatus;

}
