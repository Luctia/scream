package growl;

import growl.domain.Configuration;
import growl.domain.PerformanceDemands;
import growl.domain.Sampler;
import growl.domain.TestSpecs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class XMLMakerTest {

    @Test
    void Should_Represent_Multiple_Samplers_In_TestSpec_Without_Healthcheck() {
        List<Sampler> samplerList = new ArrayList<>();
        samplerList.add(new Sampler(
                Sampler.Method.GET,
                "presentation-tier",
                "/endpoint",
                false,
                34.5,
                null)
        );
        samplerList.add(new Sampler(
                Sampler.Method.GET,
                "presentation-tier",
                "/endpoint/spec",
                false,
                34.5,
                null)
        );
        Map<String, Integer> portMap = new HashMap<>();
        portMap.put("presentation-tier", 80);
        TestSpecs testSpecs = new TestSpecs(null, null, false, samplerList);
        PerformanceDemands performanceDemands = new PerformanceDemands(10000, PerformanceDemands.TimeUnit.MINUTE, 50, 0.01, 0.1);

        String expectedXML = """
                <hashTree>
                <com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup guiclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui" testclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup" testname="bzm - Concurrency Thread Group">
                <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <stringProp name="TargetLevel">${__tstFeedback(GET_presentation-tier_endpoint_0,2,28,0)}</stringProp>
                <stringProp name="RampUp"></stringProp>
                <stringProp name="Steps"></stringProp>
                <stringProp name="Hold">35</stringProp>
                <stringProp name="LogFilename"></stringProp>
                <stringProp name="Iterations"></stringProp>
                <stringProp name="Unit">M</stringProp>
                </com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup>
                <hashTree>
                <kg.apc.jmeter.timers.VariableThroughputTimer guiclass="kg.apc.jmeter.timers.VariableThroughputTimerGui" testclass="kg.apc.jmeter.timers.VariableThroughputTimer" testname="GET_presentation-tier_endpoint_0">
                <collectionProp name="load_profile">
                <collectionProp name="1">
                <stringProp name="1">1</stringProp>
                <stringProp name="2">7</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="2">
                <stringProp name="1">7</stringProp>
                <stringProp name="2">7</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="3">
                <stringProp name="1">7</stringProp>
                <stringProp name="2">13</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="4">
                <stringProp name="1">13</stringProp>
                <stringProp name="2">13</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="5">
                <stringProp name="1">13</stringProp>
                <stringProp name="2">19</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="6">
                <stringProp name="1">19</stringProp>
                <stringProp name="2">19</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="7">
                <stringProp name="1">19</stringProp>
                <stringProp name="2">25</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="8">
                <stringProp name="1">25</stringProp>
                <stringProp name="2">25</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="9">
                <stringProp name="1">25</stringProp>
                <stringProp name="2">31</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="10">
                <stringProp name="1">31</stringProp>
                <stringProp name="2">31</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="11">
                <stringProp name="1">31</stringProp>
                <stringProp name="2">37</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="12">
                <stringProp name="1">37</stringProp>
                <stringProp name="2">37</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="13">
                <stringProp name="1">37</stringProp>
                <stringProp name="2">43</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="14">
                <stringProp name="1">43</stringProp>
                <stringProp name="2">43</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="15">
                <stringProp name="1">43</stringProp>
                <stringProp name="2">49</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="16">
                <stringProp name="1">49</stringProp>
                <stringProp name="2">49</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="17">
                <stringProp name="1">49</stringProp>
                <stringProp name="2">55</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="18">
                <stringProp name="1">55</stringProp>
                <stringProp name="2">55</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="19">
                <stringProp name="1">55</stringProp>
                <stringProp name="2">61</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="20">
                <stringProp name="1">61</stringProp>
                <stringProp name="2">61</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                </collectionProp>
                </kg.apc.jmeter.timers.VariableThroughputTimer>
                <hashTree/>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET presentation-tier/endpoint" enabled="true">
                <stringProp name="HTTPSampler.domain">presentation-tier</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
                <stringProp name="HTTPSampler.port">80</stringProp>
                <stringProp name="HTTPSampler.path">/endpoint</stringProp>
                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                <stringProp name="HTTPSampler.method">GET</stringProp>
                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
                <collectionProp name="Arguments.arguments"/>
                </elementProp>
                </HTTPSamplerProxy>
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
                <stringProp name="filename">GET_presentation-tier_endpoint_0.csv</stringProp>
                </ResultCollector>
                <hashTree/>
                </hashTree>
                <com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup guiclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui" testclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup" testname="bzm - Concurrency Thread Group">
                <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <stringProp name="TargetLevel">${__tstFeedback(GET_presentation-tier_endpointspec_1,2,28,0)}</stringProp>
                <stringProp name="RampUp"></stringProp>
                <stringProp name="Steps"></stringProp>
                <stringProp name="Hold">35</stringProp>
                <stringProp name="LogFilename"></stringProp>
                <stringProp name="Iterations"></stringProp>
                <stringProp name="Unit">M</stringProp>
                </com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup>
                <hashTree>
                <kg.apc.jmeter.timers.VariableThroughputTimer guiclass="kg.apc.jmeter.timers.VariableThroughputTimerGui" testclass="kg.apc.jmeter.timers.VariableThroughputTimer" testname="GET_presentation-tier_endpointspec_1">
                <collectionProp name="load_profile">
                <collectionProp name="1">
                <stringProp name="1">1</stringProp>
                <stringProp name="2">7</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="2">
                <stringProp name="1">7</stringProp>
                <stringProp name="2">7</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="3">
                <stringProp name="1">7</stringProp>
                <stringProp name="2">13</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="4">
                <stringProp name="1">13</stringProp>
                <stringProp name="2">13</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="5">
                <stringProp name="1">13</stringProp>
                <stringProp name="2">19</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="6">
                <stringProp name="1">19</stringProp>
                <stringProp name="2">19</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="7">
                <stringProp name="1">19</stringProp>
                <stringProp name="2">25</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="8">
                <stringProp name="1">25</stringProp>
                <stringProp name="2">25</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="9">
                <stringProp name="1">25</stringProp>
                <stringProp name="2">31</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="10">
                <stringProp name="1">31</stringProp>
                <stringProp name="2">31</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="11">
                <stringProp name="1">31</stringProp>
                <stringProp name="2">37</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="12">
                <stringProp name="1">37</stringProp>
                <stringProp name="2">37</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="13">
                <stringProp name="1">37</stringProp>
                <stringProp name="2">43</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="14">
                <stringProp name="1">43</stringProp>
                <stringProp name="2">43</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="15">
                <stringProp name="1">43</stringProp>
                <stringProp name="2">49</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="16">
                <stringProp name="1">49</stringProp>
                <stringProp name="2">49</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="17">
                <stringProp name="1">49</stringProp>
                <stringProp name="2">55</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="18">
                <stringProp name="1">55</stringProp>
                <stringProp name="2">55</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="19">
                <stringProp name="1">55</stringProp>
                <stringProp name="2">61</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                <collectionProp name="20">
                <stringProp name="1">61</stringProp>
                <stringProp name="2">61</stringProp>
                <stringProp name="54">3</stringProp>
                </collectionProp>
                </collectionProp>
                </kg.apc.jmeter.timers.VariableThroughputTimer>
                <hashTree/>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET presentation-tier/endpoint/spec" enabled="true">
                <stringProp name="HTTPSampler.domain">presentation-tier</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
                <stringProp name="HTTPSampler.port">80</stringProp>
                <stringProp name="HTTPSampler.path">/endpoint/spec</stringProp>
                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                <stringProp name="HTTPSampler.method">GET</stringProp>
                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
                <collectionProp name="Arguments.arguments"/>
                </elementProp>
                </HTTPSamplerProxy>
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
                <stringProp name="filename">GET_presentation-tier_endpointspec_1.csv</stringProp>
                </ResultCollector>
                <hashTree/>
                </hashTree>
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
                """;
        assertEquals(expectedXML, testSpecs.toXML(performanceDemands, portMap).replaceAll("\n\n", "\n"));
    }

    @Test
    void Should_Represent_With_Health_Check() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/withHealthCheckAndRampUp.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/withHealthCheckAndRampUp.jmx"));
        } catch (IOException e) {
            fail("File containing expected output not found");
        }
        assert config != null;
        // When asserting, we need to remove some prettifying and double newlines. These do not change the validity or
        //  functionality of the file.
        assertEquals(
                expectedOutput.replaceAll("\n *", "\n"),
                XMLMaker.createTestplanXML(config, false).replaceAll("\n\n", "\n")
        );
    }

    @Test
    void Should_Properly_Construct_XML_Without_Health() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/withoutHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/withoutHealth.jmx"));
        } catch (IOException e) {
            fail("File containing expected output not found");
        }
        assert config != null;
        // When asserting, we need to remove some prettifying and double newlines. These do not change the validity or
        //  functionality of the file.
        assertEquals(
                expectedOutput.replaceAll("\n *", "\n"),
                XMLMaker.createTestplanXML(config, false).replaceAll("\n\n", "\n")
        );
    }

    @Test
    void Should_Properly_Construct_XML_Without_Health_Second() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/secondWithoutHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/secondWithoutHealth.jmx"));
        } catch (IOException e) {
            fail("File containing expected output not found");
        }
        assert config != null;
        // When asserting, we need to remove some prettifying and double newlines. These do not change the validity or
        //  functionality of the file.
        assertEquals(
                expectedOutput.replaceAll("\n *", "\n"),
                XMLMaker.createTestplanXML(config, false).replaceAll("\n\n", "\n")
        );
    }

    @Test
    void Should_Pretty_Print_To_File_With_Health() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/withHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/withHealth.jmx"));
        } catch (IOException e) {
            fail("File containing expected output not found");
        }
        assert config != null;
        XMLMaker.exportXML(config, true);
        try {
            assertEquals(
                    expectedOutput,
                    Files.readString(Path.of("config.jmx"))
            );
        } catch (IOException e) {
            fail("Config not exported");
        }
    }
}
