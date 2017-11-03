# Docker Swarm log
## info
https://docs.docker.com/get-started/part4/#create-a-cluster

## tutorial, build test image

### Create the sample container

https://docs.docker.com/get-started/part2/

    docker build -t friendlyhello .
    docker tag friendlyhello jvermeir/get-started:part2
    docker push jvermeir/get-started:part2

### Swarm it

    docker swarm init
    docker service ps getstartedlab_web
    docker stack deploy -c docker-compose.yml getstartedlab

    docker stack rm getstartedlab
    docker swarm leave --force

## cluster

    docker swarm init
    docker-machine create --driver virtualbox myvm1
    docker-machine create --driver virtualbox myvm2

    docker-machine ssh myvm1 "docker swarm init --advertise-addr 192.168.99.100"
    docker-machine ssh myvm2 "docker swarm join --token SWMTKN-1-0ghrkfqva8zjnwhtfwcru82nzutqz4qc72i0v3vsjp23to1n7v-cusq07o7ll3jlnblow6clpipn 192.168.99.100:2377"
    docker-machine ssh myvm3 "docker swarm join --token SWMTKN-1-0ghrkfqva8zjnwhtfwcru82nzutqz4qc72i0v3vsjp23to1n7v-cusq07o7ll3jlnblow6clpipn 192.168.99.100:2377"

    eval $(docker-machine env myvm1)
    docker stack deploy -c docker-compose.yml getstartedlab
    docker stack ps getstartedlab
    curl http://192.168.99.101/
    curl http://192.168.99.100/

    docker service logs getstartedlab_web --follow
    
    docker stack rm getstartedlab

How do I rebalance if a new node joins the cluster? 
    docker stack deploy -c docker-compose.yml getstartedlab 
seems to work, but are containers restarted? 


cleanup:
 
    eval $(docker-machine env -u)

## Redis

    docker-machine ssh myvm1 "mkdir ./data"


