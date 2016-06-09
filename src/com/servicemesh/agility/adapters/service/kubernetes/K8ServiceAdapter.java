/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8LogMethod;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8ServiceInstanceLifecycleOperations;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8ServiceInstanceOperations;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8ServiceProviderOperations;
import com.servicemesh.agility.api.AssetProperty;
import com.servicemesh.agility.api.AssetType;
import com.servicemesh.agility.api.Connection;
import com.servicemesh.agility.api.ConnectionDefinition;
import com.servicemesh.agility.api.Editor;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.PrimitiveType;
import com.servicemesh.agility.api.PropertyDefinition;
import com.servicemesh.agility.api.PropertyType;
import com.servicemesh.agility.api.PropertyTypeValue;
import com.servicemesh.agility.api.Service;
import com.servicemesh.agility.api.ServiceProviderOption;
import com.servicemesh.agility.api.ServiceProviderType;
import com.servicemesh.agility.api.ValueConstraintType;
import com.servicemesh.agility.api.Workload;
import com.servicemesh.agility.sdk.service.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.service.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.service.spi.IServiceInstance;
import com.servicemesh.agility.sdk.service.spi.IServiceInstanceLifecycle;
import com.servicemesh.agility.sdk.service.spi.IServiceProvider;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.core.reactor.TimerReactor;

public class K8ServiceAdapter extends ServiceAdapter
{
    private static final Logger logger = Logger.getLogger(K8ServiceAdapter.class);

    public static final String ADAPTER_VERSION;
    public static final String ADAPTER_VENDOR;

    public static final String SERVICE_PROVIDER_TYPE = "kubernetes-service-provider";
    public static final String SERVICE_PROVIDER_NAME = "Kubernetes Service Provider";
    public static final String SERVICE_PROVIDER_DESCRIPTION;
    public static final String SERVICE_REGISTRATION_PROVIDER_TYPE = "kubernetes-registration-provider";
    public static final String SERVICE_REGISTRATION_PROVIDER_NAME = K8Constants.K8_REGISTRATION_SERVICE_NAME + " Provider";
    public static final String SERVICE_REGISTRATION_PROVIDER_DESCRIPTION = K8Constants.K8_REGISTRATION_SERVICE_DESCRIPTION;

    static
    {
        String PROP_FILE = "/resources/k8.properties";
        Properties props = new Properties();
        try
        {
            InputStream rs = K8ServiceAdapter.class.getResourceAsStream(PROP_FILE);
            if (rs != null)
            {
                props.load(rs);
            }
            else
            {
                logger.error("Resource not found " + PROP_FILE);
            }
        }
        catch (Exception ex)
        {
            logger.error("Failed to load " + PROP_FILE + ": " + ex);
        }
        ADAPTER_VERSION = props.getProperty("adapter.version", "1.0.0");
        ADAPTER_VENDOR = props.getProperty("adapter.vendor", "");
        SERVICE_PROVIDER_DESCRIPTION = SERVICE_PROVIDER_NAME + " (" + ADAPTER_VERSION + ")";
    }

    public K8ServiceAdapter() throws Exception
    {
        super(TimerReactor.getTimerReactor(SERVICE_PROVIDER_NAME));
        logger.info(SERVICE_PROVIDER_DESCRIPTION);

        registerMethod("log", new K8LogMethod(K8ConnectionFactory.getInstance(), this));
    }

