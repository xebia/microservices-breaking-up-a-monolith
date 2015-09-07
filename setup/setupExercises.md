# Setup instructions

### To run the exercises, you will need the following:
- Java 1.8
- Maven 3
- RabbitMQ (including rabbitmqadmin by `rabbitmq-plugins enable rabbitmq_management`)
- Git command line or GUI client
- A Java IDE like IntelliJ might help
- A virtual machine running Linux is a good idea, but not strictly necessary. We'll assume you're using Linux or OSX. 

### To install proceed as follows:
- Install tools above using their default installers
- Clone the repo at https://github.com/xebia/microservices-breaking-up-a-monolith
- Open a shell, cd into the src directory for the exercise and run: `mvn test`

### To run all services:
- go to one of the exercise-* directories in src
- ./runAllServices.sh
- but it is probably easier to run the Application for each services in the src/main/java folder from your IDE
