package com.servicemesh.agility.adapters.service.kubernetes.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.servicemesh.agility.adapters.service.kubernetes.K8Constants;
import com.servicemesh.agility.adapters.service.kubernetes.K8ServiceAdapter;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Connection;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Endpoint;
import com.servicemesh.agility.adapters.service.kubernetes.json.Container;
import com.servicemesh.agility.adapters.service.kubernetes.json.ContainerPort;
import com.servicemesh.agility.adapters.service.kubernetes.json.ContainerState;
import com.servicemesh.agility.adapters.service.kubernetes.json.ContainerStatus;
import com.servicemesh.agility.adapters.service.kubernetes.json.Env;
import com.servicemesh.agility.adapters.service.kubernetes.json.HTTPGetAction;
import com.servicemesh.agility.adapters.service.kubernetes.json.Metadata;
import com.servicemesh.agility.adapters.service.kubernetes.json.Node;
import com.servicemesh.agility.adapters.service.kubernetes.json.NodeAddress;
import com.servicemesh.agility.adapters.service.kubernetes.json.NodeList;
import com.servicemesh.agility.adapters.service.kubernetes.json.NodeStatus;
import com.servicemesh.agility.adapters.service.kubernetes.json.Pod;
import com.servicemesh.agility.adapters.service.kubernetes.json.PodList;
import com.servicemesh.agility.adapters.service.kubernetes.json.PodSpec;
import com.servicemesh.agility.adapters.service.kubernetes.json.PodStatus;
import com.servicemesh.agility.adapters.service.kubernetes.json.PodTemplateSpec;
import com.servicemesh.agility.adapters.service.kubernetes.json.Probe;
import com.servicemesh.agility.adapters.service.kubernetes.json.ReplicationController;
import com.servicemesh.agility.adapters.service.kubernetes.json.ReplicationControllerSpec;
import com.servicemesh.agility.adapters.service.kubernetes.json.Service;
import com.servicemesh.agility.adapters.service.kubernetes.json.ServicePort;
import com.servicemesh.agility.adapters.service.kubernetes.json.ServiceSpec;
import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.AssetProperty;
import com.servicemesh.agility.api.Connection;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Project;
import com.servicemesh.agility.api.ServiceInstance;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.api.ServiceProviderType;
import com.servicemesh.agility.api.ServiceState;
import com.servicemesh.agility.api.Template;
import com.servicemesh.agility.sdk.service.helper.PropertyHelper;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.operations.ServiceInstanceOperations;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.io.http.HttpMethod;

public class K8ServiceInstanceOperations extends ServiceInstanceOperations
{
    private static final Logger logger = Logger.getLogger(K8ServiceInstanceOperations.class);

    // TODO: make poll interval/retries configurable
    private static final long POLL_DELAY = 1000; // 1 sec
    private static final long POLL_INTERVAL = 10000; // 10 secs
    private static final int POLL_RETRIES = 30; // 5 minutes

    public static final String NAME = "Name";
    public static final String LABEL = "Label";
    public static final String LB_PORT = "lb-port";
    public static final String LB_PROTOCOL = "lb-protocol";
    public static final String INSTANCE_PORT = "instance-port";
    public static final String INSTANCE_PROTOCOL = "instance-protocol";

    private K8ConnectionFactory factory;
    private K8ServiceAdapter adapter;
    private Reactor reactor;

    public K8ServiceInstanceOperations(K8ConnectionFactory factory, K8ServiceAdapter adapter)
    {
        this.factory = factory;
        this.adapter = adapter;
        reactor = adapter.getReactor();
    }

    //
    // Service Dispatch
    //

    @Override
    public Promise<ServiceProviderResponse> release(ServiceInstanceReleaseRequest request)
    {
        ServiceInstance instance = request.getServiceInstance();
        Link service = instance.getService();
        switch (instance.getAssetType().getName())
        {
            case K8Constants.CONTAINER_SERVICE_TYPE:
                return releaseContainer(request);

            case K8Constants.K8_POD_SERVICE_TYPE:
                return releasePod(request);

            case K8Constants.K8_SERVICE_SERVICE_TYPE:
                return releaseService(request);

            case K8Constants.K8_REGISTRATION_SERVICE_TYPE:
                return removeProvider(request);

            default:
            {
                ServiceProviderResponse response = new ServiceProviderResponse();
                response.setStatus(Status.FAILURE);
                response.setMessage("Unsupported service type: " + service.getName());
                return Promise.pure(response);
            }
        }
    }

    @Override
    public Promise<ServiceProviderResponse> provision(ServiceInstanceProvisionRequest request)
    {
        ServiceInstance instance = request.getServiceInstance();
        Link service = instance.getService();
        switch (instance.getAssetType().getName())
        {
            case K8Constants.CONTAINER_SERVICE_TYPE:
                return provisionContainer(request);

            case K8Constants.K8_POD_SERVICE_TYPE:
                return provisionPod(request);

            case K8Constants.K8_SERVICE_SERVICE_TYPE:
                return provisionService(request);

            case K8Constants.K8_REGISTRATION_SERVICE_TYPE:
                return registerProvider(request);

            default:
            {
                ServiceProviderResponse response = new ServiceProviderResponse();
                response.setStatus(com.servicemesh.core.messaging.Status.FAILURE);
                response.setMessage("Unsupported service type: " + service.getName());
                return Promise.pure(response);
            }
        }
    }

