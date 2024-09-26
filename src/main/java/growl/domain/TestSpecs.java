package growl.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Record for representing global test specifications.
 *
 * @param healthCheckUrl URL that, when requested, should return 200 OK before commencing with tests/benchmarks. Not
 *                       required
 * @param ordered        whether to execute all tests in the sequence they were given. Defaults to false
 * @param samplers       list of samplers. Required and should not be empty
 */
public record TestSpecs(String healthCheckUrl, String healthCheckTarget, boolean healthCheckHttps, boolean ordered, List<Sampler> samplers) {
    public TestSpecs {
        Objects.requireNonNull(samplers, "samplers cannot be null");
        if (samplers.isEmpty()) throw new IllegalArgumentException("samplers may not be empty");

    }

    public String toXML(PerformanceDemands performanceDemands, Map<String, Integer> ports) {
        StringBuilder samplerString = new StringBuilder();
        for (int i = 0; i < samplers().size(); i++) {
            Sampler sampler = samplers().get(i);
            samplerString.append(sampler.toXML((int) ((performanceDemands.throughput() * sampler.percentage() / 100) / 60), performanceDemands.latency(), i, ports.get(sampler.targetId())));
            samplerString.append("<hashTree/>\n</hashTree>\n");
        }
        return String.format("""
                <hashTree>
                %s
                %s
                <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group">
                <intProp name="ThreadGroup.num_threads">1</intProp>
                <intProp name="ThreadGroup.ramp_time">1</intProp>
                <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp elementType="LoopController" guiclass="LoopControlPanel" name="ThreadGroup.main_controller" testclass="LoopController" testname="Loop Controller">
                <stringProp name="LoopController.loops">1</stringProp>
                <boolProp name="LoopController.continue_forever">false</boolProp>
                </elementProp>
                </ThreadGroup>
                <hashTree>
                <kg.apc.jmeter.samplers.DummySampler guiclass="kg.apc.jmeter.samplers.DummySamplerGui" testclass="kg.apc.jmeter.samplers.DummySampler" testname="jp@gc - Dummy Sampler">
                <boolProp name="WAITING">true</boolProp>
                <boolProp name="SUCCESFULL">true</boolProp>
                <stringProp name="RESPONSE_CODE">200</stringProp>
                <stringProp name="RESPONSE_MESSAGE">OK</stringProp>
                <stringProp name="REQUEST_DATA">Dummy Sampler used to simulate requests and responses without actual network activity. This helps debugging tests.</stringProp>
                <stringProp name="RESPONSE_DATA">Dummy Sampler used to simulate requests and responses without actual network activity. This helps debugging tests.</stringProp>
                <stringProp name="RESPONSE_TIME">${__Random(50,500)}</stringProp>
                <stringProp name="LATENCY">${__Random(1,50)}</stringProp>
                <stringProp name="CONNECT">${__Random(1,5)}</stringProp>
                <stringProp name="URL"/>
                <stringProp name="RESULT_CLASS">org.apache.jmeter.samplers.SampleResult</stringProp>
                </kg.apc.jmeter.samplers.DummySampler>
                <hashTree/>
                <ResultCollector guiclass="TableVisualizer" testclass="ResultCollector" testname="View Results in Table">
                <boolProp name="ResultCollector.error_logging">false</boolProp>
                <objProp>
                <name>saveConfig</name>
                <value class="SampleSaveConfiguration">
                <time>true</time>
                <latency>true</latency>
                <timestamp>true</timestamp>
                <success>true</success>
                <label>true</label>
                <code>true</code>
                <message>true</message>
                <threadName>true</threadName>
                <dataType>true</dataType>
                <encoding>false</encoding>
                <assertions>true</assertions>
                <subresults>true</subresults>
                <responseData>false</responseData>
                <samplerData>false</samplerData>
                <xml>false</xml>
                <fieldNames>true</fieldNames>
                <responseHeaders>false</responseHeaders>
                <requestHeaders>false</requestHeaders>
                <responseDataOnError>false</responseDataOnError>
                <saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage>
                <assertionsResultsToSave>0</assertionsResultsToSave>
                <bytes>true</bytes>
                <sentBytes>true</sentBytes>
                <url>true</url>
                <threadCounts>true</threadCounts>
                <idleTime>true</idleTime>
                <connectTime>true</connectTime>
                </value>
                </objProp>
                <stringProp name="filename">finished.csv</stringProp>
                </ResultCollector>
                <hashTree/>
                </hashTree>
                </hashTree>
                """, healthCheckUrl == null ? "" : healthCheckConfig(ports.get(healthCheckTarget)), samplerString);
    }

    private String healthCheckConfig(int port) {
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
                <stringProp name="HTTPSampler.port">%s</stringProp>
                <stringProp name="HTTPSampler.path">%s</stringProp>
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
                </hashTree>
                """;
        return String.format(base, healthCheckTarget, healthCheckHttps ? "https" : "http", port, healthCheckUrl);
    }
}
