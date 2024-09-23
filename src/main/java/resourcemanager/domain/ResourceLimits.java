package resourcemanager.domain;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;

import java.util.Objects;

public record ResourceLimits(Quantity cpu, Quantity memory) {
    public ResourceRequirements toResourceRequirements() {
        return new ResourceRequirementsBuilder()
                .addToLimits("cpu", cpu)
                .addToLimits("memory", memory)
                .build();
    }

    public ResourceLimits copy() {
        return new ResourceLimits(cpu, memory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceLimits limits = (ResourceLimits) o;
        return cpu.equals(limits.cpu) && memory.equals(limits.memory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpu, memory);
    }
}
