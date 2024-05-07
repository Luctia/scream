package growl;

import growl.domain.Configuration;
import growl.domain.Sampler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationMakerTest {
    @Test
    void Should_Parse_Simple_Configuration_From_JSON_File() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/unorderedWithHealth.json");
        assertNotNull(config);
        assertEquals("azure", config.platform());
        assertEquals("presentation-tier", config.images().getFirst().id());

        assertEquals(1, config.images().size());
        assertEquals("test/presentation-tier", config.images().getFirst().containerId());
        assertEquals(8080, config.images().getFirst().port());
        assertTrue(config.images().getFirst().isTestEndpoint());
        assertEquals(2, config.images().getFirst().minNumInstances());
        assertTrue(config.images().getFirst().env().containsKey("LOGICAL_ENDPOINT"));
        assertEquals("{test/logical-tier}", config.images().getFirst().env().get("LOGICAL_ENDPOINT"));

        assertEquals("/v1/ping", config.tests().healthCheckUrl());
        assertFalse(config.tests().ordered());

        assertEquals(1, config.tests().samplers().size());
        assertEquals(Sampler.method.GET, config.tests().samplers().getFirst().method());
        assertEquals("/v1/policies/testid", config.tests().samplers().getFirst().path());
        assertEquals(99.9, config.tests().samplers().getFirst().percentage());
        assertEquals("{\"someProperty\": true}", config.tests().samplers().getFirst().requestBody());

        assertEquals(24050, config.performance().throughput());
        assertEquals("MINUTE", config.performance().throughputTimeUnit());
        assertEquals(50, config.performance().latency());
    }

    @Test
    void Should_Parse_Simple_Configuration_From_JSON_String() {
        String json = """
                {
                  "platform": "azure",
                  "images": [
                    {
                      "id": "presentation-tier",
                      "containerId": "test/presentation-tier",
                      "port": 8080,
                      "isTestEndpoint": true,
                      "minNumInstances": 2,
                      "env": {
                        "LOGICAL_ENDPOINT": "{test/logical-tier}"
                      }
                    }
                  ],
                  "tests": {
                    "healthCheckUrl": "/v1/ping",
                    "ordered": false,
                    "samplers": [
                      {
                        "method": "GET",
                        "path": "/v1/policies/testid",
                        "percentage": 99.9,
                        "requestBody": "{\\"someProperty\\": true}"
                      }
                    ]
                  },
                  "performance": {
                    "throughput": 24050,
                    "throughputTimeUnit": "MINUTE",
                    "latency": 50
                  }
                }
                """;
        Configuration config = ConfigurationMaker.makeConfiguration(json);
        assertEquals("azure", config.platform());
        assertEquals("presentation-tier", config.images().getFirst().id());

        assertEquals(1, config.images().size());
        assertEquals("test/presentation-tier", config.images().getFirst().containerId());
        assertEquals(8080, config.images().getFirst().port());
        assertTrue(config.images().getFirst().isTestEndpoint());
        assertEquals(2, config.images().getFirst().minNumInstances());
        assertTrue(config.images().getFirst().env().containsKey("LOGICAL_ENDPOINT"));
        assertEquals("{test/logical-tier}", config.images().getFirst().env().get("LOGICAL_ENDPOINT"));

        assertEquals("/v1/ping", config.tests().healthCheckUrl());
        assertFalse(config.tests().ordered());

        assertEquals(1, config.tests().samplers().size());
        assertEquals(Sampler.method.GET, config.tests().samplers().getFirst().method());
        assertEquals("/v1/policies/testid", config.tests().samplers().getFirst().path());
        assertEquals(99.9, config.tests().samplers().getFirst().percentage());
        assertEquals("{\"someProperty\": true}", config.tests().samplers().getFirst().requestBody());

        assertEquals(24050, config.performance().throughput());
        assertEquals("MINUTE", config.performance().throughputTimeUnit());
        assertEquals(50, config.performance().latency());
    }
}
