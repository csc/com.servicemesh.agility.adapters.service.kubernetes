# com.servicemesh.agility.adapters.service.kubernetes

Kubernetes (k8s) Service Adapter

The Kubernetes adapter leverages the Agility Platform Services SDK (https://github.com/csc/csc-agility-platform-services-sdk-reference-info) to enable orchestration and deployment of container based applications/services into kubernetes clusters. The adapter exposes a set of agility service components corresponding to kubernetes concepts such as a docker container, pod, and load-balancer that that can be integrated into an application blueprint along with other application workloads or services.

# Usage

The Kubernetes adapter exposes the following services:

* Docker Container - models attributes/settings required to instantiate a single container instance
* Kubernetes Pod - represents a collection of one or more containers that are deployed together on the same host / network namespace. Membership is represented by a dependency/connection between the pod and associated containers.
* Kubernetes Service - an extension of the agility LBaaS type that provides load balancing across one or more pods. A dependency from the k8s service to the pod defines the association.


## License
The Kubernetes adapter is distributed under the Apache 2.0 license. See the [LICENSE](https://github.com/csc/com.servicemesh.agility.adapters.core.aws/blob/master/LICENSE) file for full details.
