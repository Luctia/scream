package growl;

import growl.domain.Configuration;
import growl.domain.Image;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import resourcemanager.domain.ResourceLimits;
import java.util.ArrayList;
import java.util.List;

public class KubernetesMaker {
    public static List<Deployment> generateDeployments(Configuration configuration) {
        ResourceRequirements defaultLimits = new ResourceLimits(new Quantity("500m"), new Quantity("1M")).toResourceRequirements();
        return configuration.images().stream().map(i -> generateDeployment(i, defaultLimits)).toList();
    }

    public static Deployment generateDeployment(Image image, ResourceRequirements resourceRequirements) {
        EnvVar[] environmentVariables = new EnvVar[image.env().size()];
        List<String> envNames = image.env().keySet().stream().toList();
        for (int i = 0; i < image.env().size(); i++) {
            environmentVariables[i] = new EnvVarBuilder()
                    .withName(envNames.get(i))
                    .withValue(image.env().get(envNames.get(i)))
                    .build();
        }
        // @formatter:off
        return new DeploymentBuilder()
                .withNewMetadata()
                    .withName(image.containerId())
                    .addToLabels("scream.service", image.containerId())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(image.minNumInstances())
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("scream.service", image.containerId())
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                            .withName(image.containerId())
                            .addToEnv(environmentVariables)
                            .withImage(image.imageId())
                            .withImagePullPolicy("Never")
                            .withResources(resourceRequirements)
                            .addNewPort()
                                .withProtocol("TCP")
                                .withContainerPort(image.port())
                            .endPort()
                        .endContainer()
                    .endSpec()
                .endTemplate()
                .withNewSelector()
                    .addToMatchLabels("scream.service", image.containerId())
                .endSelector()
                .endSpec()
                .build();
        // @formatter:on
    }

    public static List<Service> generateServices(Configuration configuration) {
        List<Service> services = new ArrayList<>();
        for (Image image : configuration.images()) {
            // @formatter:off
            services.add(
                    new ServiceBuilder()
                            .withNewMetadata()
                                .withName(image.containerId())
                                .addToLabels("scream.service", image.containerId())
                            .endMetadata()
                            .withNewSpec()
                                .addNewPort()
                                    .withName("80")
                                    .withTargetPort(new IntOrString(80))
                                    .withPort(80)
                                .endPort()
                                .addToSelector("scream.service", image.containerId())
                            .endSpec()
                            .build()
            );
            // @formatter:on
        }
        return services;
    }
}
