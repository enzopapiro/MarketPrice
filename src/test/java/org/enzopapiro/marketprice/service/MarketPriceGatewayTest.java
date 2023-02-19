package org.enzopapiro.marketprice.service;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/marketprice/service", glue = "org.enzopapiro.marketprice.service", plugin = { "pretty", "html:target/cucumber" })
public class MarketPriceGatewayTest {
}
