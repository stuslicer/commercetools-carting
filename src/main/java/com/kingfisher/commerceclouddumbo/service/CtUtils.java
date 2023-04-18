package com.kingfisher.commerceclouddumbo.service;

import com.commercetools.api.models.cart.TaxedItemPrice;
import com.commercetools.api.models.cart.TaxedPrice;
import com.commercetools.api.models.common.CentPrecisionMoney;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.LocalizedStringEntry;

public class CtUtils {

    public static String printPrice(CentPrecisionMoney money) {
        return String.format("%s %.2f", money.getCurrency().getCurrencyCode(),
                money.getCentAmount() / Math.pow(10, money.getFractionDigits() )
        );
    }

    public static String printPrice(TaxedPrice taxedPrice) {
        if( taxedPrice == null ) {
            return "";
        }
        return String.format("net: %s, tax: %s, total: %s",
                printPrice( taxedPrice.getTotalNet()  ),
                printPrice( taxedPrice.getTotalTax()  ),
                printPrice( taxedPrice.getTotalGross()  )
        );
    }

    public static String printPrice(TaxedItemPrice taxedPrice) {
        if( taxedPrice == null ) {
            return "";
        }
        return String.format("net: %s, tax: %s, total: %s",
                printPrice( taxedPrice.getTotalNet()  ),
                printPrice( taxedPrice.getTotalTax()  ),
                printPrice( taxedPrice.getTotalGross()  )
        );
    }

    public static String print(LocalizedString localizedString) {
        if( localizedString != null ) {
            return localizedString.stream()
                    .filter( u -> u != null)
                    .findFirst()
                    .map(LocalizedStringEntry::getValue)
                    .orElse(null);
        }
        return null;
    }

}
