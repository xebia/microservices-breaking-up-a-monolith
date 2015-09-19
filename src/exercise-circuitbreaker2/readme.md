# Exercise 1 - Circuit Breaker
There are basically four reasons for Hystrix to call the fallback method: an exception, a timeout, too many parallel requests, or too many exceptions in the previous calls. 

In this excercise we will do some short exercises to see how Hystrix works. We will also improve the robustness of the Shop by adding a circuit breaker to one of the calls to an external service.
 
1. TimeoutTest. Uses Hystrix Examples to show how Hystrix responds on timeouts and exceptions in calls to remote services. 
    Exercise: Run @Test and see what shows up in the log file. Then look at the Hystrix Demo code in GitHub.
2. CacheTest. Based on test in Hystrix Examples. Exercises the use of a request cache in Hystrix.
    Exercise: Run @Test and ....
3. CollapserTest. Based on test in Hystrix Examples. Exercises the batching functionality in Hystrix.
    Exercise: Run @Test and ...
4. TooManyRequestsTest. .... 

