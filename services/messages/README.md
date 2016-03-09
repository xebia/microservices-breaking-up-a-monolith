# Parse document using basic JSON

The previous version of the shop required the full JSON document to be sent around. To parse the document we
used Springs JSON integration. Because of that decision each service now needs all of the domain classes. The goal of
this version is to introduce light weight JSON parsing and remove the dependency on Spring JSON. Each service will
parse the Clerk document only in as far as necessary to retrieve the data it needs. 

# History


# Process coordinator

This version of the shop will use an extra service that coordinates the process. The architecture is based on 
Greg Young's course at Skills Matter, see [CQRS/DDD course](https://skillsmatter.com/courses/345-greg-youngs-cqrs-domain-events-event-sourcing-and-how-to-apply-ddd?gclid=Cj0KEQiAwNmzBRCaw9uR3dGt950BEiQAnbK9628J7Qg2wcBqZxWc5HEpEZr19BDmR9EJqxD4EdT0cuMaAtW58P8HAQ#).

The solution has two important properties:
- All services share a document that represents the order. This differs from our previous solution where each service had a separate view of reality. 
- The process is a concept that deserves a service of its own. In the previous solution the process was implicit in the messages
published on topics. The CQRS and DDD course argues that the process is a first class citizen and should be treated as such.

What will happen is that the ShopManager implements the process. In earlier solutions the process was hidden in the sense
that whenever a service thought it couldn't proceed, it would send out a message. E.g. the shop would say it had a
completed Order. This Order would then be picked up by Payment and Fulfillment. Payment would allow a customer to pay and
Fulfillment would have to wait because it needed paid Orders. So when Payment was done it would send out a PaymentReceived
message that would allow Fulfillment to continue. 
This works but Greg argues that this allows only a single process and that the solution would be more flexible if we would
allow for a process manager that delegates steps in the process to different services, waiting for them to complete.
That touches upon an aspect that wasn't implemented in our earlier solution: what happens if payment takes to long? In 
our first solution this would mean we would have a database with completed but unpaid orders. That problem could be solved
by running a cleaning process that would send a message to customer support prompting them to call the customer, or just
to get rid of the order. This is where our earlier solution starts to feel a bit constrained; what if we needed
several services to find out what to do? Implementing process logic in a separate service seems to make sense, so 
this branch tries and do just that to find out what the consequences will be. 

Now that we've introduced a shopManager service with a Clerk to handle the shopping process, we can add process control
by implementing the ShopManager class. The idea is that a ShopManager will dispatch a Clerk when a customer enters the
shop. The Clerk will be sent on its way to add products to the shopping cart, accept payment and ship orders. 
Meanwhile the ShopManager will monitor the Clerks process. If it takes to long. e.g. because a customer decides not to
finalize an order, the process will be stopped by the ShopManager.

![](https://raw.githubusercontent.com/xebia/microservices-breaking-up-a-monolith/blob/RefactorToOrchestrator/services/messages/processDiagram.jpeg)
