package growl.domain;

import java.util.List;
import java.util.Objects;

/**
 * Record for representing global test specifications.
 *
 * @param healthCheckUrl URL that, when requested, should return 200 OK before commencing with tests/benchmarks. Not
 *                       required
 * @param ordered        whether to execute all tests in the sequence they were given. Defaults to false
 * @param samplers       list of samplers. Required and should not be empty
 */
public record TestSpecs(String healthCheckUrl, boolean ordered, List<Sampler> samplers) {
    public TestSpecs {
        Objects.requireNonNull(samplers, "samplers cannot be null");
        if (samplers.isEmpty()) throw new IllegalArgumentException("samplers may not be empty");
    }

    public String toXML(PerformanceDemands performanceDemands) {
        StringBuilder samplerString = new StringBuilder();
        for (Sampler sampler : samplers) {
            samplerString.append(sampler.toXML((int) ((performanceDemands.throughput() * sampler.percentage() / 100) / 60), performanceDemands.latency()));
            samplerString.append("<hashTree/>\n</hashTree>\n");
        }
        return String.format("""
                <hashTree>
                %s
                %s
                </hashTree>
                """, healthCheckUrl == null ? "" : healthCheckConfig(), samplerString);
    }

    private String healthCheckConfig() {
        String base = """
                <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Health check thread group">
                <intProp name="ThreadGroup.num_threads">1</intProp>
                <intProp name="ThreadGroup.ramp_time">1</intProp>
                <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller">
                <stringProp name="LoopController.loops">1</stringProp>
                <boolProp name="LoopController.continue_forever">false</boolProp>
                </elementProp>
                </ThreadGroup>
                <hashTree>
                <WhileController guiclass="WhileControllerGui" testclass="WhileController" testname="Wait for health check to succeed" enabled="true">
                <stringProp name="WhileController.condition">${__jexl3(&quot;${responseCode}&quot; != 200,)}</stringProp>
                </WhileController>
                <hashTree>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Health check" enabled="true">
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
                <RegexExtractor guiclass="RegexExtractorGui" testclass="RegexExtractor" testname="Regular Expression Extractor" enabled="true">
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
                <ConstantTimer guiclass="ConstantTimerGui" testclass="ConstantTimer" testname="Constant Timer" enabled="true">
                <stringProp name="ConstantTimer.delay">1000</stringProp>
                </ConstantTimer>
                <hashTree/>
                </hashTree>
                <hashTree/>
                </hashTree>
                """;
        // TODO determine protocol
        return String.format(base, healthCheckUrl, "http");
    }
}
