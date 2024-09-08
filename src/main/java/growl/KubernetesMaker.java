package growl;

import growl.domain.Configuration;
import growl.domain.Image;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import resourcemanager.domain.ResourceLimits;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KubernetesMaker {
    private static final String JMETER_DEPLOYMENT_FILE = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              labels:
                scream.service: jmeter
              name: jmeter
            spec:
              replicas: 1
              selector:
                matchLabels:
                  scream.service: jmeter
              template:
                metadata:
                  labels:
                    scream.service: jmeter
                spec:
                  containers:
                    - image: jmetertest
                      name: jmetertest
                      imagePullPolicy: Never
                      command: [ "./bin/jmeter", "-n", "-t", "config.jmx" ]
                      ports:
                        - containerPort: 80
                          protocol: TCP
            """;

    private static final String DEPLOYMENT_TEMPLATE = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              labels:
                scream.service: %s
              name: %s
            spec:
              replicas: 1
              selector:
                matchLabels:
                  scream.service: %s
              template:
                metadata:
                  labels:
                    scream.service: %s
                spec:
                  containers:
                    - image: %s
                      name: %s
                      imagePullPolicy: Never
                      ports:
                        - containerPort: 80
                          protocol: TCP
                  restartPolicy: Always
            """;

    private static final String SERVICE_TEMPLATE = """
            apiVersion: v1
            kind: Service
            metadata:
              labels:
                scream.service: %s
              name: %s
            spec:
              ports:
                - name: "80"
                  port: 80
                  targetPort: 80
              selector:
                scream.service: %s
            """;

    public static void generateK8sConfig(Configuration configuration) {
        PrintWriter writer;
        try {
            writer = new PrintWriter("kubernetes/scream-deployment.yaml");
            writer.print(JMETER_DEPLOYMENT_FILE);
            writer.close();

            writer = new PrintWriter("kubernetes/scream-service.yaml");
            writer.print(String.format(SERVICE_TEMPLATE, "jmeter", "jmeter", "jmeter"));
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not create k8s configuration files for JMeter");
        }
        for (Image image : configuration.images()) {
            try {
                // Deployment
                writer = new PrintWriter("kubernetes/" + image.containerId() + "-deployment.yaml");
                writer.print(String.format(DEPLOYMENT_TEMPLATE, image.containerId(), image.containerId(), image.containerId(), image.containerId(), image.imageId(), image.containerId()));
                writer.close();

                // Service
                writer = new PrintWriter("kubernetes/" + image.containerId() + "-service.yaml");
                writer.print(String.format(SERVICE_TEMPLATE, image.containerId(), image.containerId(), image.containerId()));
                writer.close();
            } catch (IOException e) {
                System.err.println("Could not create config.jmx file");
            }
        }
    }

    public static List<Deployment> generateDeployments(Configuration configuration) {
        ResourceRequirements defaultLimits = new ResourceLimits(new Quantity("500m"), new Quantity("500M")).toResourceRequirements();
        return configuration.images().stream().map(i -> generateDeployment(i, defaultLimits)).toList();
    }

    public static Deployment generateDeployment(Image image, ResourceRequirements resourceRequirements) {
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
                            .addToEnv((EnvVar[]) image.env().keySet().stream().map(k -> new EnvVarBuilder().withName(k).withValue(image.env().get(k)).build()).toArray())
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

    public static Deployment generateJMeterDeployment() {
        // @formatter:off
        return new DeploymentBuilder()
                .withNewMetadata()
                    .withName("jmeter")
                    .addToLabels("scream.service", "jmeter")
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("scream.service", "jmeter")
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                            .withName("jmeter")
                            .withImage("jmetertest")
                            .withImagePullPolicy("Never")
                            .addNewPort()
                                .withProtocol("TCP")
                                .withContainerPort(80)
                            .endPort()
                            .withCommand("./bin/jmeter", "-n", "-t", "config.jmx")
                        .endContainer()
                    .endSpec()
                .endTemplate()
                .withNewSelector()
                    .addToMatchLabels("scream.service", "jmeter")
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
