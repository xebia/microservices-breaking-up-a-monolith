# Exercise 1 - Circuit Breaker
There are basically four reasons for Hystrix to call the fallback method: an exception, a timeout, too many parallel requests, or too many exceptions in the previous calls. 

In this excercise we will do some short exercises to see how Hystrix works. We will also improve the robustness of the Shop by adding a circuit breaker to one of the calls to an external service. 

