package com.team6.onandthefarmproductservice.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

import com.team6.onandthefarmproductservice.entity.ProductDocument;
import com.team6.onandthefarmproductservice.vo.PageVo;
import com.team6.onandthefarmproductservice.vo.product.ProductSearchInfoResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductSearchQueryRepository {
	private final ElasticsearchOperations operations;

	public ProductSearchInfoResponse findByStartWithProductName(String productName, PageRequest pageRequest) {
		List<ProductDocument> productDocuments = new ArrayList<>();
		Criteria basic = Criteria.where("categoryName").contains(productName);
		Criteria criteria = Criteria.where("name").contains(productName).and(basic);
		CriteriaQuery query = new CriteriaQuery(criteria);
		query.setPageable(pageRequest);
		SearchHits<ProductDocument> search = operations.search(query, ProductDocument.class);
		SearchPage<ProductDocument> searchPage = SearchHitSupport.searchPageFor(search, query.getPageable());

		PageVo pageVo = new PageVo();
		pageVo.setTotalElement(searchPage.getTotalElements());
		pageVo.setTotalPage(searchPage.getTotalPages());
		pageVo.setNowPage(pageRequest.getPageNumber());

		for (SearchHit<ProductDocument> productDocumentSearchHit : search) {
			productDocuments.add(productDocumentSearchHit.getContent());
		}

		ProductSearchInfoResponse productSearchInfoResponse = ProductSearchInfoResponse.builder()
				.productDocuments(productDocuments)
				.pageVo(pageVo)
				.build();
		return productSearchInfoResponse;
	}
}
