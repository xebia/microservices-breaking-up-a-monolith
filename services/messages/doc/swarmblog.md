used local images till now.
doesn't work with swarm, need a registry, might use local, but dockerhub works fine

create account on Docker hub

in each buildDockerImages.sh script:
   
    (cd target &&
      docker build -t $(service_name) . &&
      docker tag $(service_name) $(docker_hub_username)/shop-$(service_name):v1
      docker push $(docker_hub_username)/shop-$(service_name):v1
    )

update docker-compose.yml with new container tags
xebia/fulfillment -> $(docker_hub_username)/shop-$(service_name)

also added visualizer (dockersamples/visualizer:stable) just for fun
and made sure rabbitsetup runs only once:

    rabbitsetup:
        image: jvermeir/shop-rabbitmq_msg_setup:v1
        restart: on-failure            

build:

	./tools/build_docker_hub.sh jvermeir
	
somewhere on your filesystem in a terminal:

    docker swarm init
    
    docker-machine create --driver virtualbox myvm1
    docker-machine create --driver virtualbox myvm2

machine names are not relevant, this starts 2 linux servers in Virtualbox

make one server the swarm manager

    docker-machine ssh myvm1 "docker swarm init --advertise-addr 192.168.99.100"

copy the token from the log, then

    docker-machine ssh myvm2 "docker swarm join --token <enter-token-here> 192.168.99.100:2377"

connect to swarm manager

    eval $(docker-machine env myvm1)
    
deploy the stack
    
    docker stack deploy -c docker-compose.yml shop

run the scenarioTest, don't forget to enter the ip address of the swarm manager in ScenarionTest.java/public static final String PROTOCOL_AND_HOST = "http://192.168.99.101";

    docker stack ps shop
    
shows the status of all services, or http://192.168.99.101:8080/ in your browser.

Remove all images from local

	docker rmi --force `docker images -q`
	
	
next up: 

- multiple instances -> need a database server
- Rabbit failover


