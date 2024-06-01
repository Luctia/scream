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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class XMLMakerTest {
//    @Test
//    void Should_Represent_Sampler() {
//        Sampler sampler = new Sampler(Sampler.method.POST, "/endpoint", 50, "{\"id\": 3}");
//        String resultingXML = sampler.toXML(10, 10);
//        String expectedXML = """
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="POST /endpoint">
//                <stringProp name="HTTPSampler.domain">localhost</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <stringProp name="HTTPSampler.path">/endpoint</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">POST</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
//                <collectionProp name="Arguments.arguments">
//                <elementProp name="" elementType="HTTPArgument">
//                <boolProp name="HTTPArgument.always_encode">false</boolProp>
//                <stringProp name="Argument.value">{&quot;id&quot;: 3}</stringProp>
//                <stringProp name="Argument.metadata">=</stringProp>
//                </elementProp>
//                </collectionProp>
//
//                </elementProp>
//                </HTTPSamplerProxy>
//                """;
//        assertEquals(expectedXML, resultingXML);
//    }

//    @Test
//    void Should_Represent_Multiple_Samplers_In_TestSpec() {
//        List<Sampler> samplerList = new ArrayList<>();
//        samplerList.add(new Sampler(
//                Sampler.method.GET,
//                "/endpoint",
//                34.5,
//                null)
//        );
//        samplerList.add(new Sampler(
//                Sampler.method.GET,
//                "/endpoint/spec",
//                34.5,
//                null)
//        );
//        TestSpecs testSpecs = new TestSpecs(null, false, samplerList);
//        PerformanceDemands performanceDemands = new PerformanceDemands(10000, PerformanceDemands.TimeUnit.MINUTE, 50);
//
//        String expectedXML = """
//                <hashTree>
//
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint">
//                <stringProp name="HTTPSampler.domain">localhost</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <stringProp name="HTTPSampler.path">/endpoint</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">GET</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
//                <collectionProp name="Arguments.arguments"/>
//                </elementProp>
//                </HTTPSamplerProxy>
//                <hashTree/>
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec">
//                <stringProp name="HTTPSampler.domain">localhost</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <stringProp name="HTTPSampler.path">/endpoint/spec</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">GET</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
//                <collectionProp name="Arguments.arguments"/>
//                </elementProp>
//                </HTTPSamplerProxy>
//                <hashTree/>
//
//                </hashTree>
//                """;
//        assertEquals(expectedXML, testSpecs.toXML(performanceDemands));
//    }

    @Test
    void Should_Represent_Multiple_Ordered_Samplers_In_TestSpec_Without_Healthcheck() {
        List<Sampler> samplerList = new ArrayList<>();
        samplerList.add(new Sampler(
                Sampler.method.GET,
                "/endpoint",
                34.5,
                null)
        );
        samplerList.add(new Sampler(
                Sampler.method.GET,
                "/endpoint/spec",
                34.5,
                null)
        );
        TestSpecs testSpecs = new TestSpecs(null, true, samplerList);
        PerformanceDemands performanceDemands = new PerformanceDemands(10000, PerformanceDemands.TimeUnit.MINUTE, 50);

        String expectedXML = """
                <hashTree>
                <com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup guiclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui" testclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup" testname="bzm - Concurrency Thread Group">
                <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <stringProp name="TargetLevel">${__tstFeedback(GET_endpoint_0,10,28,0)}</stringProp>
                <stringProp name="RampUp"></stringProp>
                <stringProp name="Steps"></stringProp>
                <stringProp name="Hold">35</stringProp>
                <stringProp name="LogFilename"></stringProp>
                <stringProp name="Iterations"></stringProp>
                <stringProp name="Unit">M</stringProp>
                </com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup>
                <hashTree>
                <kg.apc.jmeter.timers.VariableThroughputTimer guiclass="kg.apc.jmeter.timers.VariableThroughputTimerGui" testclass="kg.apc.jmeter.timers.VariableThroughputTimer" testname="GET_endpoint_0">
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
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint" enabled="true">
                <stringProp name="HTTPSampler.domain">localhost</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
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
                </hashTree>
                <com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup guiclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui" testclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup" testname="bzm - Concurrency Thread Group">
                <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <stringProp name="TargetLevel">${__tstFeedback(GET_endpointspec_1,10,28,0)}</stringProp>
                <stringProp name="RampUp"></stringProp>
                <stringProp name="Steps"></stringProp>
                <stringProp name="Hold">35</stringProp>
                <stringProp name="LogFilename"></stringProp>
                <stringProp name="Iterations"></stringProp>
                <stringProp name="Unit">M</stringProp>
                </com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup>
                <hashTree>
                <kg.apc.jmeter.timers.VariableThroughputTimer guiclass="kg.apc.jmeter.timers.VariableThroughputTimerGui" testclass="kg.apc.jmeter.timers.VariableThroughputTimer" testname="GET_endpointspec_1">
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
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec" enabled="true">
                <stringProp name="HTTPSampler.domain">localhost</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
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
                </hashTree>
                </hashTree>
                """;
        assertEquals(expectedXML, testSpecs.toXML(performanceDemands).replaceAll("\n\n", "\n"));
    }

//    @Test
//    void Should_Represent_Multiple_Ordered_Samplers_In_TestSpec_With_Healthcheck() {
//        List<Sampler> samplerList = new ArrayList<>();
//        samplerList.add(new Sampler(
//                Sampler.method.GET,
//                "/endpoint",
//                34.5,
//                null)
//        );
//        samplerList.add(new Sampler(
//                Sampler.method.GET,
//                "/endpoint/spec",
//                34.5,
//                null)
//        );
//        TestSpecs testSpecs = new TestSpecs("/v1/ping", true, samplerList);
//        PerformanceDemands performanceDemands = new PerformanceDemands(10000, PerformanceDemands.TimeUnit.MINUTE, 50);
//
//        String expectedXML = """
//                <hashTree>
//                <WhileController guiclass="WhileControllerGui" testclass="WhileController" testname="Wait for health check">
//                <stringProp name="WhileController.condition">${__jexl3(&quot;${responseCode}&quot; != 200,)}</stringProp>
//                </WhileController>
//                <hashTree>
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Health check">
//                <stringProp name="HTTPSampler.domain">/v1/ping</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">GET</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
//                <collectionProp name="Arguments.arguments"/>
//                </elementProp>
//                </HTTPSamplerProxy>
//                <hashTree>
//                <RegexExtractor guiclass="RegexExtractorGui" testclass="RegexExtractor" testname="Regular Expression Extractor">
//                <stringProp name="RegexExtractor.useHeaders">code</stringProp>
//                <stringProp name="RegexExtractor.refname">responseCode</stringProp>
//                <stringProp name="RegexExtractor.regex">(.*)</stringProp>
//                <stringProp name="RegexExtractor.template">$1$</stringProp>
//                <stringProp name="RegexExtractor.default"></stringProp>
//                <boolProp name="RegexExtractor.default_empty_value">false</boolProp>
//                <stringProp name="RegexExtractor.match_number"></stringProp>
//                </RegexExtractor>
//                <hashTree/>
//                </hashTree>
//                <ConstantTimer guiclass="ConstantTimerGui" testclass="ConstantTimer" testname="Constant Timer">
//                <stringProp name="ConstantTimer.delay">1000</stringProp>
//                </ConstantTimer>
//                <hashTree/>
//                </hashTree>
//                <GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="Ordered test"/>
//                <hashTree>
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint">
//                <stringProp name="HTTPSampler.domain">localhost</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <stringProp name="HTTPSampler.path">/endpoint</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">GET</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
//                <collectionProp name="Arguments.arguments"/>
//                </elementProp>
//                </HTTPSamplerProxy>
//                <hashTree/>
//                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec">
//                <stringProp name="HTTPSampler.domain">localhost</stringProp>
//                <stringProp name="HTTPSampler.protocol">http</stringProp>
//                <stringProp name="HTTPSampler.path">/endpoint/spec</stringProp>
//                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
//                <stringProp name="HTTPSampler.method">GET</stringProp>
//                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
//                <boolProp name="HTTPSampler.postBodyRaw">false</boolProp>
//                <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
//                <collectionProp name="Arguments.arguments"/>
//                </elementProp>
//                </HTTPSamplerProxy>
//                <hashTree/>
//                </hashTree>
//                </hashTree>
//                """;
//        assertEquals(expectedXML, testSpecs.toXML(performanceDemands).replaceAll("\n\n", "\n"));
//    }

    @Test
    void Should_Represent_Ordered_With_Health_Check() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/orderedWithHealthCheckAndRampUp.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/orderedWithHealthCheckAndRampUp.jmx"));
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
    void Should_Properly_Construct_XML_Ordered_Without_Health() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/orderedWithoutHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/orderedWithoutHealth.jmx"));
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
    void Should_Properly_Construct_XML_Unordered_Without_Health() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/unorderedWithoutHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/unorderedWithoutHealth.jmx"));
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
    void Should_Pretty_Print_To_File_Unordered_With_Health() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/unorderedWithHealth.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/growl/outputs/unorderedWithHealth.jmx"));
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
