#Vertx + Hazelcast + Scala + Kubernetes
Purpose of this project is to show how we can get automatic scaling inside a Kubernetes cluster using
the Hazelcast-clustermanager.

I stumbled over the [Hazelcast-Kubernetes-Discovery](https://github.com/hazelcast/hazelcast-kubernetes) which
 allows Hazelcast to discover other instances in the same cluster (for this example I use Kubernetes-labels).

#Work with this project

Create a runnable fat-jar
```
sbt assembly
```

play around in sbt
```
sbt
> console
scala> vertx.deployVerticle(s"scala:${classOf[HttpVerticle].getName}")
scala> vertx.deploymentIDs
```

From here you can freely interact with the Vertx-API inside the sbt-scala-shell.

#Dockerize
The project also contains everything you need to create a Kubernetes-Docker-container (really, the
created container ONLY works in a Kubernetes-environment).
Simply run the following command to package your fat-jar inside a Docker-container
```
sbt docker
```
To run use
```
docker run -p 8666:8666 default/vertx-scala-sbt
```
Point your browser to [http://127.0.0.1:8666/hello](http://127.0.0.1:8666/hello) and enjoy :)

#Kubernetes
The following steps illustrate what has to be done to get a local Kubernetes-cluster and deploy
this application in there.
##Get minikube
[Minikube](https://github.com/kubernetes/minikube) is a great little tool for creating a local Kubernetes cluster.

On mac do:
```brew install minikube```

##Setup
First we need to start a clean Minikube instance with the following command:
```minikube start```

To open the dashboard you can now use:
```minikube dashboard```

Now we need to prepare for building the Docker-container. To avoid complications we will build the image 
inside of Minikube. To allow _sbt_ to perform this action we first have to prepare the shell: 
```eval $(minikube docker-env)```

In the project root we can now start the build: 
```sbt clean docker``` 

The Docker image is now ready for getting deployed. Execute the following commannd to
create a Hazelcast-service (not needed in this tutorial but a starting point for further experiments).
```kubectl create -f kubernetes/hazelcast-service.yaml```

And now we deploy the actual application, expose it and check where we can reach the endpoints:
```
kubectl create -f kubernetes/deployment.yaml
kubectl expose deployment vertx --type="NodePort"
minikube service vertx --url
```

The last command gives a list of two ip/port-combos. The first one is Hazelcast, the second one the
demo application.

Check the logs in the Dashboard.

And now scale the cluster and see how everything is automatically taken care of:
```kubectl scale deployment vertx --replicas 2````

#Things to remember
- If anything goes wrong, check the assembly if _META-INF/services/com.hazelcast.spi.discovery.DiscoveryStrategyFactory_
contains the right factory (HazelcastKubernetesDiscoveryStrategyFactory in our case).
