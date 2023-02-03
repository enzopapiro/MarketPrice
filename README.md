MARKET PRICE EXERCISE

1. The project is built using maven and developed with JDK 11.
2. run the simulation app using:  mvn exec:java -Dexec.mainClass=org.enzopapiro.marketprice.service.MarketPriceGateway
3. run the tests using: mvn test
4. Assumptions are documented across the code, however to summarise;
   a. I'm expecting the csv transport to call my subscribe on the same thread each time.
   b. There's ring buffer used to send the messages to a single consumer thread.
   c. The REST API should call slot into the MarketPriceManager::publish call, possibly another ring buffer mechanism to handle the lengthy IO onto that transport.
   d. The ad-hoc request for rate is handled by the MarketPriceManager::request method which is assumed to be called by another thread that receives the requests from the transport.