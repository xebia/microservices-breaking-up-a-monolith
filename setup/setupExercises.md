# Setup instructions

### To run the exercises, you will need the following:
- Java 1.8
- Maven 3
- RabbitMQ (including rabbitmqadmin)
- Git command line or GUI client
- A Java IDE like IntelliJ might help
- A virtual machine running Linux is a good idea, but not strictly necessary. We'll assume you're using Linux or OSX. Consider using a VM and/or a Vagrant box.

### To install proceed as follows:
- Install tools above using their default installers
 - `rabbitmqadmin` can be activated by executing `rabbitmq-plugins enable rabbitmq_management`
- Clone the repo at https://github.com/xebia/microservices-breaking-up-a-monolith
- Open a shell, cd into the src directory for the exercise and run: `mvn test`

### To run all services:
- go to one of the exercise-* directories in src
- ./runAllServices.sh
- but it is probably easier to run the Application for each services in the src/main/java folder from your IDE

### Using provided Vagrantfile
- install Virtualbox : https://www.virtualbox.org/wiki/Downloads
- install Vagrant : https://www.vagrantup.com/downloads.html
- in the microservices-breaking-up-a-monolith folder do:
  - `vagrant up`  : will download and provision an Ubuntu VM
  - `vagrant reload` : will reboot the VM in order for installed updates to work correctly
  - `vagrant ssh` : will get you into VM itself (as user vagrant)

### Useful links
- https://www.rabbitmq.com/management.html
- https://www.vagrantup.com/
- https://www.virtualbox.org/wiki/Downloads
