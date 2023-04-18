package com.kingfisher.commerceclouddumbo;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.LineItem;
import com.commercetools.api.models.common.CentPrecisionMoney;
import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductProjection;
import com.kingfisher.commerceclouddumbo.service.CartTrial;
import com.kingfisher.commerceclouddumbo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Runner implements CommandLineRunner {

    private ProductService productService;
    private CartTrial cartTrial;

    public Runner(ProductService productService, CartTrial cartTrial) {
        this.productService = productService;
        this.cartTrial = cartTrial;
    }

    @Override
    public void run(String... args) throws Exception {

        Optional<Product> productByKey = productService.getProductByKey("71522");

        productByKey.ifPresent( System.out::println );

        List<ProductProjection> products = productService.findProducts();

//        List<Product> productsWithTaxCategory = productService.getProductsWithTaxCategory();
//        List<ProductProjection> productsWithTaxCategory1 = productService.findProductsWithTaxCategory();
//        productService.listProductProjections(productsWithTaxCategory1);

        System.out.println(products);

        Cart cart;
//        Cart cart = cartTrial.createCart();
         // 1. Create Cart
        final String CART_ID = "1bb01b25-b961-422a-915e-e6a85f5a38da";
        final String ITEM_ID = "64544219-d6cb-4680-ad20-b09ce0a5c088";
        final Long VARIANT_ID = 1L;
        final String ITEM_ID_2 = "e616c662-bfe6-42b8-8cbf-27ec3fe200e1";
        final Long VARIANT_ID_2 = 1L;
        final String ITEM_KEY = "30021";

        // 2. Lookup cart
        cart = cartTrial.findCartById( CART_ID ).get();
        long cartVersion = cart.getVersion();

        // 3. Add item to cart
//        cartTrial.addItemToCart( CART_ID, 1,"64544219-d6cb-4680-ad20-b09ce0a5c088", 1L, 2 );

        // 4. Add a shipping address (cart level - just the one)
//        cartTrial.addShippingAddress( CART_ID, cartVersion, "SHIPPING", "Arthur", "Dent", "18", "Oak Road", "York", "YO1 2QL", "GB");

        Optional<LineItem> itemFromCart = cartTrial.getItemFromCart(CART_ID, ITEM_ID, VARIANT_ID);
        System.out.println(itemFromCart);

        // 5. Set quantity for a specific line item
//        cartTrial.setItemQuantityinCart(CART_ID, cartVersion, itemFromCart.get().getId(), 3);

        // 6. Add another item to cart
//        cart = cartTrial.addItemToCart( CART_ID, cartVersion,ITEM_ID_2, VARIANT_ID_2, 3 );
//        cartVersion = cart.getVersion();

        // 7. Remove item from cart
        cart = cartTrial.removeItemFromCart( CART_ID, cartVersion, ITEM_ID_2, VARIANT_ID_2, 0L );
        cartVersion = cart.getVersion();

        cart = cartTrial.findCartById( CART_ID ).get();
        cartTrial.printCart( cart );

        // cart id - 1bb01b25-b961-422a-915e-e6a85f5a38da

        Optional<String> cartByIdAsString = cartTrial.getCartByIdAsString(CART_ID, false);
        System.out.println(cartByIdAsString.orElse(""));

    }
}