    @Override
    public Promise<ServiceProviderResponse> reconfigure(ServiceInstanceReconfigureRequest request)
    {
        ServiceInstance instance = request.getServiceInstance();
        Link service = instance.getService();
        switch (instance.getAssetType().getName())
        {
            case K8Constants.K8_POD_SERVICE_TYPE:
                return reconfigurePod(request);
            case K8Constants.CONTAINER_SERVICE_TYPE:
            case K8Constants.K8_SERVICE_SERVICE_TYPE:
            case K8Constants.K8_REGISTRATION_SERVICE_TYPE:
            {
                ServiceProviderResponse response = new ServiceProviderResponse();
                response.setStatus(com.servicemesh.core.messaging.Status.COMPLETE);
                return Promise.pure(response);
            }
            default:
            {
                ServiceProviderResponse response = new ServiceProviderResponse();
                response.setStatus(com.servicemesh.core.messaging.Status.FAILURE);
                response.setMessage("Unsupported service type: " + service.getName());
                return Promise.pure(response);
            }
        }
    }

    //
    // Kubernetes Container
    //

    public Promise<ServiceProviderResponse> provisionContainer(ServiceInstanceProvisionRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(com.servicemesh.core.messaging.Status.COMPLETE);
        return Promise.pure(response);
    }

    public Promise<ServiceProviderResponse> releaseContainer(ServiceInstanceReleaseRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(com.servicemesh.core.messaging.Status.COMPLETE);
        return Promise.pure(response);
    }

    //
    // Kubernetes Pod
    //

