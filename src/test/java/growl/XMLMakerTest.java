package growl;

import growl.domain.Configuration;
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
    @Test
    void Should_Represent_Sampler() {
        Sampler sampler = new Sampler(Sampler.method.POST, "/endpoint", 50, "{\"id\": 3}");
        String resultingXML = sampler.toXML();
        String expectedXML = """
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="POST /endpoint">
                <stringProp name="HTTPSampler.domain">localhost</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
                <stringProp name="HTTPSampler.path">/endpoint</stringProp>
                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                <stringProp name="HTTPSampler.method">POST</stringProp>
                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
                <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
                <collectionProp name="Arguments.arguments">
                <elementProp name="" elementType="HTTPArgument">
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                <stringProp name="Argument.value">{&quot;id&quot;: 3}</stringProp>
                <stringProp name="Argument.metadata">=</stringProp>
                </elementProp>
                </collectionProp>
                
                </elementProp>
                </HTTPSamplerProxy>
                """;
        assertEquals(expectedXML, resultingXML);
    }

    @Test
    void Should_Represent_Multiple_Samplers_In_TestSpec() {
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
        TestSpecs testSpecs = new TestSpecs(null, false, samplerList);
        String expectedXML = """
                <hashTree>
                
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint">
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
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec">
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
                """;
        assertEquals(expectedXML, testSpecs.toXML());
    }

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
        String expectedXML = """
                <hashTree>
                <GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="Ordered test"/>
                <hashTree>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint">
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
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec">
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
        assertEquals(expectedXML, testSpecs.toXML().replaceAll("\n\n", "\n"));
    }

    @Test
    void Should_Represent_Multiple_Ordered_Samplers_In_TestSpec_With_Healthcheck() {
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
        TestSpecs testSpecs = new TestSpecs("/v1/ping", true, samplerList);
        String expectedXML = """
                <hashTree>
                <WhileController guiclass="WhileControllerGui" testclass="WhileController" testname="Wait for health check">
                <stringProp name="WhileController.condition">${__jexl3(&quot;${responseCode}&quot; != 200,)}</stringProp>
                </WhileController>
                <hashTree>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Health check">
                <stringProp name="HTTPSampler.domain">/v1/ping</stringProp>
                <stringProp name="HTTPSampler.protocol">http</stringProp>
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
                <GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="Ordered test"/>
                <hashTree>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint">
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
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="GET /endpoint/spec">
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
        assertEquals(expectedXML, testSpecs.toXML().replaceAll("\n\n", "\n"));
    }

    @Test
    void Should_Properly_Construct_XML() {
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
    void Should_Pretty_Print_To_File() {
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
