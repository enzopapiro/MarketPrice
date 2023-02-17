Feature: String8LongEncoder

Scenario Outline: Encode a <symbol> string to an <long>

Given I have the following symbol strings
    | symbol |
    | <symbol> |

When I ask for the long encoding
Then the resulting long value should be <longEncoding>

When I ask for the long to be converted back to a string
Then the resulting string should be <inflatedSymbol>

Examples:
    | symbol | longEncoding | inflatedSymbol |
    | EURUSD | 4995989888060096512 | "EURUSD" |
    | USDJPY | 6148333002257137664 | "USDJPY" |
    | GBPUSD | 5134754852555194368 | "GBPUSD" |