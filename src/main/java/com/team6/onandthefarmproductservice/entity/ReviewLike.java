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
        name="REVIEW_LIKE_SEQ_GENERATOR",
        sequenceName = "REVIEW_LIKE_SEQ",
        initialValue = 100000, allocationSize = 1
)
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "REVIEW_LIKE_SEQ_GENERATOR")
    private Long reviewLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId")
    private Review review;

    private Long userId;

}
