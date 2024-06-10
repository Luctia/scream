package growl.domain;

import java.util.Map;
import java.util.Objects;

/**
 * Record for representing Docker images in the context of the framework.
 * @param imageId the identification of the image. Required and unique
 * @param containerId the ID of the image as it can be found in the Docker Repository. Required
 * @param port the port number that will be used for benchmarking. Defaults to 80
 * @param isTestEndpoint whether the image will expose the public endpoints for benchmarking. Defaults to true
 * @param minNumInstances the minimum amount of instances of this image required before starting benchmarking.
 *                        Defaults to 1
 * @param env a map of environment variables that will be set inside this container. Not required
 */
public record Image(String imageId, String containerId, int port, boolean isTestEndpoint, int minNumInstances,
                    Map<String, String> env) {
    public Image {
        Objects.requireNonNull(imageId, "imageId cannot be null");
        Objects.requireNonNull(containerId, "containerId cannot be null");
        if (port < 1 || port > 65535) {throw new IllegalArgumentException("Port number must be between 1 and 65535");}
        if (minNumInstances < 1) {throw new IllegalArgumentException("Number of instances must be more than 0");}
    }
}
