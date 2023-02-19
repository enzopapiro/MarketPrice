Feature: MarketPriceGateway

  Background:
      Given I have a market price gateway service
      And I have an initialised and started market price gateway

  Scenario Outline: Input messages
    Given I have these input messages
      | message |
      | 101, EUR/USD, 1.1000,1.2000,17-03-2023 12:01:01:00\n102, EUR/JPY, 119.60,119.90,17-03-2023 12:01:02:00\n103, GBP/USD, 1.2500,1.2560,17-03-2023 12:01:03:00 |
      | 104, USD/JPY, 134.172,134.175,17-03-2023 12:01:04:00 |

    When I request a rate for <symbol>
    Then the bid result should be <bid> and the ask result should be <ask>, both with a scale of <scale>

    Examples:
      | symbol | bid | ask | scale |
      | "EURUSD" | 10989 | 12012 | 4 |
      | "EURJPY" | 11948 | 12002 | 2 |
      | "GBPUSD" | 12488 | 12573 | 4 |
      | "USDJPY" | 134038 | 134309 | 3 |
      | "GBPUSD" | 12488 | 12573 | 4 |

