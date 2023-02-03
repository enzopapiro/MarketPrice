package org.enzopapiro.marketprice.util.subscriber;

/**
 * Assumption:
 * This is the interface that a subscriber uses to register a callback with the ECN/Transport mechanism,
 * typically the registration would involve some kind of symbol subscription list, so for example if
 * we wanted EURUSD and USDJPY only, we would register our interest in those symbols with transport API and
 * the transport would only deliver the relevant market data.
 */
public interface MarketPriceSubscriber {

    /**
     * Takes a CharSequence instead of String so that the caller is not forced to construct and an immutable String thus
     * helping us to keep GC overhead low.
     *
     * @param seq
     */
    public void onMessage(CharSequence seq);
}
