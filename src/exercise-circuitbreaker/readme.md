# Exercise 1b - Circuit Breaker
In this excercise we will improve the robustness of the application by adding a circuit breaker to the application. 
Please refer to the overview diagram (domain-meetup-ex1.png) to see which calls are done between services. Please note that this is a first iteration of the Shop, where we use REST calls between services. 
![domain-meetup-ex1.png](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-circuitbreaker/domain-meetup-ex1.png)

The goal of the exercise is to keep the ScenarioTest running, even when the payment service becomes slow or stops working

Please look at `*/scenarioTest/src/test/java/com/xebia/shop/rest/ScenarioTest.java*`. This test class implements the customer journey.

1. Open your IDE and run the ScenarioTest. 

2. Inspect the log files. You should see the following error: `*org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://localhost:9002/cart/products/":Connection refused*`
   This means that the catalog service is not running. Actually none of the services are running. 

3. Start the other services:
    - Catalog service. Run: `*/catalog/src/main/java/com/xebia/catalog/CatalogApplication.java*`
    - Shop service. Run: `*/shop/src/main/java/com/xebia/shop/ShopApplication.java*`
    - Payment service. Run: `*/payment/src/main/java/com/xebia/payment/PaymentApplication.java*`
    - Fulfillment service. Run: `*/fulfillment/src/main/java/com/xebia/fulfillment/FulfillmentApplication.java*`  
   and rerun the ScenarioTest.

4. The log file should now show no failures anymore. The **SHOP LOG** should show 'PAYMENT ID for Card from Payment Service:'. This means that the shop was able to succesfully call the payment service.

5. We will now make the Payment service unresponsive by increasing the response time. the circuit should close if the payment service response time > 5000. 
     Default setting of Hystrix is 5000 ms. Please go to method 'startNewPaymentProcess' in `*/payment/src/main/java/com/xebia/payment/rest/PaymentController.java*` and increase the delay.
     Restart Payment service and Shop service  The log file of the Shop Service should now show  'PAYMENT ID for Invoice from Alternative:'. This signifies that the Fallback method of the StartPaymentCommand was called.
     It can be necessary to stop, compile and start the Payment and Shop services.
 
6. We will now encapsulate the call to the Fulfillment service with a Hystrix Command object. For an example please look at 
   `*/shop/src/main/java/com/xebia/shop/rest/StartPaymentCommand.java*`. The StartPaymentCommand is used in the 'pay' method of `*/shop/src/main/java/com/xebia/shop/rest/OrderController.java*`

   First stop the Fulfillment and Shop services. Next implement a new class that extends HystrixCommand. Move the REST call in OrderController.approveOrder to the new Command. 

For more information on what can be done with the Hystrix library, see [Hystrix-HowToUse](https://github.com/Netflix/Hystrix/wiki/How-To-Use).   

---------
Tip: when running from the command line, Spring Boot does not gracefully stop on a Ctrl-C. If this is the case then do:
```
ps -aef|grep java | grep mainClass=com.xebia
kill -9 <pid>
```
