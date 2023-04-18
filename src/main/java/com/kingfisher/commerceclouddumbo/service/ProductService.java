package com.kingfisher.commerceclouddumbo.service;

import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductProjection;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public Optional<Product> getProductById(String id);

    public Optional<Product> getProductByKey(String key);

    void listProductProjections(List<ProductProjection> projections);

    List<ProductProjection> findProductsWithTaxCategory();

    public List<ProductProjection> findProducts();

}
