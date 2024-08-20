package resourcemanager;

import growl.KubernetesMaker;
import growl.domain.Configuration;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import resourcemanager.domain.ResourceLimits;

import java.util.List;

public class ResourceManager {
    public static void setNewResourceLimits(ResourceLimits resourceLimits, String serviceName, Configuration config) {
        try (KubernetesClient k8s = new KubernetesClientBuilder().build()) {
            k8s.pods().inNamespace(config.namespace()).list().getItems().stream()
                    .filter(pod -> pod.getMetadata().getName().contains(serviceName))
                    .map(Pod::getSpec)
                    .map(PodSpec::getContainers).flatMap(List::stream)
                    .forEach(container -> container.setResources(resourceLimits.toResourceRequirements()));
        }
    }

    public static ResourceLimits getResourceLimits(String podName, Configuration config) throws Exception {
        try (KubernetesClient k8s = new KubernetesClientBuilder().build()) {
            NonNamespaceOperation<Pod, PodList, PodResource> pods = k8s.pods().inNamespace(config.namespace());
            String finalPodName = podName;
            List<Pod> candidates = pods.list().getItems().stream().filter(p -> p.getMetadata().getName().contains(finalPodName)).toList();
            if (candidates.isEmpty()) {
                throw new Exception("No viable candidates found");
            }
            if (candidates.size() > 1) {
                throw new Exception("More than one viable candidate found");
            }
            return new ResourceLimits(
                    candidates.getFirst().getSpec().getContainers().getFirst().getResources()
                            .getLimits().get("cpu"),
                    candidates.getFirst().getSpec().getContainers().getFirst().getResources()
                            .getLimits().get("memory")
            );
        }
    }

    public static void deployKubernetes(Configuration configuration) {
        List<Deployment> deployments = KubernetesMaker.generateDeployments(configuration);
        List<Service> services = KubernetesMaker.generateServices(configuration);
        try (KubernetesClient k8s = new KubernetesClientBuilder().build()) {
            for (Deployment deployment : deployments) {
                k8s.apps().deployments().inNamespace(configuration.namespace()).resource(deployment).create();
            }
            for (Service service : services) {
                k8s.services().inNamespace(configuration.namespace()).resource(service).create();
            }
        }
    }
}
