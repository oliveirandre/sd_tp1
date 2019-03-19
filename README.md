# sd_tp1
First project of Distributed Systems - Repair shop activities


Events portray the activities that go by at an automobile repair shop somewhere in Aveiro.
There are five main locations: the park where the customers park their cars and retrieve them after the
repair has been concluded and where some replacement cars are parked for eventual customer use, the
lounge where the manager stays and process customers complains, the repair area where the mechanics
carry out the operations to fix the cars, the supplier site where the manager goes to obtain parts that are
not presently available at the shop, and the outside world, where customers stay performing their day-today activities.
There are three kinds of intervening entities: the manager who runs the repair shop, the mechanics
who repair the cars and the customers who own the cars.
N customers visit the repair shop during the day. M mechanics are at work to fix the cars. T replacement vehicles for customer use are present. A repair always requires the replacement of one part out of K
that in principle are available at the repair area.
The activities are organized as described below
― the customer arrives at the repair shop sometime during the day and parks her/his car;
― the customer goes to the lounge and queues in to talk with the manager;
― the manager deals with customers one at the time: collects the car key, posts the job to the repair
area and asks if a replacement vehicle is required; if so, she assigns one to the customer;
― the customer waits for the key of the replacement vehicle, if that was the case, and leaves the
repair shop;
― the mechanics, upon receiving a repair order, go to the park to collect the vehicle, check which part
is necessary to be replaced and get one, if it is available; if not, they contact the manager to
replenish the stock and proceed to the next order, while waiting for the part to arrive;
― when the repair is concluded, they drive the vehicle back to the park and alert the manager of the
fact;
― the manager, upon receiving a notice of an empty stock, goes as soon as possible to the suppliers
site to replenish it;
― the manager, upon receiving a notice that a service has been concluded, contacts the customer to
come and collect her/his car,
― the customer arrives at the repair shop and, if she/he is driving a replacement vehicle, parks it;
then, goes to the lounge, queues in to pay for the service and collect her/his car key and leaves the
repair shop;
― when all the cars have been repaired, the manager sends the mechanics home and calls the day off.
Assume there are thirty customers, two mechanics, three replacement vehicles and three different
types of parts. Write a simulation of the life cycle of the customers, the manager and the mechanics using
one of the models for thread communication and synchronization which have been studied: monitors or
semaphores and shared memory.
One aims for a distributed solution with multiple information sharing regions, written in Java, run in
Linux and which terminates. A logging file, that describes the evolution of the internal state of the
problem in a clear and precise way, must be included.
