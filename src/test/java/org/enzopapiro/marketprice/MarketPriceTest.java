package org.enzopapiro.marketprice;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/marketprice/price", glue = "org.enzopapiro.marketprice", plugin = { "pretty", "html:target/cucumber" })
public class MarketPriceTest {
}
