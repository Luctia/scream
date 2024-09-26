package resourcemanager;

import growl.KubernetesMaker;
import growl.domain.Configuration;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.NonDeletingOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import resourcemanager.domain.ResourceLimits;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResourceManager {
    public static void setNewResourceLimits(ResourceLimits resourceLimits, String serviceName, Configuration config) {
        try (KubernetesClient k8s = new KubernetesClientBuilder().build()) {
            try {
                ResourceLimits oldLimits = getResourceLimits(serviceName, config);
                Deployment replacementWithNewLimits = KubernetesMaker.generateDeployment(config.images().stream().filter(i -> i.containerId().equals(serviceName)).findFirst().orElseThrow(), resourceLimits.toResourceRequirements());
                System.out.printf("Resources to be set at %s%n", resourceLimits);
                k8s.apps().deployments().inNamespace(config.namespace()).resource(replacementWithNewLimits).forceConflicts().serverSideApply();
                System.out.println("Actual resources:");
                for (int i = 0; i < 20; i++) {
                    TimeUnit.SECONDS.sleep(5);
                    if (!getResourceLimits(serviceName, config).equals(oldLimits)) {
                        break;
                    }
                }
                System.out.println(getResourceLimits(serviceName, config));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static ResourceLimits getResourceLimits(String podName, Configuration config) throws Exception {
        try (KubernetesClient k8s = new KubernetesClientBuilder().build()) {
            NonNamespaceOperation<Pod, PodList, PodResource> pods = k8s.pods().inNamespace(config.namespace());
            List<Pod> candidates = new ArrayList<>();
            while (candidates.isEmpty()) {
                candidates = pods.list().getItems().stream().filter(p -> p.getMetadata().getName().contains(podName)).toList();
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
                k8s.apps().deployments().inNamespace(configuration.namespace()).resource(deployment).createOr(NonDeletingOperation::patch);
            }
            for (Service service : services) {
                k8s.services().inNamespace(configuration.namespace()).resource(service).createOr(NonDeletingOperation::patch);
            }
        }
    }
}
