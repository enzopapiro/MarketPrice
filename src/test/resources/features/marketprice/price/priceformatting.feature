Feature: Price

Scenario Outline: Create prices with correct id <id>, symbol <symbol>, bid <bid>, ask <ask> and scale <scale> with expected formatted values
  Given I have the prices
    | id | symbol | scale | bid | ask | ts |
    | <id>  | <symbol>  | <scale> | <bid> | <ask> | <ts> |

  When I ask for a formatted bid string
  Then the resulting bid string should be <formattedBid>

  When I ask for a formatted ask string
  Then the resulting ask string should be <formattedAsk>

  Examples:
    | id | symbol | scale | bid | ask | ts | formattedBid | formattedAsk |
    | 101 | EURUSD | 4   | 10705 | 10700 | 1676463167455 | "1.0705" | "1.0700" |
    | 102 | USDJPY | 4   | 1338300 | 1338430 | 1676463177455 | "133.8300" | "133.8430" |
    | 103 | GBPUSD | 4   | 12054 | 12053 | 1676572287455 | "1.2054" | "1.2053" |


