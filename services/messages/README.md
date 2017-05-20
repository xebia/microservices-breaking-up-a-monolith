# Refactoring a monolith to microservices

This project documents my microservices journey. It started end 2015 when we started investigating microservices
at [Xebia] ( http://www.xebia.com ). Since then I worked on several versions of the software, trying out new 
microservices related concepts. 

### History

For more history about this project see these blogs:

[refactoring-a-monolith-to-microservices] (http://blog.xebia.com/refactoring-a-monolith-to-microservices/)

[introducing-a-process-manager] (http://blog.xebia.com/refactoring-to-microservices-introducing-a-process-manager/)

[using-document-state] (http://blog.xebia.com/refactoring-microservices-using-document-state/)

## Build and run

You'll need Java 8, a recent version of Docker and Maven 3. I'm working on OSX and haven't tried to run the software
on other platforms. 

To start:

```
./build.sh
docker-compose up
```

See if it works:

```
cd ./scenarioTest
mvn clean test
```

## In this installment: Wire up containers using docker-compose

In the previous version of the shop landscape (see tag 'document_v2' in this repository) services were
started with a shell script. Each depended on Rabbit MQ to run, so there was a url with an IP address that
depended on whatever address the host it runs on got from its DHCP server. This was brittle, so I
decided to introduce docker-compose. Actually I should say 're-introduce' because my colleague 
Pavel Goultiaev built a previous version using compose. In this version I copied and finished his code.  

