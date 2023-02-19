package org.enzopapiro.marketprice.service;

import org.enzopapiro.marketprice.domain.Price;

/**
 * This interface is for implementing classes that are registered with the MarketPriceManager to perform some
 * action when a price is to be published by the manager.
 */
public interface MarketPriceAction {
    void onPricePublish(PublishReason reason, Price price);
}
