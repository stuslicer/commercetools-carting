package com.kingfisher.commerceclouddumbo.integration;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.*;
import com.commercetools.api.models.tax_category.TaxCategory;
import com.kingfisher.commerceclouddumbo.service.CtUtils;
import com.kingfisher.commerceclouddumbo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class CTProductService implements ProductService {

    private ProjectApiRoot apiRoot;

    @Autowired
    public CTProductService(ProjectApiRoot apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override
    public Optional<Product> getProductById(String id) {
        Product product = apiRoot.products()
                .withId(id)
                .get()
                .executeBlocking()
                .getBody();
        return Optional.ofNullable(product);
    }

    @Override
    public Optional<Product> getProductByKey(String key) {
        Product product = apiRoot.products()
                .withKey(key)
                .get()
                .executeBlocking()
                .getBody();
        return Optional.ofNullable(product);
    }



    @Override
    public List<Product> getProductsWithTaxCategory() {
        ProductPagedQueryResponse products = apiRoot.products()
                .get()
                .withWhere("taxCategory = :category", "category", "" )
                .executeBlocking()
                .getBody();

        return products.getResults();
    }

    public Optional<TaxCategory> getTaxCategory() {
        TaxCategory products = apiRoot.taxCategories()
                .withKey("VAT-S")
                .get()
                .executeBlocking()
                .getBody();

        return Optional.ofNullable(products);
    }

    @Override
    public void listProductProjections(List<ProductProjection> projections) {
        for (ProductProjection projection : projections) {
            System.out.printf("%s - %s - %s %n", projection.getId(), projection.getKey(),
                    CtUtils.print( projection.getName() ));
        }
    }

    @Override
    public List<ProductProjection> findProductsWithTaxCategory() {
        ProductProjectionPagedSearchResponse response = apiRoot.productProjections()
                .search()
                .get()
                .withLimit(200)
//                .addFilterQuery("taxCategory.id:{\"86a4fd46-8002-423a-95fc-d120d40e7a02\"}")
                .addFilterQuery("taxCategory.id:exists")
                .executeBlocking()
                .getBody();
        System.out.println(response.getTotal());
        return response.getResults();
    }

    @Override
    public List<ProductProjection> findProducts() {
//        ProductPagedQueryResponse productList = apiRoot.products()
//                .get()
//                .withWhere("masterData.current.name = :name", "name", "Sumo Traditional Wrecking Bar 18\"")
//                .executeBlocking()
//                .getBody();
        ProductProjectionPagedSearchResponse response = apiRoot.productProjections()
                .search()
                .get()
                .addFilterQuery("categories.id:subtree(\"8b0a3aab-de89-44f4-9f8c-6aeb06697e81\")")
                .addFilterQuery("variants.price.centAmount:range (* to 3000)")
                .executeBlocking()
                .getBody();
        System.out.println(response.getTotal());
        return response.getResults();
    }

}
