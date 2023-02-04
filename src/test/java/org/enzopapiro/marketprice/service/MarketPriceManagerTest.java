package org.enzopapiro.marketprice.service;

import org.enzopapiro.marketprice.domain.Price;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MarketPriceManagerTest {
    @Test
    public void testMarginCalculation(){
        Price price = MarketPriceManager.applyBidAskMargin(new Price(), 11960, 11990, 2);
        Assertions.assertEquals(11948L,price.getBid().getValue());
        Assertions.assertEquals(12002L,price.getAsk().getValue());
    }
}
