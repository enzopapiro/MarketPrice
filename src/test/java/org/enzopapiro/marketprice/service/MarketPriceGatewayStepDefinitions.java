package org.enzopapiro.marketprice.service;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.enzopapiro.marketprice.domain.Price;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MarketPriceGatewayStepDefinitions implements MarketPriceAction {
    private MarketPriceGateway mpg;
    private Throwable e;
    private CountDownLatch receivedRatesLatch;
    private Map<PublishReason,Map<String,Price>> outputRates = new HashMap<>();
    private Price requestedPrice;

    @Before
    public void setup() {
    }

    @When("I start it")
    public void i_start_it() {
        try{
            mpg.start();
        } catch(Throwable e){
            this.e = e;
        }
    }

    @Given("I have a market price gateway service without actions")
    public void i_have_a_market_price_gateway_service_with_null_actions() {
        mpg = new MarketPriceGateway("TEST", null);
    }

    @Given("I have a market price gateway service with an empty actions array")
    public void i_have_a_market_price_gateway_service_with_an_empty_actions_array() {
        mpg = new MarketPriceGateway("TEST", new MarketPriceAction[0]);
    }

    @Given("I have a market price gateway service with an invalid environment")
    public void i_have_a_market_price_gateway_service_with_an_invalid_environment() {
        mpg = new MarketPriceGateway("THIS IS NOT A VALID ENVIRONMENT STRING", new MarketPriceAction[] {this});
    }

    @Given("I have an initialised and started market price gateway")
    public void i_have_initialised_market_price_gateway_actions() throws IOException {
        mpg = new MarketPriceGateway("TEST", new MarketPriceAction[]{this});
        mpg.setMarketPriceActions(new MarketPriceAction[]{this});
        mpg.start();
    }

    @Then("an exception should have been generated")
    public void an_exception_should_have_been_generated() {
        Assertions.assertNotNull(e);
    }

    @Given("I have these input messages")
    public void i_have_these_input_messages(DataTable dataTable) {
        List<String> rows = dataTable.rows(1).asList();
        receivedRatesLatch = new CountDownLatch(rows.size());
        for(String msg:rows) {
            mpg.subscriber.onMessage(msg);
        }

        try {
            receivedRatesLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onPricePublish(PublishReason reason, Price price) {
        System.out.println(">" + reason + " " + price);
        Map<String, Price> pairMap = outputRates.computeIfAbsent(reason, r -> new HashMap<>());
        pairMap.put(price.getSymbol().getCodeString(),price);
        receivedRatesLatch.countDown();
    }

    @When("I request a rate for {string}")
    public void i_request_a_rate_for(String symbol) {
        try {
            mpg.waitForStart();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        receivedRatesLatch = new CountDownLatch(1);
        mpg.consumer.onRateRequest(symbol);
        try {
            requestedPrice = outputRates.get(PublishReason.Request).get(symbol);
            receivedRatesLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Then("the bid result should be {long} and the ask result should be {long}, both with a scale of {int}")
    public void the_bid_result_should_be_and_the_ask_result_should_be_both_with_a_scale_of(long expectedBid, Long expectedAsk, Integer expectedScale) {
        Assertions.assertEquals(requestedPrice.getBid().getValue(),expectedBid);
        Assertions.assertEquals(requestedPrice.getBid().getScale(),expectedScale);
        Assertions.assertEquals(requestedPrice.getAsk().getValue(),expectedAsk);
        Assertions.assertEquals(requestedPrice.getAsk().getScale(),expectedScale);
    }

    @Given("I have a market price gateway service")
    public void iHaveAMarketPriceGatewayService() {

    }
}
