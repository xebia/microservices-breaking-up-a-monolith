# Refactoring a monolith to microservices

This project documents my microservices journey. It started end 2015 when we started investigating microservices
at [Xebia](http://www.xebia.com). Since then I worked on several versions of the software, trying out new 
microservices related concepts. 

## History

For more history about this project see these blogs:

[refactoring-a-monolith-to-microservices](http://blog.xebia.com/refactoring-a-monolith-to-microservices/)

[introducing-a-process-manager](http://blog.xebia.com/refactoring-to-microservices-introducing-a-process-manager/)

[using-document-state](http://blog.xebia.com/refactoring-microservices-using-document-state/)

[using-docker-compose](https://www.linkedin.com/pulse/refactoring-microservices-using-docker-compose-jan-vermeir/)

## In this installment: Using Swarm, part 1

This version introduces Swarm. If you follow the install manual in my blog (TODO) you end up with
two virtual machines running 6 containers. I've also changed some of the build scripting to make it 
easier to build and run the swarm. 

## Build and run

You'll need Java 8, a recent version of Docker and Maven 3. I'm working on OSX and haven't tried to run the software
on other platforms. 

To start: see blog (TODO)

See if it works:

```
cd ./scenarioTest
mvn clean install -DprotocolAndHost=http://192.168.99.101
```

Replace the ip address with the address of your swarm.
