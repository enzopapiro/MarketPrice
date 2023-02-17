package org.enzopapiro.marketprice.util.parsing.string;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.enzopapiro.marketprice.util.parsing.text.string.String8LongEncoder;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class String8LongEncoderStepDefinitions {

    private String symbol;
    private long actualLongEncoding;
    private String inflatedSymbol;

    @Given("I have the following symbol strings")
    public void i_have_the_following_symbol_strings(io.cucumber.datatable.DataTable dataTable) {
        this.symbol = dataTable.asMaps().get(0).get("symbol");
    }

    @When("I ask for the long encoding")
    public void i_ask_for_the_long_encoding() {
        this.actualLongEncoding = String8LongEncoder.stringToLong(symbol);
    }

    @Then("the resulting long value should be {long}")
    public void the_resulting_long_value_should_be(long expectedLongEncoding) {
        assertEquals(expectedLongEncoding, this.actualLongEncoding);
    }

    @When("I ask for the long to be converted back to a string")
    public void i_ask_for_the_long_to_be_converted_back_to_a_string(){
        inflatedSymbol=String8LongEncoder.longToString(this.actualLongEncoding);
    }

    @Then("the resulting string should be {string}")
    public void the_resulting_string_should_be(String expectedString){
        Assertions.assertEquals(expectedString,inflatedSymbol);
    }
}
