2.
## a. The (Remote Method Invocation) RMI Architecture
RMI is an API that provides  a mechanism  to create distributed application in java. it allows an object  to invoke  methods on an  object running in another JVM.
The RMI provides  remote communication between the applications using two objects that is the stub(present in the client side) and skeleton(present in the server side).

## RMI System layers
The RMI system consists of three layers in its architecture:
- stub/skeleton layer - On the client-side, a stub (proxy) acts as a local representative or proxy for the remote object. It forwards the remote method invocation requests to the skeleton on the server.
On the server-side, a skeleton receives the request from the stub, unmarshals the parameters, invokes the method on the actual remote object, and marshals the result back to the stub.

- Remote reference layer - This layer handles the interpretation of references made from the client to the remote service objects. It manages references to remote objects and supports the invocation of the methods on these objects.

- Transport layer - The transport layer handles the actual data transfer over the network. It provides the basic connectivity between the client and server, managing connections, and ensuring reliable message delivery.


## b. Describe at least three RMI object services used in distributed Applications 
1. Naming/Registry Service
The RMI registry is a simple naming service that allows clients to look up remote objects by name. It acts as a centralized directory where remote objects can be registered and located. In the server remote objects are bound to a name in the registry using the bind or rebind method. Clients use the lookup method to retrieve the remote object reference.

2. Activation Service
The activation service allows remote objects to be activated on demand. Instead of keeping remote objects in memory, they can be activated only when a client requests them, which helps save resources.

3. Distributed Garbage Collection (DGC) Service
Distributed Garbage Collection is a mechanism that automatically manages the memory of remote objects, ensuring that objects no longer in use are properly cleaned up.