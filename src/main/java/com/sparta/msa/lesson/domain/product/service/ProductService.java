package com.sparta.msa.lesson.domain.product.service;

import com.sparta.msa.lesson.domain.product.dto.request.ProductRequest;
import com.sparta.msa.lesson.domain.product.dto.response.ProductResponse;
import com.sparta.msa.lesson.domain.product.entity.Product;
import com.sparta.msa.lesson.domain.product.mapper.ProductMapper;
import com.sparta.msa.lesson.domain.product.repository.ProductRepository;
import com.sparta.msa.lesson.global.enums.DomainExceptionCode;
import com.sparta.msa.lesson.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional
  public ProductResponse create(ProductRequest request) {
    Product product = productMapper.toEntity(request);
    product.validateStock();
    Product savedProduct = productRepository.save(product);
    return productMapper.toProductResponse(savedProduct);
  }

  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new DomainException(
        DomainExceptionCode.NOT_FOUND));

    return productMapper.toProductResponse(product);
  }

}
