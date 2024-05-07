package cpim.deployers;

/**
 * Interface for all the functionality needed to deploy images to cloud providers using Kubernetes.
 */
public interface CloudDeployer {
    void deployImages();
}
