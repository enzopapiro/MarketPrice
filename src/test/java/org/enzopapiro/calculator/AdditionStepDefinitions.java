package org.enzopapiro.calculator;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdditionStepDefinitions {

    List<Integer> numbers;
    int result = 0;
    @Before
    public void setUp(){
        numbers = new ArrayList<>();
    }
    @Given("I have entered {int} into the calculator")
    public void i_have_entered_into_the_calculator(Integer number){
        numbers.add(number);
    }

    @When("I press add")
    public void i_press_add() {
        for(int n:numbers){
            result+=n;
        }
    }

    @Then("the result should be {int} on the screen")
    public void the_result_should_be_on_the_screen(Integer expected){
        assertEquals(expected,result);
    }
}
