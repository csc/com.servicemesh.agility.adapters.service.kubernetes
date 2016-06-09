# com.servicemesh.agility.adapters.service.kubernetes

Kubernetes (k8s) Service Adapter

The Kubernetes adapter leverages the Agility Platform Services SDK (https://github.com/csc/csc-agility-platform-services-sdk-reference-info) to enable orchestration and deployment of container based applications/services into kubernetes clusters. The adapter exposes a set of agility service components corresponding to kubernetes concepts such as a docker container, pod, and load-balancer that that can be integrated into an application blueprint along with other application workloads or services.

# Usage

The Kubernetes adapter exposes the following services:

* Docker Container - models attributes/settings required to instantiate a single container instance
* Kubernetes Pod - represents a collection of one or more containers that are deployed together on the same host / network namespace. Membership is represented by a dependency/connection between the pod and associated containers.
* Kubernetes Service - an extension of the agility LBaaS type that provides load balancing across one or more pods. A dependency from the k8s service to the pod defines the association.

# Build

This adapter is dependent on the following:

* Agility Platform Services SDK: The services sdk is open source and can be found on github at https://github.com/csc/csc-agility-platform-sdk. See the included documentation for instructions on building the sdk and its dependencies. The adapter build assumes the services sdk is checked out and built in a relative path of ../agility-platform-sdk
* Java 8
* Ant (http://ant.apache.org/) version >= 1.9.3. Must be installed and the ant script located in your path.
* Ivy ((http://ant.apache.org/ivy/) Required for dependency management and should be installed/included in your ant installation.

After satisfying the above dependencies:

<code>ant clean deploy</code>

and packaged with:

<code>ant clean rpm-build</code>

## License
The Kubernetes adapter is distributed under the Apache 2.0 license. See the [LICENSE](https://github.com/csc/com.servicemesh.agility.adapters.service.kubernetes/blob/develop/LICENSE) file for full details.