    @Override
    public List<ServiceProviderType> getServiceProviderTypes()
    {
        List<ServiceProviderType> serviceProviderTypes = new ArrayList<ServiceProviderType>();
        ServiceProviderType serviceProviderType = new ServiceProviderType();
        serviceProviderType.setName(SERVICE_PROVIDER_NAME);
        serviceProviderType.setDescription(SERVICE_PROVIDER_DESCRIPTION);

        Link containerServiceType = new Link();
        containerServiceType.setName(K8Constants.CONTAINER_SERVICE_TYPE);
        serviceProviderType.getServiceTypes().add(containerServiceType);

        Link k8PodServiceType = new Link();
        k8PodServiceType.setName(K8Constants.K8_POD_SERVICE_TYPE);
        serviceProviderType.getServiceTypes().add(k8PodServiceType);

        Link k8ReplicationControllerType = new Link();
        k8ReplicationControllerType.setName(K8Constants.K8_REPLICATION_CONTROLLER_SERVICE_TYPE);
        serviceProviderType.getServiceTypes().add(k8ReplicationControllerType);

        Link k8ServiceType = new Link();
        k8ServiceType.setName(K8Constants.K8_SERVICE_SERVICE_TYPE);
        serviceProviderType.getServiceTypes().add(k8ServiceType);

        Link serviceProviderAssetType = new Link();
        serviceProviderAssetType.setName(SERVICE_PROVIDER_TYPE);
        serviceProviderAssetType.setType("application/" + AssetType.class.getName() + "+xml");
        serviceProviderType.setAssetType(serviceProviderAssetType);
        serviceProviderType.getOptions().add(ServiceProviderOption.NO_NETWORKS);
        serviceProviderTypes.add(serviceProviderType);

        ServiceProviderType registrationProviderType = new ServiceProviderType();
        registrationProviderType.setName(SERVICE_REGISTRATION_PROVIDER_NAME);
        registrationProviderType.setDescription(SERVICE_REGISTRATION_PROVIDER_DESCRIPTION);

        Link registrationProviderAssetType = new Link();
        registrationProviderAssetType.setName(SERVICE_REGISTRATION_PROVIDER_TYPE);
        registrationProviderAssetType.setType("application/" + AssetType.class.getName() + "+xml");
        registrationProviderType.setAssetType(registrationProviderAssetType);

        Link registrationServiceAssetType = new Link();
        registrationServiceAssetType.setName(K8Constants.K8_REGISTRATION_SERVICE_TYPE);
        registrationServiceAssetType.setType("application/" + AssetType.class.getName() + "+xml");
        registrationProviderType.getServiceTypes().add(registrationServiceAssetType);

        registrationProviderType.getOptions().add(ServiceProviderOption.NO_NETWORKS);
        registrationProviderType.getOptions().add(ServiceProviderOption.NO_HOSTNAME);
        registrationProviderType.getOptions().add(ServiceProviderOption.NO_PASSWORD);
        registrationProviderType.getOptions().add(ServiceProviderOption.NO_USERNAME);
        serviceProviderTypes.add(registrationProviderType);

        return serviceProviderTypes;
    }

    /**
     * Returns implementations of service provider message handlers.
     */

    @Override
    public IServiceProvider getServiceProviderOperations()
    {
        return new K8ServiceProviderOperations(K8ConnectionFactory.getInstance());
    }

    /**
     * Returns implementations of service provider message handlers.
     */

    @Override
    public IServiceInstance getServiceInstanceOperations()
    {
        return new K8ServiceInstanceOperations(K8ConnectionFactory.getInstance(), this);
    }

    @Override
    public IServiceInstanceLifecycle getServiceInstanceLifecycleOperations()
    {
        return new K8ServiceInstanceLifecycleOperations();
    }

