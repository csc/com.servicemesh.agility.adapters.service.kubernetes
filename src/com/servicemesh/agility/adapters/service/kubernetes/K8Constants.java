package com.servicemesh.agility.adapters.service.kubernetes;

public class K8Constants
{
    public static final String LABEL_PREFIX = "agility.csc.com/";

    public static final String CONTAINER_SERVICE_TYPE = "container-service";
    public static final String CONTAINER_SERVICE_NAME = "Docker Container";
    public static final String CONTAINER_SERVICE_DESCRIPTION = "Docker Container";

    public static final String CONTAINER_IMAGE = "image";
    public static final String CONTAINER_COMMAND = "command";
    public static final String CONTAINER_ARG = "arg";
    public static final String CONTAINER_ENV = "env";
    public static final String CONTAINER_WORKING_DIR = "workingDir";
    public static final String CONTAINER_PORT = "port";
    public static final String CONTAINER_LIVENESS_PROBE = "readinessProbe";
    public static final String CONTAINER_READINESS_PROBE = "livenessProbe";
    public static final String CONTAINER_VOLUME_MOUNT = "volumeMount";

    public static final String K8_REGISTRATION_SERVICE_TYPE = "k8-registration-service";
    public static final String K8_REGISTRATION_SERVICE_NAME = "Kubernetes Cluster Registration";
    public static final String K8_REGISTRATION_SERVICE_DESCRIPTION =
            "Registers a Kubernetes cluster as an Agility service provider";

    public static final String K8_POD_SERVICE_TYPE = "k8-pod-service";
    public static final String K8_POD_SERVICE_NAME = "Kubernetes Pod";
    public static final String K8_POD_SERVICE_DESCRIPTION =
            "Collection of one or more containers that are deployed together on the same host";

    public static final String K8_POD_LABEL = "label";
    public static final String K8_POD_RESTART_POLICY = "restartPolicy";
    public static final String K8_POD_REPLICAS = "replicas";

    public static final String K8_POD_RESTART_ALWAYS = "Always";
    public static final String K8_POD_RESTART_ONFAILURE = "OnFailure";
    public static final String K8_POD_RESTART_NEVER = "Never";

    public static final String K8_REPLICATION_CONTROLLER_SERVICE_TYPE = "k8-replication-controller-service";
    public static final String K8_REPLICATION_CONTROLLER_SERVICE_NAME = "Kubernetes Replication Controller";
    public static final String K8_REPLICATION_CONTROLLER_SERVICE_DESCRIPTION =
            "Monitors health and manages replication of one or more pods";
    public static final String K8_REPLICATION_CONTROLLER_NAME = "Replication Controller";

    public static final String K8_SERVICE_SERVICE_TYPE = "k8-service-service";
    public static final String K8_SERVICE_SERVICE_NAME = "Kubernetes Service";
    public static final String K8_SERVICE_SERVICE_DESCRIPTION = "Load balancer that exposes one or more pods behind a VIP";

    public static final String K8_SERVICE_TYPE = "type";
    public static final String K8_SERVICE_TYPE_CLUSTERIP = "ClusterIP";
    public static final String K8_SERVICE_TYPE_NODEPORT = "NodePort";
    public static final String K8_SERVICE_EXTERNAL_IP = "externalIP";
    public static final String K8_SERVICE_SELECTOR = "selector";

    public static final String K8_CONNECTION_TYPE = "k8-connection";
    public static final String K8_CONNECTION_NAME = "Kubernetes Connection";
    public static final String K8_CONNECTION_DESCRIPTION = "Provide container link semantics for dependencies";

    public static final String K8_POD_SERVICE_CONNECTION_TYPE = "k8-pod-to-service";
    public static final String K8_POD_SERVICE_CONNECTION_NAME = "Kubernetes Pod-Service Connection";
    public static final String K8_POD_SERVICE_CONNECTION_DESCRIPTION =
            "Provide container link semantics for public service endpoints";

    public static final String K8_IMAGE_SECRETS = "image-secrets";

}
