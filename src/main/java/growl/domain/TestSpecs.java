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
                             %s
                             <GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="Ordered test"/>
                             <hashTree>
                             %s
                             </hashTree>
                             </hashTree>
                             """, healthCheckUrl == null ? "" : healthCheckConfig(), samplerString);
        } else {
            return String.format("""
                             <hashTree>
                             %s
                             %s
                             </hashTree>
                             """, healthCheckUrl == null ? "" : healthCheckConfig(), samplerString);
        }
    }

    private String healthCheckConfig() {
        String base = """
                <WhileController guiclass="WhileControllerGui" testclass="WhileController" testname="Wait for health check">
                <stringProp name="WhileController.condition">${__jexl3(&quot;${responseCode}&quot; != 200,)}</stringProp>
                </WhileController>
                <hashTree>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Health check">
                <stringProp name="HTTPSampler.domain">%s</stringProp>
                <stringProp name="HTTPSampler.protocol">%s</stringProp>
                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                <stringProp name="HTTPSampler.method">GET</stringProp>
                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
                <collectionProp name="Arguments.arguments"/>
                </elementProp>
                </HTTPSamplerProxy>
                <hashTree>
                <RegexExtractor guiclass="RegexExtractorGui" testclass="RegexExtractor" testname="Regular Expression Extractor">
                <stringProp name="RegexExtractor.useHeaders">code</stringProp>
                <stringProp name="RegexExtractor.refname">responseCode</stringProp>
                <stringProp name="RegexExtractor.regex">(.*)</stringProp>
                <stringProp name="RegexExtractor.template">$1$</stringProp>
                <stringProp name="RegexExtractor.default"></stringProp>
                <boolProp name="RegexExtractor.default_empty_value">false</boolProp>
                <stringProp name="RegexExtractor.match_number"></stringProp>
                </RegexExtractor>
                <hashTree/>
                </hashTree>
                <ConstantTimer guiclass="ConstantTimerGui" testclass="ConstantTimer" testname="Constant Timer">
                <stringProp name="ConstantTimer.delay">1000</stringProp>
                </ConstantTimer>
                <hashTree/>
                </hashTree>
                """;
        // TODO determine protocol
        return String.format(base, healthCheckUrl, "http");
    }
}
