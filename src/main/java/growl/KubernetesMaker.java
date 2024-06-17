package growl;

import growl.domain.Configuration;
import growl.domain.Image;

import java.io.IOException;
import java.io.PrintWriter;

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
            writer = new PrintWriter("kubernetes/jmeter-deployment.yaml");
            writer.print(JMETER_DEPLOYMENT_FILE);
            writer.close();

            writer = new PrintWriter("kubernetes/jmeter-service.yaml");
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
}
