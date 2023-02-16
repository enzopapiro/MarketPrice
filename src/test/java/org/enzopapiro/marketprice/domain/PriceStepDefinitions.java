package org.enzopapiro.marketprice.domain;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class PriceStepDefinitions {
    private Price price;

    @Given("I have the prices")
    public void i_have_the_prices(Price price){//io.cucumber.datatable.DataTable dataTable) {
        this.price = price;
    }

    @When("I ask for a formatted bid string")
    public void i_ask_for_a_formatted_bid_string() {
        // No-op, the bid value is already parsed in the given step
    }

    @Then("the resulting bid string should be {string}")
    public void the_resulting_bid_string_should_be(String expectedBid) {
        Assertions.assertEquals(expectedBid,price.getBidAsString());
    }

    @When("I ask for a formatted ask string")
    public void i_ask_for_a_formatted_ask_string() {
        // No-op, the ask value is already parsed in the given step
    }

    @Then("the resulting ask string should be {string}")
    public void the_resulting_ask_string_should_be(String expectedAsk) {
        Assertions.assertEquals(expectedAsk,price.getAskAsString());
    }
}
