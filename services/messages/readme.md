# Messages on Queues

This version of the shop uses an extra service that coordinates the process. The architecture is based on 
Greg Young's course (see https://skillsmatter.com/courses/345-greg-youngs-cqrs-domain-events-event-sourcing-and-how-to-apply-ddd?gclid=Cj0KEQiAwNmzBRCaw9uR3dGt950BEiQAnbK9628J7Qg2wcBqZxWc5HEpEZr19BDmR9EJqxD4EdT0cuMaAtW58P8HAQ#).

The solution has two important properties:
- All services share a document that represents the order. This differs from our previous solution where each service had a separate view of reality. 
- The process is a concept that deserves a service of its own. In the previous solution the process was implicit in the messages
published on topics. The CQRS and DDD course argues that the process is a first class citizen and should be treated as such.

![](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/master/src/exercise-queues/domain-meetup-ex3.jpeg)

