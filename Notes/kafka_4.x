@@ listeners vs. advertised.listeners:

	- listeners: 

		"listeners=PLAINTEXT://localhost:9092(1),CONTROLLER://localhost:19092(2)"
		
		* This defines which network endpoints the Kafka broker listens on, for both:

		* Client/broker communication (e.g., producers, consumers, other brokers) (1)

		* Controller communication. (2)

		Is what the broker will use to create server sockets.
	
		(server socket: a type of socket that a server uses to listen for incoming connection requests from clients.)

	- advertised.listeners:

		* This tells Kafka what addresses it should advertise to clients and other brokers.

		(is what clients will use to connect to the brokers.)


@@ controller.quorum.bootstrap.servers vs controller.quorum.voters:

	- controller.quorum.bootstrap.servers: 
		This property is used to configure the initial set of endpoints that can be used to discover the leader of the KRaft cluster metadata partition. It is particularly useful when bootstrapping a new KRaft cluster or when a controller needs to discover the current leader. This property is essential for new controllers to join the cluster and for brokers to find the leader. For example, when starting a new controller, it can use the endpoints specified in controller.quorum.bootstrap.servers to discover the leader and join the quorum. 

	- controller.quorum.voters: 

		This property specifies the set of controllers that are part of the metadata quorum. These controllers are the ones that participate in the leader election and are responsible for maintaining the metadata log. Each node in the quorum must have a unique node ID, and the node IDs specified in controller.quorum.voters must match the corresponding IDs on the controller servers. For example, if you have three controllers with node IDs 1, 2, and 3, you would list them in controller.quorum.voters as 1@host1:port,2@host2:port,3@host3:port. This property is crucial for ensuring that the quorum can function correctly and that a majority of the controllers can agree on the leader. 

		(In summary, controller.quorum.bootstrap.servers is used for discovery and initial connection, while controller.quorum.voters defines the set of controllers that participate in the quorum and leader election.) 