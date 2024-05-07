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
        TestSpecs testSpecs = new TestSpecs("/healthcheck", false, samplerList);
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
    void Should_Represent_Multiple_Ordered_Samplers_In_TestSpec() {
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
        TestSpecs testSpecs = new TestSpecs("/healthcheck", true, samplerList);
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
        assertEquals(expectedXML, testSpecs.toXML());
    }

    @Test
    void Should_Properly_Construct_XML() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/sample.json");
        String expectedOutput = "";
        try {
            expectedOutput = Files.readString(Path.of("src/test/resources/testid.jmx"));
        } catch (IOException e) {
            fail("File containing expected output not found");
        }
        assert config != null;
        // When asserting, we need to remove some prettifying and double newlines. These do not change the validity or
        //  functionality of the file.
        assertEquals(
                expectedOutput.replaceAll("\n *", "\n"),
                XMLMaker.createTestplanXML(config).replaceAll("\n\n", "\n")
        );
    }

    @Test
    void Should_Pretty_Print_To_File() {

    }
}
