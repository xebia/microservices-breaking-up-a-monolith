# Exercise 3 - Messages on Queues
In this exercise we will use queues to connect services. This means services are decoupled in time, making the overall architecture more robust.

The goal of this exercise is to find out what the consequences of this decision are.

Sources are in https://github.com/xebia/microservices-breaking-up-a-monolith/
in the src/exercise-queues subdirectory.
While browsing the code it may help to search for '// Exercise' markers. You can work your way through the process by following the markers in the correct order, starting at the one marked '// Exercise 1'.

1. The picture in [domain-meetup-ex3.jpeg](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg) shows the messages (Events, the orange ellipses) passed between services. Open your IDE, import the root pom.xml in src/exercise-queues and try to locate the code that handles and produces the messages shown in the picture. You should find `ItemsOrdered` and `OrderPaid` events. Note that while the picture shows events, the software is defined in more technical terms so you may not find the names of the events as they are shown in the picture. Would you prefer event names to be mapped to queue names?
2. The queues used by RabbitMQ are defined in a file named RabbitMQSetup.sh. Open this file and locate the queues that correspond to the events in the picture. 
   The next exercise depends on RabbitMQ running, so open a shell and run RabbitMQSetup.sh.
3. Start the services, in your IDE is probably the most convenient way. 
    - Catalog service. Run: `*/catalog/src/main/java/com/xebia/catalog/CatalogApplication.java*`
    - Shop service. Run: `*/shop/src/main/java/com/xebia/shop/ShopApplication.java*`
    - Payment service. Run: `*/payment/src/main/java/com/xebia/payment/PaymentApplication.java*`
    - Fulfillment service. Run: `*/fulfillment/src/main/java/com/xebia/fulfillment/FulfillmentApplication.java*` 
Now open the `scenarioTest` (in IntelliJ you'll have to import this project separately) and run the test. This fails (404 Not Found) because of a missing implementation. Find the missing code in the message handler and implement it correctly.

The exercise above shows how to use queues to transport events between services. 
If you feel like it, implement the missing Events shown in domain-meetup-ex3.jpeg: OrderShipped and ProductAdded.

Another area to explore is the code in payment. Look at the classes in `com.xebia.payment.domain`. You'll find that these classes were copied from shop, which means payment knows too much about what an order looks like in shop. This isn't necessary and by using a feature from Spring's JSON parsing. ShipmentController in fulfillment shows how to ignore JSON data you don't need. This allows us to define an Order class in payment that contains only the attributes we do need.

You can use Swagger UI to explore the APIs of the services. Please open http://localhost:PORT/docs/index.html.

![](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg)

*The Long Story*

Exploring the code is best done by following the trail of '// Exercise' lines starting with '// Exercise 1'. Below is a summary.

*// Exercise 1*

This is where the REST version used to send information about the Order to Fulfillment. When a new Order is created, shop has to inform the world about this fact
so other services may process the order. In this case a new Order is picked up by both Payment and Fulfillment.

*// Exercise 2*

In Fulfillment you'll find a EventListener defining a method named processOrderMessage that will be called by Springs RabbitMQ integration when
 a message appears on the fulfillment.payment queue. There's a second method in this class you can ignore for now.

*// Exercise 3*

The second recipient of completed orders is Payment. The method in its EventListener class isn't implemented yet. 

*// Exercise 4 and // Exercise 4a*

Note that Payment needs hardly any data from the order event. 
  The current implementation parses all of the JSON message using classes copied from shop. This is not necessary because Jacksons JsonIgnoreProperties annotation allows you to  
  just ignore whatever is not needed. This simplifies the code in Payment and illustrates that domain models needn't be the same in all services. 
  To make this work in the EventListener you'll need to add an ObjectMapper and a tell it to ignore unknown properties:
    private ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  In controllers however you can just use the annotation (see ShipmentController for an example).

*// Exercise 5*

When PaymentController receives a payment (probably entered by the user in a third party payment service) it notifies the world
  of this fact. This used to be a synchronous REST call to shop but now it sends an event, OrderPaid. This event is
  picked up by both shop and fulfillment. Shop can use the fact to inform customers of the status of an order, whereas
  fulfillment can use the event to trigger actual shipping. 
  This code looks like the code you need to solve Exercise 3 so it would be a good idea to do some refactoring here. 

*// Exercise 6*

  Fulfillment receives a OrderPaid Event in its EventListener. It uses this fact to update the status of the Order it received 
  earlier from shop. If all is well it creates a Shipment that will cause shipping in the real world.

*// Exercise 7*

  Shop also receives a OrderPaid Event, updating the status of the order so it can inform the customer. This isn't strictly
  necessary since we could build a user interface that includes data from shop, fulfillment and payment that would have
   all information necessary to inform the customer. However, we don't really know how this is going to work out, so
    for now there's the event and we'll see what happens later.

*// Exercise 8*

Will Order register payment? This method won't be called anymore because Payment will send out an orderPaid event
that will be handled in events.EventListener. See discussion under Exercise 7.

*Extra:*

- Fulfillment may create a OrderShipped event that might be interesting to shop, though you could argue that shop doesn't need
this fact like we did under 'Exercise 7'.
- The diagram shows Catalog sending ProductAdded events. This function isn't implemented yet. Sending an event is probably not very
 efficient, this problem is better solved using a RSS or Atom feed. Catalog might notify other services of new data
  by publishing a message to improve efficiency. 
- What happens if an order is created and send to Payment, but it never gets paid? That would imply Shop would have junk in its database that
  needs to be acted upon. How should we implement this kind of clean up or batch processing? 
 
 
  

