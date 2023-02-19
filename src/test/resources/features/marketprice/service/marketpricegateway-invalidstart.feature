Feature: MarketPriceGateway

  Scenario: Starting a service without specifying market price actions
    Given I have a market price gateway service without actions
    When I start it
    Then an exception should have been generated
