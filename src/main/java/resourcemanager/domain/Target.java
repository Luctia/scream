package resourcemanager.domain;

import growl.domain.Configuration;
import growl.domain.Sampler;
import io.fabric8.kubernetes.api.model.Quantity;
import resourcemanager.ResourceManager;

import java.math.BigDecimal;
import java.util.List;

public class Target {
    private String targetId;
    private List<Sampler> samplers;
    private final Configuration configuration;
    private ResourceLimits resourceLimits;
    private ResourceLimits minimumResourceLimits;
    private int currentlyChecking;

    private enum TargetPhase {INCREASING_BOTH_FAST, DECREASING_MEMORY_FAST, INCREASING_MEMORY_SLOW, DECREASING_CPU_FAST, INCREASING_CPU_SLOW, DONE}

    private TargetPhase phase = TargetPhase.INCREASING_BOTH_FAST;

    public Target(String targetId, List<growl.domain.Sampler> samplers, Configuration configuration) {
        this.targetId = targetId;
        this.samplers = samplers;
        this.configuration = configuration;
        this.resourceLimits = new ResourceLimits(new Quantity("100m"), new Quantity("100M"));
        this.minimumResourceLimits = new ResourceLimits(new Quantity("10m"), new Quantity("6M"));
        this.currentlyChecking = 0;
    }

    public ResourceLimits getResourceLimits() {
        return resourceLimits;
    }

    public void setResourceLimits(ResourceLimits resourceLimits) {
        this.resourceLimits = resourceLimits;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<Sampler> getSamplers() {
        return samplers;
    }

    public void setSamplers(List<Sampler> samplers) {
        this.samplers = samplers;
    }

    public boolean done() {
        return currentlyChecking > samplers.size() - 1;
    }

    public Sampler getCurrentSampler() {
        return samplers.get(currentlyChecking);
    }

    public void processTestResults(boolean passed) {
        switch (phase) {
            case INCREASING_BOTH_FAST:
                System.out.println("Increasing both fast");
                if (passed) {
                    phase = TargetPhase.DECREASING_MEMORY_FAST;
                    this.processTestResults(passed);
                } else
                    increaseBoth();
                break;
            case DECREASING_MEMORY_FAST:
                System.out.println("Decreasing mem fast");
                if (passed) {
                    if (multiplyMemory(0.6)) {
                        System.out.println("Minimum mem reached");
                        phase = TargetPhase.INCREASING_MEMORY_SLOW;
                        this.processTestResults(passed);
                    }
                } else {
                    phase = TargetPhase.INCREASING_MEMORY_SLOW;
                    this.processTestResults(passed);
                }
                break;
            case INCREASING_MEMORY_SLOW:
                System.out.println("Increasing mem slow");
                if (passed) {
                    phase = TargetPhase.DECREASING_CPU_FAST;
                    this.processTestResults(passed);
                }
                else
                    multiplyMemory(1.1);
                break;
            case DECREASING_CPU_FAST:
                System.out.println("Decreasing CPU fast");
                if (passed) {
                    if (multiplyCPU(0.6)) {
                        System.out.println("Minimum CPU reached");
                        phase = TargetPhase.INCREASING_CPU_SLOW;
                        this.processTestResults(passed);
                    }
                } else {
                    System.out.println("Did not pass, going slow");
                    phase = TargetPhase.INCREASING_CPU_SLOW;
                    this.processTestResults(passed);
                }
                break;
            case INCREASING_CPU_SLOW:
                System.out.println("Increasing CPU slow");
                if (passed) {
                    finishSampler();
                } else
                    multiplyCPU(1.1);
                break;
        }
    }

    public void applyResourceLimits() {
        ResourceManager.setNewResourceLimits(this.resourceLimits, this.targetId, configuration);
    }

    private void finishSampler() {
        currentlyChecking++;
        minimumResourceLimits = resourceLimits.copy();
        if (done()) {
            phase = TargetPhase.DONE;
        } else {
            phase = TargetPhase.INCREASING_BOTH_FAST;
        }
    }


    /**
     * Adjusts CPU by some rate.
     * @param rate
     * @return whether the minimum resource limits have been reached by this multiplication
     */
    private boolean multiplyCPU(double rate) {
        Quantity toCheck = multiplyQuantity(this.resourceLimits.cpu(), rate);
        if (toCheck.compareTo(minimumResourceLimits.cpu()) >= 0) {
            this.resourceLimits = new ResourceLimits(
                    multiplyQuantity(this.resourceLimits.cpu(), rate),
                    this.resourceLimits.memory()
            );
            return false;
        } else {
            this.resourceLimits = new ResourceLimits(
                    minimumResourceLimits.cpu(),
                    this.resourceLimits.memory()
            );
            return true;
        }
    }

    /**
     * Adjusts memory by some rate.
     * @param rate
     * @return whether the minimum resource limits have been reached by this multiplication
     */
    private boolean multiplyMemory(double rate) {
        Quantity toCheck = multiplyQuantity(this.resourceLimits.memory(), rate);
        if (toCheck.compareTo(minimumResourceLimits.memory()) > 0) {
            this.resourceLimits = new ResourceLimits(
                    this.resourceLimits.cpu(),
                    multiplyQuantity(this.resourceLimits.memory(), rate)
            );
            return false;
        } else {
            this.resourceLimits = new ResourceLimits(
                    this.resourceLimits.cpu(),
                    minimumResourceLimits.memory()
            );
            return true;
        }
    }

    private void increaseBoth() {
        this.resourceLimits = new ResourceLimits(
                multiplyQuantity(this.resourceLimits.cpu(), 1.5),
                multiplyQuantity(this.resourceLimits.memory(), 1.5)
        );
    }

    private static Quantity multiplyQuantity(Quantity quantity, double multiplier) {
        Quantity newQuantity = new Quantity();
        newQuantity.setAmount(new BigDecimal(quantity.getAmount()).multiply(new BigDecimal(multiplier)).toString());
        newQuantity.setFormat(quantity.getFormat());
        return newQuantity;
    }
}
