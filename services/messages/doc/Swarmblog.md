In my [previous blog](https://www.linkedin.com/pulse/refactoring-microservices-using-docker-compose-jan-vermeir/?lipi=urn%3Ali%3Apage%3Ad_flagship3_profile_view_base_post_details%3B07mN6wMxTcq78ra8ZivEnQ%3D%3D), I used local images wired together with a docker-compose.yml file. This was an improvement over stand alone containers. Networking is now more robust because code in images uses names instead of IP addresses to access services. This time my goal is to introduces Swarm so I can distribute components over multiple hosts and run more instances if necessary. Below I'll describe step one: migrate the docker-compose-single-host setup to a Docker Swarm multi-host version. The code for this blog can be [found here](git@github.com:xebia/microservices-breaking-up-a-monolith.git), check out the swarm tag and go to the services/messages folder. 

To get started, I followed the tutorial on [docker.com](https://docs.docker.com/get-started/). It's worthwhile browsing through the first five parts to get an idea about Swarm. 

First we need to initialize Docker Swarm and define servers to run containers on. Enter somewhere on your filesystem in a terminal:

    docker swarm init
    
    docker-machine create --driver virtualbox myvm1
    docker-machine create --driver virtualbox myvm2

Machine names are not important. The lines above start two linux servers named myvm1 and myvm2 in Virtualbox. 
Make one server the Swarm manager:

    docker-machine ssh myvm1 "docker swarm init --advertise-addr 192.168.99.100"

The output in the terminal shows a token that's needed by members of the swarm to connect to the Swarm manager. Copy the token from the log, then

    docker-machine ssh myvm2 "docker swarm join --token <enter-token-here> 192.168.99.100:2377"

All commands to configure the swarm have to be executed against the Swarm manager. To save typing, set myvm1 as the Swarm manager:

    eval $(docker-machine env myvm1)

Now we need several changes to make the software ready to run on a swarm:

- Publish containers on Docker hub.
- Modify docker-compose.yml

With Swarm we need a Docker registry for images (the containers will run on separate hosts), so one of the changes I've made is to push to Docker hub. You can create a free account and push images, but they will be public. To streamline my build process, I've introduced a build script in tools/buildDockerImage.sh. This script can be called from build_docker_hub.sh or build_local.sh. The first script will publish images to Docker hub and the latter will work like in previous versions of my code. In that case you won't be able to use Swarm, but it will be faster for development. 

So, go to Docker hub and create an account. Then enter 

	./tools/build_docker_hub.sh <your username on Docker hub here>
	
This will create six images on Docker hub named <account>/shop-<serviceName>, e.g. jvermeir/shop-payment.

The second change is in docker-compose.yml. The main differences are the network definition, the new images used and a restart policy for the container that sets up RabbitMQ:

	version: "3"
	services:
	  rabbit:
	    image: rabbitmq:3-management
	    ports:
	     - "15672:15672"
	     - "4369:4369"
	     - "5672:5672"
	     - "25672:25672"
	    networks:
	     - webnet
	  rabbitsetup:
	    image: jvermeir/shop-rabbitmq_msg_setup:v1
	    restart: on-failure
	    networks:
	     - webnet
	  shopmanager:
	    image: jvermeir/shop-shopmanager:v1
	    ports:
	     - "9005:9005"
	    networks:
	     - webnet
	# other service ommitted
	
	networks:
	  webnet:

Each service is now part of the network named 'webnet' and images are taken from Docker hub, so they are named like jvermeir/shop-shopmanager:v1. Finally, rabbitsetup has a restart policy: 'restart: on-failure'. This was necessary because it is a one-time process that shouldn't be restarted if it completes. Without the Swarm this container would indeed run only once, but in the context of Swarm it was restarted after completion. Hence the restart policy. 

Start the swarm:

	docker stack deploy -c docker-compose.yml shop
	
I've also added a visualizer from dockersamples to docker-compose.yml. The visualizer can be accessed in a browser:

	http://192.168.99.100:8080/

and shows a nice picture of each host in the swarm and the containers it runs. Clicking the name of the image will open a popup with detailed data about the container. 
To quickly see which containers are running: 

    docker stack ps shop
    
shows the status of all services.

Finally, I've updated the integration test in ./scenarioTest. It can be run against the swarm

	mvn clean install -DprotocolAndHost=http://192.168.99.101
	
where the parameter is the url to one of the servers in the swarm.

Next I plan to create multiple instances of services, but to make that work I need a database container. Also RabbitMQ will probably need some work.

So far the Swarm experience was fairly painless: the changes I had to make were minimal (mostly cleaning up old junk left over from years back) and the manuals on docker.io worked well. 
