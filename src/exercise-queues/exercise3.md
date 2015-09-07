# Exercise 3 - Messages on Queues
In this exercise we will use queues to connect services. This means services are decoupled in time, making the overall architecture more robust.

The goal of this exercise is to find out what the consequences of this decision are.

Sources are in https://github.com/xebia/microservices-breaking-up-a-monolith/
In the src/exercise-queues subdirectory. 

1. The picture in [domain-meetup-ex3.jpeg](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg) shows the messages (Events, the orange ellipses) passed between services. Open your IDE, import the root pom.xml in src/exercise-queues and try to locate the code that handles and produces the messages shown in the picture. You should find `ItemsOrdered` and `OrderPaid` events. Note that while the picture shows events, the software is defined in more technical terms so you may not find the names of the events as they are shown in the picture. Would you prefer event names to be mapped to queue names?
2. The queues used by RabbitMQ are defined in a file named RabbitMQSetup.sh. Open this file and locate the queues in domain-meetup-ex2.gv. The next exercise depends on RabbitMQ running, so open a shell and run RabbitMQSetup.sh.
3. Start the services, in your IDE is probably the most convenient way. Now open the `scenarioTest` (in IntelliJ you'll have to import this project separately) and run the test. This fails because of a missing implementation. Find the missing code in the message handler and implement it correctly.

The exercise above shows how to use queues to transport events between services. 
If you feel like it, implement the missing Events shown in domain-meetup-ex2.gv: OrderShipped and ProductAdded.

Another area to explore is the code in payment. Look at the classes in `com.xebia.payment.domain`. You'll find that these classes were copied from shop, which means payment knows too much about what an order looks like in shop. This isn't necessary and by using a feature from Spring's JSON parsing. ShipmentController in fulfillment shows how to ignore JSON data you don't need. This allows us to define an Order class in payment that contains only the attributes we do need.

TODO: check this...
Finally, the test in scenarioTest includes a call to Thread.sleep(). This is necessary because we have to make sure the itemsOrdered event is processed by payment before we send the pay command. Implement logic in payment that allows out of order processing.

![](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg)
