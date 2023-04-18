package com.kingfisher.commerceclouddumbo.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.AddressDraft;
import com.commercetools.api.models.common.AddressDraftBuilder;
import com.commercetools.api.models.product.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Cart items - products
 * 57b1673c-5815-4100-adc7-b8e3c2c36261 - Evo-Stik White for Life Sealant 295ml                   - 56846    - £5.43
 * 83c0cbe6-d2c1-4100-b8e2-cc383a06da63 - Quicksilver® Countersunk Prodrive® 8 x 5/8" Pack of 200 - 19850    - £1.28   >5 £1.02 >15 £ 0.96
 * 3819ae64-b2e3-4100-8708-f7be3bc41158 - No Nonsense Contact Adhesive 500ml                      - 97225    - £3.51
 * e8b38d2a-8c7b-4100-b2f9-a1c225b445e2 - Galvanised 4 Shelf Racking Unit                         - 85560    - £357.89
 */
@Component
public class CartTrial {

    private ProjectApiRoot apiRoot;
    private ProductService productService;
    private ObjectMapper objectMapper;

    public CartTrial(ProjectApiRoot apiRoot, ProductService productService, ObjectMapper objectMapper) {
        this.apiRoot = apiRoot;
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    public Optional<Cart> findCartById(String cartId) {
        Cart cart = apiRoot.carts()
                .withId(cartId)
                .get()
                .executeBlocking()
                .getBody();

        return Optional.ofNullable(cart);
    }

    /**
     * Gets the cart as a prettified JSON String.
     * @param cartId
     * @return
     */
    public Optional<String> getCartByIdAsString(String cartId) {
        return getCartByIdAsString(cartId, true);
    }

    /**
     * Gets the cart as a JSON String.
     * String can be prettified.
     * @param cartId
     * @param prettyPrint
     * @return
     */
    public Optional<String> getCartByIdAsString(String cartId, boolean prettyPrint) {
        byte[] cart = apiRoot.carts()
                .withId(cartId)
                .get()
                .sendBlocking()
                .getBody();

        if( cart == null || cart.length == 0 ) {
            return Optional.empty();
        }
        if( prettyPrint ) {
            try {
                Object asObject = objectMapper.readValue(cart, Object.class);
                return Optional.of(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(asObject));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.of( new String(cart) );
    }

    public Cart createCart() {
        // Create a new Cart

        CartDraft cartDraft = CartDraftBuilder.of()
                .currency("GBP")
                .country("GB")
                .locale("en-GB")
//                .anonymousId("anonymous-basket-stu")
                .taxRoundingMode( RoundingMode.HALF_DOWN )
                .build();
        // Indicate it's anonymous

        Cart cart = apiRoot.carts().create(cartDraft)
                .executeBlocking()
                .getBody();

        return cart;
    }

    /**
     * Add an item to the cart with the quantity.
     * Variant is required because possible to add the same product with different variants.
     * @param cartId
     * @param cartVersion
     * @param itemId
     * @param variant
     * @param quantity
     * @return
     */
    public Cart addItemToCart(String cartId, long cartVersion, String itemId, Long variant, long quantity) {

        CartUpdate cartUpdate = CartUpdateBuilder.of()
                .version(cartVersion)
                .plusActions(actionBuilder ->
                        actionBuilder.addLineItemBuilder()
                                .productId(itemId)
                                .variantId(variant)
                                .quantity(quantity)
                )
                .build();

        return updateCartWithActions(cartId, cartUpdate);
    }

    /**
     * Remove an item the cart, effectively reduces the number of items by given quantity.
     * If quantity is zero then removes the entire item line.
     * @param cartId
     * @param cartVersion
     * @param itemId
     * @param variant
     * @param quantity
     * @return
     */
    public Cart removeItemFromCart(String cartId, long cartVersion, String itemId, Long variant, Long quantity) {

        Optional<LineItem> itemFromCart = getItemFromCart(cartId, itemId, variant);

        if( itemFromCart.isPresent() ) {

            CartUpdate cartUpdate = CartUpdateBuilder.of()
                    .version(cartVersion)
                    .plusActions(actionBuilder -> {
                                // set quantity to 0 doesn't appear to work - leave off does.
                                return quantity == 0L ?
                                        actionBuilder.removeLineItemBuilder()
                                            .lineItemId(itemFromCart.get().getId())
                                        :
                                        actionBuilder.removeLineItemBuilder()
                                            .lineItemId(itemFromCart.get().getId())
                                            .quantity(quantity)
                                        ;
                            }
                    )
                    .build();

            return updateCartWithActions(cartId, cartUpdate);
        }
        return findCartById(cartId).get();
    }

    /**
     * Sets the quantity for the given cart line item.
     * The line item has to exist.
     * @param cartId
     * @param cartVersion
     * @param lineItemId
     * @param quantity
     * @return
     */
    public Cart setItemQuantityinCart(String cartId, long cartVersion, String lineItemId, long quantity) {

        CartUpdate cartUpdate = CartUpdateBuilder.of()
                .version(cartVersion)
                .plusActions(actionBuilder ->
                        actionBuilder.changeLineItemQuantityBuilder()
                                .lineItemId(lineItemId)
                                .quantity(quantity)
                )
                .build();

        return updateCartWithActions(cartId, cartUpdate);
    }

    /**
     * Gets the cart line item for given product and variant.
     * @param cartId
     * @param itemId
     * @param variantId
     * @return
     */
    public Optional<LineItem> getItemFromCart(String cartId, String itemId, long variantId) {

        // validate
        Cart body = apiRoot.carts()
                .withId(cartId)
                .get()
                .executeBlocking()
                .getBody();

        List<LineItem> candidates = body.getLineItems().stream()
                .filter(item -> item.getProductId().equals(itemId) && item.getVariant().getId().equals(variantId))
                .toList();

        // MAY NEED TO LOOK AT SHPPING METHOD ETC

        return candidates.isEmpty() ? Optional.ofNullable( null ) : Optional.of( candidates.get(0));
    }

    public Cart addShippingAddress(String cartId, long cartVersion,
                                   String key, String firstName, String lastName,
                                   String streetNumber, String streetName, String city, String postalCode, String country) {

        AddressDraft draft = AddressDraftBuilder.of()
                .key(key)
                .firstName(firstName)
                .lastName(lastName)
                .streetNumber(streetNumber)
                .streetName(streetName)
                .city(city)
                .postalCode(postalCode)
                .country(country)
                .build();

        CartUpdate cartUpdate = CartUpdateBuilder.of()
                .version(cartVersion)
                .plusActions(actionBuilder ->
                        actionBuilder.setShippingAddressBuilder()
                                .address(draft)
                )
                .build();

        return updateCartWithActions(cartId, cartUpdate);
    }

    /**
     * Print out the cart in all its glory...
     * @param cart
     */
    public void printCart(Cart cart) {
        List<LineItem> lineItems = cart.getLineItems();
        System.out.printf("Cart id: %s - %d %n", cart.getId(), cart.getVersion());
        System.out.printf("State: %s %n", cart.getCartState());
        System.out.printf("Items: %d, total: %d %n", cart.getLineItems().size(), cart.getTotalLineItemQuantity());
        System.out.printf("Total price: %s %n", CtUtils.printPrice(cart.getTotalPrice()));
        System.out.printf("Tax price: %s %n", CtUtils.printPrice(cart.getTaxedPrice()));
        for (LineItem lineItem : lineItems) {
            Optional<Product> productById = productService.getProductById(lineItem.getProductId());
            productById.ifPresent((product) -> {
                System.out.printf("%s - %s - %s - %d -  %s%n", product.getId(), CtUtils.print(product.getMasterData().getCurrent().getName()),
                       product.getKey(),
                       lineItem.getQuantity(),
                       CtUtils.printPrice( lineItem.getTotalPrice() ) );
            });

        }
    }

    private Cart updateCartWithActions(String cartId, CartUpdate cartUpdate) {
        final Cart updatedCart = apiRoot.carts()
                .withId(cartId)
                .post(cartUpdate)
                .executeBlocking()
                .getBody();

        return updatedCart;
    }



}
