/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

import com.servicemesh.agility.api.AssetProperty;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.ServiceInstance;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.adapters.service.kubernetes.K8Constants;
import com.servicemesh.agility.adapters.service.kubernetes.K8ServiceAdapter;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Connection;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Endpoint;
import com.servicemesh.agility.adapters.service.kubernetes.json.ContainerStatus;
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
import com.servicemesh.agility.adapters.service.kubernetes.json.Service;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8ServiceInstanceOperations;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.helper.PropertyHelper;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.io.http.HttpMethod;


class K8ServiceInstanceOperationsSpec extends spock.lang.Specification
{
    // mock adapter and connection
    K8Connection conn = Mock(K8Connection);
    K8ConnectionFactory factory = Mock(K8ConnectionFactory);
    Reactor reactor = Mock(Reactor);
    K8ServiceAdapter adapter  = [ getReactor: { return reactor; } ] as K8ServiceAdapter; 
    K8ServiceInstanceOperations ops = new K8ServiceInstanceOperations(factory,adapter);
    
    // mock data for test cases
    Pod[] pods = new Pod[2];
    PodList podList = new PodList();
    
    def setup()
    {
        for(int i=0; i<pods.length; i++)
        {
            Pod pod = new Pod();
            pod.setMetadata(new Metadata());
            pod.getMetadata().setName("Pod" + i);
            pod.getMetadata().setName("Pod" + i);
            pod.setStatus(new PodStatus());
            pod.getStatus().setPhase("Running");
            pod.getStatus().setPodIP("10.1.1." + i);
            pod.getStatus().setHostIP("192.168.1." + i);
    
            ContainerStatus[] status = new ContainerStatus[1];
            status[0] = new ContainerStatus();
            status[0].setName("Container");
            status[0].setImage("image");
            pod.getStatus().setContainerStatuses(status);
            pods[i] = pod;
        }
        podList.setItems(pods);
    }
    
    
    def "create container"()
    {
        // setup provisioning request
        Link containerType = new Link();
        containerType.setName("container-service");
    
        ServiceInstanceProvisionRequest request = new ServiceInstanceProvisionRequest();
        ServiceInstance containerInstance = new ServiceInstance();
        containerInstance.setAssetType(containerType);
        containerInstance.setName("Container");
        containerInstance.setId(1);
        request.setServiceInstance(containerInstance);
        request.setProvider(new ServiceProvider());
    
        // should just complete
        when:
           def promise = ops.provision(request);
    
        then:
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
    
    def "create pod with replication controller"()
    {
        // setup provisioning request
        Link podType = new Link();
        podType.setName("k8-pod-service");
    
        ServiceInstanceProvisionRequest request = new ServiceInstanceProvisionRequest();
        ServiceInstance podInstance = new ServiceInstance();
        podInstance.setAssetType(podType);
        podInstance.setName("Pod");
        podInstance.setId(1);
        PropertyHelper.setString(podInstance.getAssetProperties(), "restartPolicy", "Always");
        AssetProperty ap = new AssetProperty();
        ap.setName("replicas");
        ap.setIntValue(2);
        podInstance.getAssetProperties().add(ap);
    
        Link containerType = new Link();
        containerType.setName("container-service");
        ServiceInstance container = new ServiceInstance();
        container.setAssetType(containerType);
        container.setName("Container");
        container.setId(1);
        PropertyHelper.setString(container.getAssetProperties(), "image", "image");
    
        request.setServiceInstance(podInstance);
        request.getDependents().add(container);
        request.setProvider(new ServiceProvider());
    
        // mock response to create replication controller
        ReplicationController rc = new ReplicationController();
        rc.setMetadata(new Metadata());
        rc.getMetadata().setName("rc1");
    
        when:
           factory.getConnection(request) >> conn;
           conn.execute(HttpMethod.POST, "/replicationcontrollers", _, ReplicationController.class) >> Promise.pure(rc);
           conn.execute(HttpMethod.GET, _, _, PodList.class) >> Promise.pure(podList);
           def promise = ops.provision(request);
    
        then:
           1*reactor.timerCreateRel(_,_) >> { arguments -> 
              final TimerHandler handler = arguments[1];
              handler.timerFire(0,0);
              return null;
           };
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
    
    def "release pod with replication controller"()
    {
        // setup provisioning request
        Link podType = new Link();
        podType.setName("k8-pod-service");
    
        ServiceInstanceReleaseRequest request = new ServiceInstanceReleaseRequest();
        ServiceInstance podInstance = new ServiceInstance();
        podInstance.setAssetType(podType);
        podInstance.setName("Pod");
        podInstance.setId(1);
        PropertyHelper.setString(podInstance.getAssetProperties(), "restartPolicy", "Never");
        PropertyHelper.setString(podInstance.getConfigurations(), "Label", "label/pod-1");
        PropertyHelper.setString(podInstance.getConfigurations(), "Replication Controller", "RC1");
        AssetProperty ap = new AssetProperty();
        ap.setName("replicas");
        ap.setIntValue(2);
        podInstance.getAssetProperties().add(ap);
    
        Link containerType = new Link();
        containerType.setName("container-service");
        ServiceInstance container = new ServiceInstance();
        container.setAssetType(containerType);
        container.setName("Container");
        container.setId(1);
        PropertyHelper.setString(container.getAssetProperties(), "image", "image");
    
        request.setServiceInstance(podInstance);
        request.getDependents().add(container);
        request.setProvider(new ServiceProvider());
    
        when:
           factory.getConnection(request) >> conn;
           conn.execute(HttpMethod.GET, _, _, PodList.class) >> Promise.pure(podList);
           def promise = ops.release(request);
    
        then:
           1*conn.execute(HttpMethod.DELETE, "/replicationcontrollers/RC1", null, com.servicemesh.agility.adapters.service.kubernetes.json.Status.class) >> 
              Promise.pure(new com.servicemesh.agility.adapters.service.kubernetes.json.Status());
           1*conn.execute(HttpMethod.DELETE, "/pods/Pod0", null, Pod.class) >> Promise.pure(pods[0]);
           1*conn.execute(HttpMethod.DELETE, "/pods/Pod1", null, Pod.class) >> Promise.pure(pods[1]);
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
    
    def "create pod without replication controller"()
    {
        // setup provisioning request
        Link podType = new Link();
        podType.setName("k8-pod-service");
    
        ServiceInstanceProvisionRequest request = new ServiceInstanceProvisionRequest();
        ServiceInstance podInstance = new ServiceInstance();
        podInstance.setAssetType(podType);
        podInstance.setName("Pod");
        podInstance.setId(1);
        PropertyHelper.setString(podInstance.getAssetProperties(), "restartPolicy", "Never");
        AssetProperty ap = new AssetProperty();
        ap.setName("replicas");
        ap.setIntValue(2);
        podInstance.getAssetProperties().add(ap);
    
        Link containerType = new Link();
        containerType.setName("container-service");
        ServiceInstance container = new ServiceInstance();
        container.setAssetType(containerType);
        container.setName("Container");
        container.setId(1);
        PropertyHelper.setString(container.getAssetProperties(), "image", "image");
    
        request.setServiceInstance(podInstance);
        request.getDependents().add(container);
        request.setProvider(new ServiceProvider());
    
        when:
           factory.getConnection(request) >> conn;
           conn.execute(HttpMethod.POST, "/pods", _, Pod.class) >> { arguments -> return Promise.pure(arguments[0]); }
           conn.execute(HttpMethod.GET, _, _, PodList.class) >> Promise.pure(podList);
           def promise = ops.provision(request);
    
        then:
           1*reactor.timerCreateRel(_,_) >> { arguments -> 
              final TimerHandler handler = arguments[1];
              handler.timerFire(0,0);
              return null;
           };
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
    
    
    def "release pod without replication controller"()
    {
        // setup provisioning request
        Link podType = new Link();
        podType.setName("k8-pod-service");
    
        ServiceInstanceReleaseRequest request = new ServiceInstanceReleaseRequest();
        ServiceInstance podInstance = new ServiceInstance();
        podInstance.setAssetType(podType);
        podInstance.setName("Pod");
        podInstance.setId(1);
        PropertyHelper.setString(podInstance.getAssetProperties(), "restartPolicy", "Never");
        PropertyHelper.setString(podInstance.getConfigurations(), "Label", "label/pod-1");
        AssetProperty ap = new AssetProperty();
        ap.setName("replicas");
        ap.setIntValue(2);
        podInstance.getAssetProperties().add(ap);
    
        Link containerType = new Link();
        containerType.setName("container-service");
        ServiceInstance container = new ServiceInstance();
        container.setAssetType(containerType);
        container.setName("Container");
        container.setId(1);
        PropertyHelper.setString(container.getAssetProperties(), "image", "image");
    
        request.setServiceInstance(podInstance);
        request.getDependents().add(container);
        request.setProvider(new ServiceProvider());
    
        when:
           factory.getConnection(request) >> conn;
           conn.execute(HttpMethod.GET, _, _, PodList.class) >> Promise.pure(podList);
           def promise = ops.release(request);
    
        then:
           1*conn.execute(HttpMethod.DELETE, "/pods/Pod0", null, Pod.class) >> Promise.pure(pods[0]);
           1*conn.execute(HttpMethod.DELETE, "/pods/Pod1", null, Pod.class) >> Promise.pure(pods[1]);
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }


    def "create load balancer service "()
    {
        // setup provisioning request
        Link serviceType = new Link();
        serviceType.setName("k8-service-service");
    
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setAssetType(serviceType);
        serviceInstance.setName("LoadBalancer");
        serviceInstance.setId(1);

        AssetProperty ap = new AssetProperty();
        ap.setName("type");
        ap.setStringValue("NodePort");
        serviceInstance.getAssetProperties().add(ap);

        ap = new AssetProperty();
        ap.setName("lb-port");
        ap.setIntValue(80);
        serviceInstance.getAssetProperties().add(ap);

        ap = new AssetProperty();
        ap.setName("lb-protocol");
        ap.setStringValue("tcp");
        serviceInstance.getAssetProperties().add(ap);

        ap = new AssetProperty();
        ap.setName("instance-port");
        ap.setIntValue(80);
        serviceInstance.getAssetProperties().add(ap);

        ap = new AssetProperty();
        ap.setName("instance-protocol");
        ap.setStringValue("tcp");
        serviceInstance.getAssetProperties().add(ap);
    
        Link podType = new Link();
        podType.setName("k8-pod-service");
    
        ServiceInstance podInstance = new ServiceInstance();
        podInstance.setAssetType(podType);
        podInstance.setName("Pod1");
        podInstance.setId(1);
        PropertyHelper.setString(podInstance.getConfigurations(), "Label", "label/Pod.1");
    
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setHostname("host01");

        ServiceInstanceProvisionRequest request = new ServiceInstanceProvisionRequest();
        request.setServiceInstance(serviceInstance);
        request.getDependents().add(podInstance);
        request.setProvider(serviceProvider);
    
        NodeStatus nodeStatus = new NodeStatus();
        NodeAddress[] nodeAddresses = new NodeAddress[1];
        nodeAddresses[0] = new NodeAddress();
        nodeAddresses[0].setType("LegacyHostIP");
        nodeAddresses[0].setAddress("192.168.1.1");
        nodeStatus.setAddresses(nodeAddresses);

        Node[] nodes = new Node[1];
        nodes[0] = new Node();
        nodes[0].setStatus(nodeStatus);

        NodeList nodeList = new NodeList();
        nodeList.setItems(nodes);

        when:
           factory.getConnection(request) >> conn;
           conn.execute(HttpMethod.POST, "/services", _, Service.class) >> { arguments -> return Promise.pure(arguments[2]); }
           def promise = ops.provision(request);
    
        then:
           def response = promise.get();
           1*conn.get("/api/v1/nodes", NodeList.class) >> Promise.pure(nodeList);
           response.getStatus() == Status.COMPLETE;
           response.getModified().size() == 1;
           serviceInstance.getConfigurations().size() == 8;
           serviceInstance.getConfigurations().get(0).getName().equals("Label");
           serviceInstance.getConfigurations().get(1).getName().equals("Name");
           serviceInstance.getConfigurations().get(2).getName().equals("Endpoint");
           serviceInstance.getConfigurations().get(3).getName().equals("TCP_PORT_80_ADDR");
           serviceInstance.getConfigurations().get(4).getName().equals("TCP_PORT_80_PORT");
    }


    def "release load balancer service "()
    {
        // setup provisioning request
        Link serviceType = new Link();
        serviceType.setName("k8-service-service");
    
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setAssetType(serviceType);
        serviceInstance.setName("LoadBalancer");
        serviceInstance.setId(1);
        PropertyHelper.setString(serviceInstance.getConfigurations(), "Name", "LoadBalancer-1");

        ServiceInstanceReleaseRequest request = new ServiceInstanceReleaseRequest();
        request.setServiceInstance(serviceInstance);
        request.setProvider(new ServiceProvider());

        when:
           factory.getConnection(request) >> conn;
           def promise = ops.release(request);
    
        then:
           1*conn.execute(HttpMethod.DELETE, "/services/LoadBalancer-1", null, com.servicemesh.agility.adapters.service.kubernetes.json.Status.class) >> { 
               return Promise.pure(new com.servicemesh.agility.adapters.service.kubernetes.json.Status()); }
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
}

