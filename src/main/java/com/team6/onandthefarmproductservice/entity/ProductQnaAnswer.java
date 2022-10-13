package com.team6.onandthefarmproductservice.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
public class ProductQnaAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productQnaAnswerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productQnaId")
    private ProductQna productQna;

    private String productQnaAnswerContent;

    private String productQnaAnswerCreatedAt;

    private String productQnaAnswerModifiedAt;
}
