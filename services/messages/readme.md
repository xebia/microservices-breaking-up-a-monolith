# Messages on Queues

1. The picture in [domain-meetup-ex3.jpeg](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg) shows the messages (Events, the orange ellipses) passed between services. Open your IDE, import the root pom.xml in src/exercise-queues and try to locate the code that handles and produces the messages shown in the picture. You should find `ItemsOrdered` and `OrderPaid` events. Note that while the picture shows events, the software is defined in more technical terms so you may not find the names of the events as they are shown in the picture. Would you prefer event names to be mapped to queue names?
2. The queues used by RabbitMQ are defined in a file named RabbitMQSetup.sh. Open this file and locate the queues that correspond to the events in the picture. 
   The next exercise depends on RabbitMQ running, so open a shell and run RabbitMQSetup.sh.
3. Start the services, in your IDE is probably the most convenient way. 
    - Catalog service. Run: `*/catalog/src/main/java/com/xebia/catalog/CatalogApplication.java*`
    - Shop service. Run: `*/shop/src/main/java/com/xebia/shop/ShopApplication.java*`
    - Payment service. Run: `*/payment/src/main/java/com/xebia/payment/PaymentApplication.java*`
    - Fulfillment service. Run: `*/fulfillment/src/main/java/com/xebia/fulfillment/FulfillmentApplication.java*` 
Now open the `scenarioTest` (in IntelliJ you'll have to import this project separately) and run the test. This fails (404 Not Found) because of a missing implementation. Find the missing code in the message handler and implement it correctly.

You can use Swagger UI to explore the APIs of the services. Please open http://localhost:PORT/docs/index.html.

![](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg)

