package growl.domain;

import java.util.List;

public record TestSpecs(String healthCheckUrl, boolean ordered, List<Sampler> samplers) {
    public String toXML() {
        StringBuilder samplerString = new StringBuilder();
        for (Sampler sampler : samplers) {
            samplerString.append(sampler.toXML());
            samplerString.append("<hashTree/>\n");
        }
        if (ordered) {
            return String.format("""
                             <hashTree>
                             <GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="Ordered test"/>
                             <hashTree>
                             %s
                             </hashTree>
                             </hashTree>
                             """, samplerString);
        } else {
            return String.format("""
                             <hashTree>
                             %s
                             </hashTree>
                             """, samplerString);
        }
    }
}
