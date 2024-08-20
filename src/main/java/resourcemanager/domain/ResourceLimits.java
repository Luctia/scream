package resourcemanager.domain;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;

public record ResourceLimits(Quantity cpu, Quantity memory) {
    public ResourceRequirements toResourceRequirements() {
        return new ResourceRequirementsBuilder()
                .addToLimits("cpu", cpu)
                .addToLimits("memory", memory)
                .build();
    }
}