    public Promise<ServiceProviderResponse> provisionPod(final ServiceInstanceProvisionRequest request)
    {
        try
        {
            ServiceInstance podInstance = request.getServiceInstance();
            PodSpec podSpec = buildPodSpec(request, podInstance);

            final K8Connection connection = factory.getConnection(request);
            Metadata metadata = new Metadata();
            String baseName = generateName(podInstance);
            metadata.setGenerateName(baseName + "-");
            metadata.setNamespace(connection.getNamespace());

            // build up a set of labels for the pod - walk through any service bindings and
            // add a label that will match their selector

            Map<String, String> labels = new HashMap<String, String>();
            StringBuilder label = new StringBuilder();
            label.append(K8Constants.LABEL_PREFIX);
            label.append(baseName);
            label.append(".");
            label.append(podInstance.getId());
            labels.put(label.toString(), "Pod");
            metadata.setLabels(labels);
            PropertyHelper.setString(podInstance.getConfigurations(), LABEL, label.toString());

            String restartPolicy = PropertyHelper.getString(podInstance.getAssetProperties(), K8Constants.K8_POD_RESTART_POLICY,
                    K8Constants.K8_POD_RESTART_ALWAYS);
            final int replicas = PropertyHelper.getInteger(podInstance.getAssetProperties(), K8Constants.K8_POD_REPLICAS, 0);
            if (replicas > 0 && restartPolicy.equals(K8Constants.K8_POD_RESTART_ALWAYS))
            {
                PodTemplateSpec template = new PodTemplateSpec();
                template.setMetadata(metadata);
                template.setSpec(podSpec);
                ReplicationControllerSpec rcSpec = new ReplicationControllerSpec();
                rcSpec.setReplicas(replicas);
                rcSpec.setTemplate(template);
                ReplicationController rc = new ReplicationController();
                rc.setMetadata(metadata);
                rc.setSpec(rcSpec);

                Promise<ReplicationController> promise =
                        connection.execute(HttpMethod.POST, "/replicationcontrollers", rc, ReplicationController.class);
                return promise.flatMap((ReplicationController result) -> {
                    PropertyHelper.setString(podInstance.getConfigurations(), K8Constants.K8_REPLICATION_CONTROLLER_NAME,
                            result.getMetadata().getName());
                    Promise<List<Pod>> poll_promise = pollPod(connection, label.toString(), replicas);
                    return poll_promise.flatMap((List<Pod> running) -> {
                        return provisionPodResponse(podInstance, request.getDependents(), running);
                    });
                });
            }
            else
            {
                List<Promise<Pod>> promises = new ArrayList<Promise<Pod>>();
                for (int i = 0; i < replicas; i++)
                {
                    Pod pod = new Pod();
                    pod.setMetadata(metadata);
                    pod.setKind("Pod");
                    pod.setApiVersion(K8Endpoint.API_VERSION);
                    pod.setSpec(podSpec);

                    Promise<Pod> promise = connection.execute(HttpMethod.POST, "/pods", pod, Pod.class);
                    promises.add(promise);
                }
                return Promise.sequence(promises).flatMap((List<Pod> result) -> {
                    Promise<List<Pod>> poll_promise = pollPod(connection, label.toString(), replicas);
                    return poll_promise.flatMap((List<Pod> running) -> {
                        return provisionPodResponse(podInstance, request.getDependents(), running);
                    });
                });
            }
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    private PodSpec buildPodSpec(ServiceInstanceRequest request, ServiceInstance podInstance) throws Exception
    {
        List<Env> links = buildLinks(request);
        Set<String> containerNames = new HashSet<String>();
        List<Container> containers = new ArrayList<Container>();
        for (Asset dependent : request.getDependents())
        {
            if (!instanceOf(dependent, K8Constants.CONTAINER_SERVICE_TYPE))
            {
                continue;
            }

            ServiceInstance containerInstance = (ServiceInstance) dependent;
            String image = PropertyHelper.getString(dependent.getAssetProperties(), K8Constants.CONTAINER_IMAGE, null);
            if (image == null)
            {
                throw new Exception("Container image must be specified");
            }

            String livenessProbe = null;
            String readinessProbe = null;
            List<String> cmd = new ArrayList<String>();
            List<String> args = new ArrayList<String>();
            List<Env> env = new ArrayList<Env>(links);
            List<ContainerPort> ports = new ArrayList<ContainerPort>();
            for (AssetProperty ap : dependent.getAssetProperties())
            {
                switch (ap.getName())
                {
                    case K8Constants.CONTAINER_ARG:
                        args.add(ap.getStringValue());
                        break;
                    case K8Constants.CONTAINER_COMMAND:
                        cmd.add(ap.getStringValue());
                        break;
                    case K8Constants.CONTAINER_PORT:
                    {
                        ContainerPort port = new ContainerPort();
                        int index = ap.getStringValue().indexOf("/");
                        if (index > 0)
                        {
                            port.setProtocol(ap.getStringValue().substring(0, index));
                            port.setContainerPort(ap.getStringValue().substring(index + 1));
                        }
                        else
                        {
                            port.setProtocol("tcp");
                            port.setContainerPort(ap.getStringValue());
                        }
                        ports.add(port);
                        break;
                    }
                    case K8Constants.CONTAINER_ENV:
                    {
                        int index = ap.getStringValue().indexOf("=");
                        if (index > 0)
                        {
                            Env e = new Env();
                            e.setName(ap.getStringValue().substring(0, index));
                            e.setValue(ap.getStringValue().substring(index + 1));
                            env.add(e);
                        }
                        break;
                    }
                    case K8Constants.CONTAINER_LIVENESS_PROBE:
                        livenessProbe = ap.getStringValue();
                        break;
                    case K8Constants.CONTAINER_READINESS_PROBE:
                        readinessProbe = ap.getStringValue();
                        break;

                }
            }

            // add in any container variables
            for (AssetProperty ap : containerInstance.getVariables())
            {
                Env e = new Env();
                e.setName(ap.getName().toUpperCase());
                if (ap.getStringValue() != null)
                {
                    e.setValue(ap.getStringValue());
                }
                else if (ap.getIntValue() != null)
                {
                    e.setValue("" + ap.getIntValue());
                }
                else if (ap.getFloatValue() != null)
                {
                    e.setValue("" + ap.getFloatValue());
                }
                else if (ap.getDateValue() != null)
                {
                    e.setValue(ap.getDateValue().toString());
                }
                env.add(e);
            }

            // add in any variables inherited from environment
            for (AssetProperty ap : request.getVariables())
            {
                Env e = new Env();
                e.setName(ap.getName().toUpperCase());
                if (ap.getStringValue() != null)
                {
                    e.setValue(ap.getStringValue());
                }
                else if (ap.getIntValue() != null)
                {
                    e.setValue("" + ap.getIntValue());
                }
                else if (ap.getFloatValue() != null)
                {
                    e.setValue("" + ap.getFloatValue());
                }
                else if (ap.getDateValue() != null)
                {
                    e.setValue(ap.getDateValue().toString());
                }
                env.add(e);
            }

            String containerName = generateUniqueName(containerNames, dependent);
            PropertyHelper.setString(containerInstance.getConfigurations(), NAME, containerName);

            Container container = new Container();
            container.setName(containerName);
            container.setImage(image);
            container.setArgs(args.toArray(new String[args.size()]));
            container.setCommand(cmd.toArray(new String[cmd.size()]));
            container.setEnv(env.toArray(new Env[args.size()]));

            if (livenessProbe != null)
            {
                Probe probe = new Probe();
                probe.setHttpGet(new HTTPGetAction(livenessProbe));
                container.setLivenessProbe(probe);
            }

            if (readinessProbe != null)
            {
                Probe probe = new Probe();
                probe.setHttpGet(new HTTPGetAction(readinessProbe));
                container.setReadinessProbe(probe);
            }
            containers.add(container);
        }
        PodSpec podSpec = new PodSpec();
        podSpec.setContainers(containers.toArray(new Container[containers.size()]));
        return podSpec;
    }

    private Promise<List<Pod>> pollPod(final K8Connection connection, final String label, int replicas)
    {
        return pollPod(connection, label, replicas, POLL_DELAY, POLL_RETRIES);
    }

    private Promise<List<Pod>> pollPod(final K8Connection connection, final String label, final int replicas, long delay,
            int retries)
    {
        if (retries <= 0)
        {
            return Promise.pure(new Exception("pod failed to provision with configured timeout"));
        }

        final StringBuilder labelSelector = new StringBuilder();
        labelSelector.append(label);
        labelSelector.append("=Pod");

        Promise<Promise<List<Pod>>> promise = Promise.delayed(reactor, delay, () -> {
            Promise<PodList> pp = connection.execute(HttpMethod.GET, "/pods?labelSelector=" + labelSelector, null, PodList.class);
            return pp.flatMap((PodList podList) -> {

                List<Pod> pods = new ArrayList<Pod>();
                for (Pod pod : podList.getItems())
                {
                    PodStatus podStatus = pod.getStatus();
                    if (podStatus == null || podStatus.getPhase().equalsIgnoreCase("Pending"))
                    {
                        if (podStatus != null && podStatus.getContainerStatuses() != null)
                        {
                            for (ContainerStatus cs : podStatus.getContainerStatuses())
                            {
                                ContainerState state = cs.getState();
                                if (state != null && state.getWaiting() != null
                                        && state.getWaiting().getReason().equalsIgnoreCase("PullImageError"))
                                {
                                    return Promise.pure(new Exception(state.getWaiting().getMessage()));
                                }
                            }
                        }
                        return pollPod(connection, label, replicas, POLL_INTERVAL, retries - 1);
                    }
                    pods.add(pod);
                }
                if (pods.size() != replicas)
                {
                    return pollPod(connection, label, replicas, POLL_INTERVAL, retries - 1);
                }
                return Promise.pure(pods);
            });
        });
        return promise.flatMap((Promise<List<Pod>> pp) -> {
            return pp;
        });
    }

    private Promise<ServiceProviderResponse> provisionPodResponse(final ServiceInstance podInstance, final List<Asset> dependents,
            List<Pod> pods)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();

        // preserve label and rc but otherwise clear all config and rebuild
        List<AssetProperty> config = new ArrayList<AssetProperty>();
        for (AssetProperty ap : podInstance.getConfigurations())
        {
            if (ap.getName().equals(LABEL) || ap.getName().equals(K8Constants.K8_REPLICATION_CONTROLLER_NAME))
            {
                config.add(ap);
            }
        }
        podInstance.getConfigurations().clear();
        podInstance.getConfigurations().addAll(config);

        // likewise on dependent container services
        for (Asset dependent : dependents)
        {
            if (instanceOf(dependent, K8Constants.CONTAINER_SERVICE_TYPE))
            {
                ServiceInstance containerInstance = (ServiceInstance) dependent;
                String containerName = PropertyHelper.getString(containerInstance.getConfigurations(), NAME, "");
                containerInstance.getConfigurations().clear();
                PropertyHelper.setString(containerInstance.getConfigurations(), NAME, containerName);
            }
        }

        // update config
        for (Pod pod : pods)
        {
            String podName = pod.getMetadata().getName();
            PodStatus podStatus = pod.getStatus();
            if (podStatus != null)
            {
                PropertyHelper.setString(podInstance.getConfigurations(), podName + ".hostIP", podStatus.getHostIP());
                PropertyHelper.setString(podInstance.getConfigurations(), podName + ".podIP", podStatus.getPodIP());
                for (Asset dependent : dependents)
                {
                    if (instanceOf(dependent, K8Constants.CONTAINER_SERVICE_TYPE))
                    {
                        ServiceInstance containerInstance = (ServiceInstance) dependent;
                        String containerName = PropertyHelper.getString(containerInstance.getConfigurations(), NAME, "");
                        for (ContainerStatus containerStatus : podStatus.getContainerStatuses())
                        {
                            if (containerName.equals(containerStatus.getName()))
                            {
                                PropertyHelper.setString(containerInstance.getConfigurations(), "Image",
                                        containerStatus.getImage());
                                AssetProperty ap = new AssetProperty();
                                ap.setName("ContainerID");
                                ap.setStringValue(containerStatus.getContainerID());
                                containerInstance.getConfigurations().add(ap);
                            }
                        }
                        containerInstance.setState(ServiceState.RUNNING);
                        response.getModified().add(containerInstance);
                    }
                }
            }

            AssetProperty name = new AssetProperty();
            name.setName(NAME);
            name.setStringValue(podName);
            podInstance.getConfigurations().add(name);
        }
        podInstance.setState(ServiceState.RUNNING);
        response.getModified().add(podInstance);
        response.setStatus(com.servicemesh.core.messaging.Status.COMPLETE);
        return Promise.pure(response);
    }

    public Promise<ServiceProviderResponse> releasePod(final ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance podInstance = request.getServiceInstance();
        try
        {
            final K8Connection connection = factory.getConnection(request);
            String rcName =
                    PropertyHelper.getString(podInstance.getConfigurations(), K8Constants.K8_REPLICATION_CONTROLLER_NAME, null);
            if (rcName != null)
            {
                Promise<com.servicemesh.agility.adapters.service.kubernetes.json.Status> promise =
                        connection.execute(HttpMethod.DELETE, "/replicationcontrollers/" + rcName, null,
                                com.servicemesh.agility.adapters.service.kubernetes.json.Status.class);
                return promise.flatMap((com.servicemesh.agility.adapters.service.kubernetes.json.Status status) -> {
                    return releasePods(connection, request);
                });
            }
            return releasePods(connection, request);
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    private Promise<ServiceProviderResponse> releasePods(final K8Connection connection,
            final ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance podInstance = request.getServiceInstance();

        String label = PropertyHelper.getString(podInstance.getConfigurations(), LABEL, null);
        if (label != null)
        {
            Promise<PodList> pp =
                    connection.execute(HttpMethod.GET, "/pods?labelSelector=" + label + "=Pod", null, PodList.class);
            return pp.flatMap((PodList podList) -> {
                List<Promise<Pod>> promises = new ArrayList<Promise<Pod>>();
                for (Pod pod : podList.getItems())
                {
                    Promise<Pod> promise =
                            connection.execute(HttpMethod.DELETE, "/pods/" + pod.getMetadata().getName(), null, Pod.class);
                    promises.add(promise);
                }
                return Promise.sequence(promises).flatMap((List<Pod> pods) -> {
                    return releasePodResponse(request);
                });
            });
        }
        return releasePodResponse(request);
    }

    private Promise<ServiceProviderResponse> releasePodResponse(ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance podInstance = request.getServiceInstance();
        ServiceProviderResponse response = new ServiceProviderResponse();
        for (Asset dependent : request.getDependents())
        {
            if (instanceOf(dependent, K8Constants.CONTAINER_SERVICE_TYPE))
            {
                ServiceInstance containerInstance = (ServiceInstance) dependent;
                containerInstance.setState(ServiceState.UNPROVISIONED);
                containerInstance.getConfigurations().clear();
                response.getModified().add(containerInstance);
            }
        }
        podInstance.getConfigurations().clear();
        response.getModified().add(podInstance);
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    private Promise<ServiceProviderResponse> reconfigurePod(ServiceInstanceReconfigureRequest request)
    {
        final ServiceInstance podInstance = request.getServiceInstance();
        String replicationController =
                PropertyHelper.getString(podInstance.getConfigurations(), K8Constants.K8_REPLICATION_CONTROLLER_NAME, null);
        if (replicationController != null)
        {
            return reconfigureReplicationController(request, replicationController);
        }

        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    private Promise<ServiceProviderResponse> reconfigureReplicationController(final ServiceInstanceReconfigureRequest request,
            final String rcName)
    {
        final ServiceInstance podInstance = request.getServiceInstance();
        try
        {
            final K8Connection connection = factory.getConnection(request);
            Promise<ReplicationController> get =
                    connection.execute(HttpMethod.GET, "/replicationcontrollers/" + rcName, null, ReplicationController.class);
            return get.flatMap((ReplicationController rc) -> {

                final int replicas = PropertyHelper.getInteger(podInstance.getAssetProperties(), K8Constants.K8_POD_REPLICAS, 1);
                if (rc != null && rc.getSpec().getReplicas() != replicas)
                {
                    rc.getSpec().setReplicas(replicas);
                    rc.setStats(null);
                    rc.getMetadata().setResourceVersion(null);
                    rc.getMetadata().setCreationTimestamp(null);
                    rc.getMetadata().setSelfLink(null);
                    rc.getMetadata().setUid(null);

                    Promise<ReplicationController> put = connection.execute(HttpMethod.PUT, "/replicationcontrollers/" + rcName,
                            rc, ReplicationController.class);
                    return put.flatMap((ReplicationController rcNew) -> {
                        String podLabel = PropertyHelper.getString(podInstance.getConfigurations(), LABEL, null);
                        Promise<List<Pod>> poll = pollPod(connection, podLabel, replicas);
                        return poll.flatMap((List<Pod> podList) -> {
                            return provisionPodResponse(podInstance, request.getDependents(), podList);
                        });
                    });
                }
                else
                {
                    ServiceProviderResponse response = new ServiceProviderResponse();
                    response.setStatus(Status.COMPLETE);
                    return Promise.pure(response);
                }
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    //
    // Kubernetes Service
    //

    public Promise<ServiceProviderResponse> provisionService(ServiceInstanceProvisionRequest request)
    {
        try
        {
            final ServiceInstance serviceInstance = request.getServiceInstance();
            Map<String, String> selector = new HashMap<String, String>();
            for (Asset dependency : request.getDependencies())
            {
                if (instanceOf(dependency, K8Constants.K8_POD_SERVICE_TYPE))
                {
                    ServiceInstance podInstance = (ServiceInstance) dependency;
                    String label = PropertyHelper.getString(podInstance.getConfigurations(), LABEL, null);
                    if (label != null)
                    {
                        selector.put(label, "Pod");
                    }
                }
            }

            List<Integer> lb_ports = new ArrayList<Integer>();
            List<String> lb_protos = new ArrayList<String>();
            List<Integer> instance_ports = new ArrayList<Integer>();
            List<String> instance_protos = new ArrayList<String>();
            for (AssetProperty ap : serviceInstance.getAssetProperties())
            {
                switch (ap.getName())
                {
                    case LB_PORT:
                        lb_ports.add(ap.getIntValue());
                        break;
                    case LB_PROTOCOL:
                        lb_protos.add(ap.getStringValue());
                        break;
                    case INSTANCE_PORT:
                        instance_ports.add(ap.getIntValue());
                        break;
                    case INSTANCE_PROTOCOL:
                        instance_protos.add(ap.getStringValue());
                        break;
                }
            }

            if ((instance_protos.size() > 0 && instance_protos.size() != instance_ports.size())
                    || instance_ports.size() != lb_ports.size())
            {
                throw new Exception("Service port/protocol definitions are mismatched");
            }

            List<ServicePort> ports = new ArrayList<ServicePort>();
            for (int i = 0; i < instance_ports.size(); i++)
            {
                ServicePort port = new ServicePort();
                port.setPort(lb_ports.get(i));
                port.setTargetPort(instance_ports.get(i));
                port.setProtocol((instance_protos.size() > i) ? instance_protos.get(i).toUpperCase() : "TCP");
                ports.add(port);
            }

            ServiceSpec spec = new ServiceSpec();
            spec.setType(PropertyHelper.getString(serviceInstance.getAssetProperties(), K8Constants.K8_SERVICE_TYPE,
                    K8Constants.K8_SERVICE_TYPE_CLUSTERIP));
            spec.setSelector(selector);
            spec.setPorts(ports.toArray(new ServicePort[ports.size()]));

            final K8Connection connection = factory.getConnection(request);
            Metadata metadata = new Metadata();
            String baseName = generateName(serviceInstance);
            metadata.setGenerateName(baseName + "-");
            metadata.setNamespace(connection.getNamespace());

            StringBuilder label = new StringBuilder();
            label.append(K8Constants.LABEL_PREFIX);
            label.append(baseName);
            label.append(".");
            label.append(serviceInstance.getId());
            PropertyHelper.setString(serviceInstance.getConfigurations(), LABEL, label.toString());

            Service service = new Service();
            service.setKind("Service");
            service.setApiVersion(K8Endpoint.API_VERSION);
            service.setMetadata(metadata);
            service.setSpec(spec);

            Promise<Service> promise = connection.execute(HttpMethod.POST, "/services", service, Service.class);
            return promise.flatMap((final Service result) -> {

                final ServiceProviderResponse response = new ServiceProviderResponse();
                PropertyHelper.setString(serviceInstance.getConfigurations(), NAME, result.getMetadata().getName());
                serviceInstance.setState(ServiceState.RUNNING);
                response.getModified().add(serviceInstance);
                response.setStatus(com.servicemesh.core.messaging.Status.COMPLETE);

                if (spec.getType().equals(K8Constants.K8_SERVICE_TYPE_NODEPORT))
                {
                    Promise<NodeList> pnodes = connection.get("/api/" + K8Endpoint.API_VERSION + "/nodes", NodeList.class);
                    return pnodes.map((NodeList nodelist) -> {
                        buildEndpoints(serviceInstance.getConfigurations(), result.getSpec(), nodelist.getItems(),
                                request.getProvider());
                        return response;
                    });
                }
                return Promise.pure(response);
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    private void buildEndpoints(List<AssetProperty> config, ServiceSpec nodeSpec, Node[] nodes, ServiceProvider provider)
    {
        String provider_address = null;
        for (ServicePort port : nodeSpec.getPorts())
        {
            for (Node node : nodes)
            {
                NodeStatus status = node.getStatus();
                if (status != null)
                {
                    for (NodeAddress nodeAddress : status.getAddresses())
                    {
                        if (nodeAddress.getType().equals("LegacyHostIP"))
                        {
                            if (provider_address == null)
                            {
                                provider_address = provider.getHostname();
                                int index = provider_address.indexOf(":");
                                if (index > 0)
                                {
                                    provider_address = provider_address.substring(0, index);
                                }
                                buildEndpoint(config, port, provider_address);

                            }
                            if (!provider_address.equals(nodeAddress.getAddress()))
                            {
                                buildEndpoint(config, port, nodeAddress.getAddress());
                            }
                        }
                    }
                }
            }
        }
    }

    public void buildEndpoint(List<AssetProperty> config, ServicePort port, String address)
    {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append(address);
        endpoint.append(":");
        endpoint.append(port.getNodePort());

        AssetProperty ap = new AssetProperty();
        ap.setName("Endpoint");
        ap.setDescription("Service endpoint");
        ap.setStringValue(endpoint.toString());
        config.add(ap);

        ap = new AssetProperty();
        StringBuilder sb = new StringBuilder();
        sb.append(port.getProtocol().toUpperCase());
        sb.append("_PORT_");
        sb.append(port.getPort());
        sb.append("_ADDR");
        ap.setName(sb.toString());
        ap.setDescription("Service port mapping");
        ap.setStringValue(address);
        config.add(ap);

        ap = new AssetProperty();
        sb = new StringBuilder();
        sb.append(port.getProtocol().toUpperCase());
        sb.append("_PORT_");
        sb.append(port.getPort());
        sb.append("_PORT");
        ap.setName(sb.toString());
        ap.setDescription("Service port mapping");
        if (port.getNodePort() != null)
        {
            ap.setStringValue(port.getNodePort().toString());
        }
        else
        {
            ap.setStringValue(port.getPort().toString());
        }
        config.add(ap);
    }

    public Promise<ServiceProviderResponse> releaseService(final ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance serviceInstance = request.getServiceInstance();
        try
        {
            final K8Connection connection = factory.getConnection(request);
            String serviceName = PropertyHelper.getString(serviceInstance.getConfigurations(), NAME, null);
            if (serviceName == null)
            {
                return releaseServiceResponse(request);
            }

            Promise<com.servicemesh.agility.adapters.service.kubernetes.json.Status> promise =
                    connection.execute(HttpMethod.DELETE, "/services/" + serviceName, null,
                            com.servicemesh.agility.adapters.service.kubernetes.json.Status.class);
            return promise.flatMap((com.servicemesh.agility.adapters.service.kubernetes.json.Status response) -> {
                return releaseServiceResponse(request);
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    private Promise<ServiceProviderResponse> releaseServiceResponse(ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance serviceInstance = request.getServiceInstance();
        ServiceProviderResponse response = new ServiceProviderResponse();
        serviceInstance.getConfigurations().clear();
        response.getModified().add(serviceInstance);
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    //
    // Registration Operations
    //

    public Promise<ServiceProviderResponse> registerProvider(ServiceInstanceProvisionRequest request)
    {
        final ServiceInstance serviceInstance = request.getServiceInstance();

        Link serviceProviderLink = new Link();
        serviceProviderLink.setName("serviceprovider");
        serviceProviderLink.setType("application/" + ServiceProvider.class.getName() + "+xml");

        Link serviceProviderTypeLink = new Link();
        serviceProviderTypeLink.setName(K8ServiceAdapter.SERVICE_PROVIDER_NAME);
        serviceProviderTypeLink.setType("application/" + ServiceProviderType.class.getName() + "+xml");

        for (Asset dependency : request.getDependencies())
        {
            if (dependency instanceof Template)
            {
                List<Promise<Asset>> promises = new ArrayList<Promise<Asset>>();
                Template master = (Template) dependency;
                promises.add(adapter.getAsset(Project.class.getName(), master.getProject().getId()));
                for (Link instance : master.getInstances())
                {
                    promises.add(adapter.getAsset(Instance.class.getName(), instance.getId()));
                }
                return Promise.sequence(promises).flatMap((List<Asset> assets) -> {

                    List<Promise<Asset>> providers = new ArrayList<Promise<Asset>>();
                    Project project = (Project) assets.get(0);
                    for (int i = 1; i < assets.size(); i++)
                    {
                        Instance instance = (Instance) assets.get(i);
                        StringBuilder uri = new StringBuilder();
                        uri.append(instance.publicAddress);
                        uri.append(":8080");

                        StringBuilder name = new StringBuilder();
                        name.append(project.getName());
                        name.append("/");
                        name.append(serviceInstance.name);

                        ServiceProvider provider = new ServiceProvider();
                        provider.setAssetType(serviceProviderLink);
                        provider.setType(serviceProviderTypeLink);
                        provider.setName(name.toString());
                        provider.setDescription(serviceInstance.getDescription());
                        provider.setHostname(uri.toString());
                        provider.setCloud(master.getCloud());
                        providers.add(adapter.createAsset(provider, project));
                    }

                    return Promise.sequence(providers).map((List<Asset> created) -> {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        for (Asset asset : created)
                        {
                            ServiceProvider provider = (ServiceProvider) asset;
                            AssetProperty name = new AssetProperty();
                            name.setName("Provider");
                            name.setStringValue(provider.getName());
                            serviceInstance.getConfigurations().add(name);
                            AssetProperty id = new AssetProperty();
                            id.setName("ProviderID");
                            id.setIntValue(provider.getId());
                            serviceInstance.getConfigurations().add(id);
                        }
                        response.getModified().add(serviceInstance);
                        response.setStatus(Status.COMPLETE);
                        return response;
                    });
                });
            }
        }

        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setMessage("No instance found");
        response.setStatus(Status.FAILURE);
        return Promise.pure(response);
    }

    public Promise<ServiceProviderResponse> removeProvider(ServiceInstanceReleaseRequest request)
    {
        final ServiceInstance serviceInstance = request.getServiceInstance();
        List<Promise<Asset>> promises = new ArrayList<Promise<Asset>>();
        for (AssetProperty ap : serviceInstance.getConfigurations())
        {
            if (ap.getName().equals("ProviderID"))
            {
                promises.add(adapter.getAsset(ServiceProvider.class.getName(), ap.getIntValue()));
            }
        }

        Promise<ServiceProviderResponse> promise = Promise.sequence(promises).map((List<Asset> assets) -> {

            for (Asset asset : assets)
            {
                adapter.deleteAsset(asset, null);
            }
            ServiceProviderResponse response = new ServiceProviderResponse();
            response.getModified().add(serviceInstance);
            response.setStatus(Status.COMPLETE);
            return response;

        });

        return promise.recover((Throwable t) -> {

            ServiceProviderResponse response = new ServiceProviderResponse();
            if (t.getMessage().contains("found"))
            {
                response.setStatus(Status.COMPLETE);
            }
            else
            {
                response.setStatus(Status.FAILURE);
                response.setMessage(t.getMessage());
            }
            return response;
        });

    }

    //
    // Common Operations
    //

    private List<Env> buildLinks(ServiceInstanceRequest request)
    {
        List<Env> links = new ArrayList<Env>();
        for (Connection connection : request.getSrcConnections())
        {
            String link = null;
            List<Integer> publish = new ArrayList<Integer>();
            for (AssetProperty ap : connection.getAssetProperties())
            {
                if (ap.getName().equals("publish"))
                {
                    publish.add(ap.getIntValue());
                }
                if (ap.getName().equals("link"))
                {
                    link = ap.getStringValue();
                }
            }

            Asset asset = lookup(request.getDependencies(), connection.getDestination());
            if (asset == null)
            {
                continue;
            }
            if (asset instanceof Template)
            {
                Template template = (Template) asset;
                for (Link l : template.getInstances())
                {
                    Instance instance = (Instance) lookup(request.getDependencies(), l);
                    if (instance == null)
                    {
                        continue;
                    }

                    String name = link;
                    if (name == null)
                    {
                        name = instance.getTemplate().getName();
                    }
                    for (Integer port : publish)
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(envKey(name));
                        sb.append("_PORT_");
                        sb.append(port);
                        sb.append("_TCP_ADDR");

                        Env env = new Env();
                        env.setName(sb.toString());
                        if (instance.getPublicAddress() != null)
                        {
                            env.setValue(instance.getPublicAddress());
                        }
                        else
                        {
                            env.setValue(instance.getPrivateAddress());
                        }
                        links.add(env);

                        sb = new StringBuilder();
                        sb.append(envKey(name));
                        sb.append("_PORT_");
                        sb.append(port);
                        sb.append("_TCP_PORT");
                        env = new Env();
                        env.setName(sb.toString());
                        env.setValue(port.toString());
                        links.add(env);
                    }
                }
            }
            if (asset instanceof ServiceInstance)
            {
                ServiceInstance instance = (ServiceInstance) asset;
                String name = link;
                if (name == null)
                {
                    name = instance.getName();
                }

                // only add first encountered
                Set<String> first = new HashSet<String>();
                for (AssetProperty ap : instance.getConfigurations())
                {
                    if (first.contains(ap.getName()))
                    {
                        continue;
                    }

                    if (ap.getName().startsWith("TCP_"))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(envKey(name));
                        sb.append("_");
                        sb.append(ap.getName());
                        Env env = new Env();
                        env.setName(sb.toString());
                        env.setValue(ap.getStringValue());
                        links.add(env);
                        first.add(ap.getName());
                    }
                }
            }
        }
        return links;
    }

    public static boolean instanceOf(Asset asset, String type)
    {
        if (asset instanceof ServiceInstance && asset.getAssetType() != null)
        {
            return asset.getAssetType().getName().equals(type);
        }
        return false;
    }

    public static String generateUniqueName(Set<String> existing, Asset asset)
    {
        int cnt = 1;
        String generated = generateName(asset);
        String retval = generated;
        while (existing.contains(retval))
        {
            retval = generated + cnt;
            cnt++;
        }
        existing.add(retval);
        return retval;
    }

    public static String generateName(final Asset asset)
    {
        String serviceName = asset.getName();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < serviceName.length() && i < 32; i++)
        {
            if (Character.isJavaLetterOrDigit(serviceName.charAt(i)))
            {
                out.append(Character.toLowerCase(serviceName.charAt(i)));
            }
        }
        return out.toString();
    }

    public static String envKey(String name)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < name.length() && i < 32; i++)
        {
            char ch = name.charAt(i);
            if (Character.isJavaLetterOrDigit(ch))
            {
                out.append(Character.toUpperCase(ch));
            }
            if (Character.isWhitespace(ch))
            {
                out.append("_");
            }
        }
        return out.toString();
    }

    public static void expandLabel(Map<String, String> labels, String label, List<AssetProperty> variables)
    {
        int index = label.indexOf("=");
        if (index > 0)
        {
            String key = label.substring(0, index);
            String value = label.substring(index + 1);
            if (value.contains("${"))
            {
                // TODO : fix expansion
            }
            labels.put(key, value);
        }
    }

    private static Asset lookup(List<Asset> assets, Link link)
    {
        for (Asset asset : assets)
        {
            if (link.getType().contains(asset.getClass().getName()) && asset.getId() == link.getId())
            {
                return asset;
            }
        }
        return null;
    }
}