    /**
     * Build up the set of asset types exposed by the adapter and return these in the registration request. If the adapter exposes
     * a service to an application via a blueprint, a sub-class of service is defined to expose this functionality and define
     * configuration parameters for the service. A sub-class of service provider exposes configuration parameters for the adapter
     * itself.
     */
    @Override
    public RegistrationRequest getRegistrationRequest()
    {
        logger.debug("getRegistrationRequest");
        RegistrationRequest registration = new RegistrationRequest();
        registration.setName(SERVICE_PROVIDER_NAME);
        registration.setVersion(ADAPTER_VERSION);

        // references to common types
        String X_PROPERTY_TYPE = "application/" + PropertyType.class.getName() + "+xml";
        Link string_type = new Link();
        string_type.setName("string-any");
        string_type.setType(X_PROPERTY_TYPE);

        Link integer_type = new Link();
        integer_type.setName("integer-any");
        integer_type.setType(X_PROPERTY_TYPE);

        Link encrypted_type = new Link();
        encrypted_type.setName("encrypted");
        encrypted_type.setType(X_PROPERTY_TYPE);

        String X_SERVICE_TYPE = "application/" + Service.class.getName() + "+xml";
        Link service = new Link();
        service.setName("service");
        service.setType(X_SERVICE_TYPE);

        Link lbaas = new Link();
        lbaas.setName("lbaas");
        lbaas.setType(X_SERVICE_TYPE);

        String X_SERVICE_PROVIDER_TYPE = "application/" + ServiceProviderType.class.getName() + "+xml";
        Link service_provider_type = new Link();
        service_provider_type.setName("serviceprovidertype");
        service_provider_type.setType(X_SERVICE_PROVIDER_TYPE);

        //
        // Docker Container
        //

        PropertyDefinition imagePD = new PropertyDefinition();
        imagePD.setName(K8Constants.CONTAINER_IMAGE);
        imagePD.setDisplayName("Image");
        imagePD.setDescription("Docker image name");
        imagePD.setReadable(true);
        imagePD.setWritable(true);
        imagePD.setMinRequired(1);
        imagePD.setMaxAllowed(1);
        imagePD.setPropertyType(string_type);

        PropertyDefinition commandPD = new PropertyDefinition();
        commandPD.setName(K8Constants.CONTAINER_COMMAND);
        commandPD.setDisplayName("Command");
        commandPD.setDescription("Entrypoint array; defaults to image's definition; cannot be updated");
        commandPD.setReadable(true);
        commandPD.setWritable(true);
        commandPD.setMaxAllowed(255);
        commandPD.setPropertyType(string_type);

        PropertyDefinition argPD = new PropertyDefinition();
        argPD.setName(K8Constants.CONTAINER_ARG);
        argPD.setDisplayName("Argument");
        argPD.setDescription("Argument array; defaults to image's definition; cannot be updated");
        argPD.setReadable(true);
        argPD.setWritable(true);
        argPD.setMaxAllowed(255);
        argPD.setPropertyType(string_type);

        PropertyDefinition envPD = new PropertyDefinition();
        envPD.setName(K8Constants.CONTAINER_ENV);
        envPD.setDisplayName("Environment");
        envPD.setDescription("List of environment variables to set in the container; cannot be updated");
        envPD.setReadable(true);
        envPD.setWritable(true);
        envPD.setMaxAllowed(255);
        envPD.setPropertyType(string_type);

        PropertyDefinition workingDirPD = new PropertyDefinition();
        workingDirPD.setName(K8Constants.CONTAINER_WORKING_DIR);
        workingDirPD.setDisplayName("Working Directory");
        workingDirPD.setDescription("Container’s working directory; defaults to image’s default; cannot be updated");
        workingDirPD.setReadable(true);
        workingDirPD.setWritable(true);
        workingDirPD.setMaxAllowed(1);
        workingDirPD.setPropertyType(string_type);

        PropertyDefinition portPD = new PropertyDefinition();
        portPD.setName(K8Constants.CONTAINER_PORT);
        portPD.setDisplayName("Exposed Port");
        portPD.setDescription("List of ports to expose from the container; cannot be updated");
        portPD.setReadable(true);
        portPD.setWritable(true);
        portPD.setMaxAllowed(255);
        portPD.setPropertyType(string_type);

        PropertyDefinition volumeMountPD = new PropertyDefinition();
        volumeMountPD.setName(K8Constants.CONTAINER_VOLUME_MOUNT);
        volumeMountPD.setDisplayName("Volume Mount");
        volumeMountPD.setDescription("List of environment variables to set in the container; cannot be updated");
        volumeMountPD.setReadable(true);
        volumeMountPD.setWritable(true);
        volumeMountPD.setMaxAllowed(255);
        volumeMountPD.setPropertyType(string_type);

        PropertyDefinition readinessProbePD = new PropertyDefinition();
        readinessProbePD.setName(K8Constants.CONTAINER_READINESS_PROBE);
        readinessProbePD.setDisplayName("Readiness Probe");
        readinessProbePD.setDescription("Probe to test availability of service on startup");
        readinessProbePD.setReadable(true);
        readinessProbePD.setWritable(true);
        readinessProbePD.setMaxAllowed(1);
        readinessProbePD.setPropertyType(string_type);

        PropertyDefinition livenessProbePD = new PropertyDefinition();
        livenessProbePD.setName(K8Constants.CONTAINER_LIVENESS_PROBE);
        livenessProbePD.setDisplayName("Liveness Probe");
        livenessProbePD.setDescription("Probe to test health of service");
        livenessProbePD.setReadable(true);
        livenessProbePD.setWritable(true);
        livenessProbePD.setMaxAllowed(1);
        livenessProbePD.setPropertyType(string_type);

        AssetType containerType = new AssetType();
        containerType.setName(K8Constants.CONTAINER_SERVICE_TYPE);
        containerType.setDisplayName(K8Constants.CONTAINER_SERVICE_NAME);
        containerType.setDescription(K8Constants.CONTAINER_SERVICE_DESCRIPTION);
        containerType.getPropertyDefinitions().add(imagePD);
        containerType.getPropertyDefinitions().add(commandPD);
        containerType.getPropertyDefinitions().add(argPD);
        containerType.getPropertyDefinitions().add(envPD);
        containerType.getPropertyDefinitions().add(workingDirPD);
        containerType.getPropertyDefinitions().add(portPD);
        containerType.getPropertyDefinitions().add(volumeMountPD);
        containerType.getPropertyDefinitions().add(readinessProbePD);
        containerType.getPropertyDefinitions().add(livenessProbePD);
        containerType.setSuperType(service);
        containerType.getEditors().add(Editor.VARIABLES);

        //
        // Kubernetes Pod
        //

        PropertyTypeValue always = new PropertyTypeValue();
        always.setName(K8Constants.K8_POD_RESTART_ALWAYS);
        always.setDisplayName(always.getName());
        always.setValue(always.getName());

        PropertyTypeValue onFailure = new PropertyTypeValue();
        onFailure.setName(K8Constants.K8_POD_RESTART_ONFAILURE);
        onFailure.setDisplayName(onFailure.getName());
        onFailure.setValue(onFailure.getName());

        PropertyTypeValue never = new PropertyTypeValue();
        never.setName(K8Constants.K8_POD_RESTART_NEVER);
        never.setDisplayName(never.getName());
        never.setValue(never.getName());

        PropertyType restartPolicyPT = new PropertyType();
        restartPolicyPT.setName("RestartPolicy");
        restartPolicyPT.setType(PrimitiveType.STRING);
        restartPolicyPT.setDisplayName("Restart Policy");
        restartPolicyPT.setValueConstraint(ValueConstraintType.LIST);
        restartPolicyPT.getRootValues().add(always);
        restartPolicyPT.getRootValues().add(onFailure);
        restartPolicyPT.getRootValues().add(never);

        Link restartPropertyTypeLink = new Link();
        restartPropertyTypeLink.setName(restartPolicyPT.getName());
        restartPropertyTypeLink.setType("application/" + PropertyType.class.getName() + "+xml");

        AssetProperty restartPolicyDefault = new AssetProperty();
        restartPolicyDefault.setName("Default");
        restartPolicyDefault.setStringValue("Always");

        PropertyDefinition restartPolicyPD = new PropertyDefinition();
        restartPolicyPD.setName(K8Constants.K8_POD_RESTART_POLICY);
        restartPolicyPD.setDisplayName("Restart Policy");
        restartPolicyPD.setDescription("Restart policy for all containers within the pod");
        restartPolicyPD.setReadable(true);
        restartPolicyPD.setWritable(true);
        restartPolicyPD.setMinRequired(1);
        restartPolicyPD.setMaxAllowed(1);
        restartPolicyPD.getDefaultValues().add(restartPolicyDefault);
        restartPolicyPD.setPropertyType(restartPropertyTypeLink);
        restartPolicyPD.setPropertyTypeValue(restartPolicyPT);

        PropertyDefinition labelPD = new PropertyDefinition();
        labelPD.setName(K8Constants.K8_POD_LABEL);
        labelPD.setDisplayName("Label");
        labelPD.setDescription(
                "Label(s) in the form <label>=<value> where values can be populated by Agility variables (my_label=${VAR1})");
        labelPD.setReadable(true);
        labelPD.setWritable(true);
        labelPD.setMaxAllowed(255);
        labelPD.setPropertyType(string_type);

        AssetProperty defaultReplicas = new AssetProperty();
        defaultReplicas.setName("Default");
        defaultReplicas.setIntValue(1);

        PropertyDefinition replicasPD = new PropertyDefinition();
        replicasPD.setName(K8Constants.K8_POD_REPLICAS);
        replicasPD.setDisplayName("Replicas");
        replicasPD.setDescription("If set a replication controller is created to maintain the desired number of replicas");
        replicasPD.setReadable(true);
        replicasPD.setWritable(true);
        replicasPD.setMaxAllowed(1);
        replicasPD.setMinRequired(1);
        replicasPD.getDefaultValues().add(defaultReplicas);
        replicasPD.setPropertyType(integer_type);

        AssetType k8PodType = new AssetType();
        k8PodType.setName(K8Constants.K8_POD_SERVICE_TYPE);
        k8PodType.setDisplayName(K8Constants.K8_POD_SERVICE_NAME);
        k8PodType.setDescription(K8Constants.K8_POD_SERVICE_DESCRIPTION);
        k8PodType.getPropertyDefinitions().add(restartPolicyPD);
        k8PodType.getPropertyDefinitions().add(labelPD);
        k8PodType.getPropertyDefinitions().add(replicasPD);
        k8PodType.setSuperType(service);

        //
        // Kubernetes Service
        //

        PropertyTypeValue clusterIP = new PropertyTypeValue();
        clusterIP.setName("ClusterIP");
        clusterIP.setDisplayName(clusterIP.getName());
        clusterIP.setValue(clusterIP.getName());

        PropertyTypeValue nodePort = new PropertyTypeValue();
        nodePort.setName("NodePort");
        nodePort.setDisplayName(nodePort.getName());
        nodePort.setValue(nodePort.getName());

        PropertyType serviceTypePT = new PropertyType();
        serviceTypePT.setName("ServiceType");
        serviceTypePT.setType(PrimitiveType.STRING);
        serviceTypePT.setDisplayName("Service Type");
        serviceTypePT.setValueConstraint(ValueConstraintType.LIST);
        serviceTypePT.getRootValues().add(clusterIP);
        serviceTypePT.getRootValues().add(nodePort);

        Link servicePropertyTypeLink = new Link();
        servicePropertyTypeLink.setName(serviceTypePT.getName());
        servicePropertyTypeLink.setType("application/" + PropertyType.class.getName() + "+xml");

        PropertyDefinition serviceTypePD = new PropertyDefinition();
        serviceTypePD.setName(K8Constants.K8_SERVICE_TYPE);
        serviceTypePD.setDisplayName("Type");
        serviceTypePD.setDescription("Type of exposed service");
        serviceTypePD.setReadable(true);
        serviceTypePD.setWritable(true);
        serviceTypePD.setMaxAllowed(1);
        serviceTypePD.setPropertyType(servicePropertyTypeLink);
        serviceTypePD.setPropertyTypeValue(serviceTypePT);

        PropertyDefinition externalPD = new PropertyDefinition();
        externalPD.setName(K8Constants.K8_SERVICE_EXTERNAL_IP);
        externalPD.setDisplayName("External IP");
        externalPD.setDescription(
                "List of IP addresses for which nodes in the cluster will also accept traffic for this service. These IPs are not managed by Kubernetes. The user is responsible for ensuring that traffic arrives at a node with this IP.");
        externalPD.setReadable(true);
        externalPD.setWritable(true);
        externalPD.setMaxAllowed(16);
        externalPD.setPropertyType(string_type);

        PropertyDefinition selectorPD = new PropertyDefinition();
        selectorPD.setName(K8Constants.K8_SERVICE_SELECTOR);
        selectorPD.setDisplayName("Selector");
        selectorPD.setDescription("Specific label values (<label>=<value>) used to constrain service membership");
        selectorPD.setReadable(true);
        selectorPD.setWritable(true);
        selectorPD.setMaxAllowed(255);
        selectorPD.setPropertyType(string_type);

        AssetType k8ServiceType = new AssetType();
        k8ServiceType.setName(K8Constants.K8_SERVICE_SERVICE_TYPE);
        k8ServiceType.setDisplayName(K8Constants.K8_SERVICE_SERVICE_NAME);
        k8ServiceType.setDescription(K8Constants.K8_SERVICE_SERVICE_DESCRIPTION);
        k8ServiceType.getPropertyDefinitions().add(serviceTypePD);
        k8ServiceType.getPropertyDefinitions().add(externalPD);
        k8ServiceType.getPropertyDefinitions().add(selectorPD);
        k8ServiceType.setSuperType(lbaas);

        //
        // Kubernetes Provider - Registers the service as a provider
        //

        AssetType k8RegistrationType = new AssetType();
        k8RegistrationType.setName(K8Constants.K8_REGISTRATION_SERVICE_TYPE);
        k8RegistrationType.setDisplayName(K8Constants.K8_REGISTRATION_SERVICE_NAME);
        k8RegistrationType.setDescription(K8Constants.K8_REGISTRATION_SERVICE_DESCRIPTION);
        k8RegistrationType.setSuperType(service);

        //
        // Connections
        //

        String X_CONNECTION_TYPE = "application/" + Connection.class.getName() + "+xml";
        Link connection_link = new Link();
        connection_link.setName("designconnection");
        connection_link.setType(X_CONNECTION_TYPE);

        //
        // Container->Workload Connection Type
        //

        PropertyDefinition linkPD = new PropertyDefinition();
        linkPD.setName("link");
        linkPD.setDisplayName("Link");
        linkPD.setDescription("Name used to publish this link in container, defaults to workload name");
        linkPD.setReadable(true);
        linkPD.setWritable(true);
        linkPD.setMaxAllowed(1);
        linkPD.setPropertyType(string_type);

        PropertyDefinition publishPD = new PropertyDefinition();
        publishPD.setName("publish");
        publishPD.setDisplayName("Publish");
        publishPD.setDescription("Workload port to publish in container");
        publishPD.setReadable(true);
        publishPD.setWritable(true);
        publishPD.setMaxAllowed(255);
        publishPD.setPropertyType(integer_type);

        AssetType k8ConnectionType = new AssetType();
        k8ConnectionType.setName(K8Constants.K8_CONNECTION_TYPE);
        k8ConnectionType.setDisplayName(K8Constants.K8_CONNECTION_NAME);
        k8ConnectionType.setDescription(K8Constants.K8_CONNECTION_DESCRIPTION);
        k8ConnectionType.setSuperType(connection_link);
        k8ConnectionType.getPropertyDefinitions().add(linkPD);
        k8ConnectionType.getPropertyDefinitions().add(publishPD);

        Link k8ConnectionTypeLink = new Link();
        k8ConnectionTypeLink.setName(K8Constants.K8_CONNECTION_TYPE);
        k8ConnectionTypeLink.setType(X_CONNECTION_TYPE);

        Link workloadTypeLink = new Link();
        workloadTypeLink.setName("designworkload");
        workloadTypeLink.setType("application/" + Workload.class.getName() + "+xml");

        Link containerTypeLink = new Link();
        containerTypeLink.setName(K8Constants.CONTAINER_SERVICE_TYPE);
        containerTypeLink.setType(X_CONNECTION_TYPE);

        Link podTypeLink = new Link();
        podTypeLink.setName(K8Constants.K8_POD_SERVICE_TYPE);
        podTypeLink.setType(X_SERVICE_TYPE);

        Link serviceTypeLink = new Link();
        serviceTypeLink.setName(K8Constants.K8_SERVICE_SERVICE_TYPE);
        serviceTypeLink.setType(X_SERVICE_TYPE);

        // registration to workload connection
        ConnectionDefinition registrationToWorkloadConnection = new ConnectionDefinition();
        registrationToWorkloadConnection.setName("k8-registration");
        registrationToWorkloadConnection.setDisplayName("Kubernetes API endpoint");
        registrationToWorkloadConnection.setDescription("Register workload as Kubernetes service provider");
        registrationToWorkloadConnection.setConnectionType(connection_link);
        registrationToWorkloadConnection.setDestinationType(workloadTypeLink);
        k8RegistrationType.getSrcConnections().add(registrationToWorkloadConnection);

        // container to pod connection
        ConnectionDefinition containerToPodConnection = new ConnectionDefinition();
        containerToPodConnection.setName("k8-container-to-pod");
        containerToPodConnection.setDisplayName("Container to Pod");
        containerToPodConnection.setDescription("Pod membership");
        containerToPodConnection.setConnectionType(connection_link);
        containerToPodConnection.setSourceType(containerTypeLink);
        k8PodType.getDestConnections().add(containerToPodConnection);

        // pod to service connection
        ConnectionDefinition serviceToPod = new ConnectionDefinition();
        serviceToPod.setName("k8-service-to-pod");
        serviceToPod.setDisplayName("Service to Pod");
        serviceToPod.setDescription("Service membership");
        serviceToPod.setConnectionType(connection_link);
        serviceToPod.setDestinationType(podTypeLink);
        k8ServiceType.getSrcConnections().add(serviceToPod);

        // pod dependencies
        ConnectionDefinition podToService = new ConnectionDefinition();
        podToService.setName("k8-pod-to-service");
        podToService.setDisplayName("Pod to Service");
        podToService.setDescription("Application (pod) dependency on connected service");
        podToService.setConnectionType(k8ConnectionTypeLink);
        podToService.setSourceType(podTypeLink);
        k8ServiceType.getDestConnections().add(podToService);

        // pod to workload connection
        ConnectionDefinition podToWorkloadConnection = new ConnectionDefinition();
        podToWorkloadConnection.setName("k8-pod-to-workload");
        podToWorkloadConnection.setDisplayName("Pod to Workload");
        podToWorkloadConnection.setDescription("Application (pod) dependency on connected workload");
        podToWorkloadConnection.setConnectionType(k8ConnectionTypeLink);
        podToWorkloadConnection.setDestinationType(workloadTypeLink);
        k8PodType.getSrcConnections().add(podToWorkloadConnection);

        //
        // Kubernetes Service Provider
        //

        AssetType k8ServiceProviderType = new AssetType();
        k8ServiceProviderType.setName(SERVICE_PROVIDER_TYPE);
        k8ServiceProviderType.setDisplayName(SERVICE_PROVIDER_NAME);
        k8ServiceProviderType.setSuperType(service_provider_type);

        //
        // Kubernetes Registration Provider Type
        //

        AssetType k8RegistrationProviderType = new AssetType();
        k8RegistrationProviderType.setName(SERVICE_REGISTRATION_PROVIDER_TYPE);
        k8RegistrationProviderType.setDisplayName(SERVICE_REGISTRATION_PROVIDER_NAME);
        k8RegistrationProviderType.setSuperType(service_provider_type);

        //
        // Registration response
        //

        registration.getAssetTypes().add(k8ConnectionType);
        registration.getAssetTypes().add(containerType);
        registration.getAssetTypes().add(k8PodType);
        registration.getAssetTypes().add(k8RegistrationType);
        registration.getAssetTypes().add(k8RegistrationProviderType);
        registration.getAssetTypes().add(k8ServiceType);
        registration.getAssetTypes().add(k8ServiceProviderType);
        registration.getServiceProviderTypes().addAll(getServiceProviderTypes());
        return registration;
    }

    @Override
    public void onRegistration(RegistrationResponse response)
    {
    }
}
