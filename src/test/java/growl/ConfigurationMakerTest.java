package growl;

import growl.domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationMakerTest {
    @Test
    void Should_Parse_Simple_Configuration_From_JSON_File() {
        Configuration config = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/withHealth.json");
        assertNotNull(config);
        assertEquals("default", config.namespace());
        assertEquals("presentation-tier", config.images().getFirst().imageId());

        assertEquals(1, config.images().size());
        assertEquals("test/presentation-tier", config.images().getFirst().containerId());
        assertEquals(8080, config.images().getFirst().port());
        assertTrue(config.images().getFirst().isTestEndpoint());
        assertEquals(2, config.images().getFirst().minNumInstances());
        assertTrue(config.images().getFirst().env().containsKey("LOGICAL_ENDPOINT"));
        assertEquals("{test/logical-tier}", config.images().getFirst().env().get("LOGICAL_ENDPOINT"));

        assertEquals("/v1/ping", config.tests().healthCheckUrl());

        assertEquals(1, config.tests().samplers().size());
        assertEquals(Sampler.Method.GET, config.tests().samplers().getFirst().method());
        assertEquals("/v1/policies/testid", config.tests().samplers().getFirst().path());
        assertEquals(99.9, config.tests().samplers().getFirst().percentage());
        assertEquals("{\"someProperty\": true}", config.tests().samplers().getFirst().requestBody());

        assertEquals(24050, config.performance().throughput());
        assertEquals(PerformanceDemands.TimeUnit.MINUTE, config.performance().throughputTimeUnit());
        assertEquals(50, config.performance().latency());
    }

    @Test
    void Should_Parse_Simple_Configuration_From_JSON_String() {
        String json = """
                {
                  "namespace": "default",
                  "images": [
                    {
                      "imageId": "presentation-tier",
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
                    "latency": 50,
                    "failureRate": 0.01
                  }
                }
                """;
        Configuration config = ConfigurationMaker.makeConfiguration(json);
        assertEquals("default", config.namespace());
        assertEquals("presentation-tier", config.images().getFirst().imageId());

        assertEquals(1, config.images().size());
        assertEquals("test/presentation-tier", config.images().getFirst().containerId());
        assertEquals(8080, config.images().getFirst().port());
        assertTrue(config.images().getFirst().isTestEndpoint());
        assertEquals(2, config.images().getFirst().minNumInstances());
        assertTrue(config.images().getFirst().env().containsKey("LOGICAL_ENDPOINT"));
        assertEquals("{test/logical-tier}", config.images().getFirst().env().get("LOGICAL_ENDPOINT"));

        assertEquals("/v1/ping", config.tests().healthCheckUrl());

        assertEquals(1, config.tests().samplers().size());
        assertEquals(Sampler.Method.GET, config.tests().samplers().getFirst().method());
        assertEquals("/v1/policies/testid", config.tests().samplers().getFirst().path());
        assertEquals(99.9, config.tests().samplers().getFirst().percentage());
        assertEquals("{\"someProperty\": true}", config.tests().samplers().getFirst().requestBody());

        assertEquals(24050, config.performance().throughput());
        assertEquals(PerformanceDemands.TimeUnit.MINUTE, config.performance().throughputTimeUnit());
        assertEquals(50, config.performance().latency());
    }

    @Test
    void Throw_On_Invalid_File_Name() {
        assertThrows(RuntimeException.class, () -> ConfigurationMaker.makeConfigurationFromFilename("thisFileDoesNotExist.json"));
    }

    @Test
    void Throw_On_Invalid_JSON() {
        assertThrows(RuntimeException.class, () -> ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/growl/inputs/brokenJSON.json"));
    }

    @Test
    void Enforces_Record_Requirements() {
        // Start with required fields
        assertThrows(NullPointerException.class, () -> new Configuration(null, new ArrayList<>(), null, null));
        assertThrows(NullPointerException.class, () -> new Image(null, null, 0, false, 0, null));
        assertThrows(NullPointerException.class, () -> new TestSpecs(null, null, false, null));
        assertThrows(NullPointerException.class, () -> new Sampler(null, null, null, false, 0, null));

        assertThrows(IllegalArgumentException.class, () -> new TestSpecs(null, null, false, new ArrayList<>()));

        // Test not-negative requirements
        assertThrows(IllegalArgumentException.class, () -> new Image("id", "id", -1, false, 1, null));
        assertThrows(IllegalArgumentException.class, () -> new Image("id", "id", 0, false, 1, null));
        assertThrows(IllegalArgumentException.class, () -> new Image("id", "id", 65536, false, 1, null));
        assertThrows(IllegalArgumentException.class, () -> new Image("id", "id", 1, false, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new Image("id", "id", 1, false, -1, null));

        assertThrows(IllegalArgumentException.class, () -> new PerformanceDemands(-1, null, 0, 0, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new PerformanceDemands(0, null, -1, 0, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new PerformanceDemands(0, null, 0, -1, 0.1));

        assertThrows(IllegalArgumentException.class, () -> new Sampler(null, null, "", false, -1, null));

        // Test no image ID duplicates
        assertThrows(IllegalArgumentException.class, () -> {
            Image image1 = new Image("id", "id", 1, false, 1, null);
            Image image2 = new Image("id", "id", 1, false, 1, null);
            List<Image> imageList = new ArrayList<>();
            imageList.add(image1);
            imageList.add(image2);
            new Configuration("default", imageList, null, null);
        });
    }
}
