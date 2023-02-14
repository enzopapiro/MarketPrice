package org.enzopapiro.marketprice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This class represents some configuration that is needed by the gateway.
 */
public class MarketPriceGatewayConfiguration {
    private List<String> subscriptionSymbols;
    private long idGeneratorProcessId;
    private int ringbuffersize;
    private int cacheWriteWinProbability;

    public void load() throws IOException {

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("marketpricegateway.properties")) {
            properties.load(input);
        }

        String listOfStrings = properties.getProperty("symbol.subscriptions");
        subscriptionSymbols = Arrays.stream(listOfStrings.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        idGeneratorProcessId = Long.parseLong(properties.getProperty("id.gen.process.id","1"));

        ringbuffersize = Integer.parseInt(properties.getProperty("ringbuffer.size","4096"));

        cacheWriteWinProbability = Integer.parseInt(properties.getProperty("cache.write.win.probability", "50"));
    }

    public List<String> getSubscriptionSymbols(){
        return subscriptionSymbols;
    }

    public long getIdGeneratorProcessId() {
        return idGeneratorProcessId;
    }

    public int getRingbufferSize() {
        return ringbuffersize;
    }
    public int getCacheWriteWinProbability() {
        return cacheWriteWinProbability;
    }
}
