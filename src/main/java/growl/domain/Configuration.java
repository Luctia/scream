package growl.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Record for representing the highest configuration parameters in the framework.
 * @param namespace information about the namespace the deploying and benchmarking will take place on. Required
 * @param images list of images to be deployed. Required and may not be empty
 * @param tests test specifications. Required
 * @param performance performance demands. Required
 */
public record Configuration(String namespace, List<Image> images, TestSpecs tests, PerformanceDemands performance) {
    public Configuration {
        Objects.requireNonNull(namespace, "namespace cannot be null");
        Objects.requireNonNull(images, "images cannot be null");
        if (images.isEmpty()) System.out.println("Warning: images is empty");

        Set<String> imageIds = images.stream().map(Image::imageId).collect(Collectors.toSet());
        if (imageIds.size() != images.size()) throw new IllegalArgumentException("Duplicate image IDs");

        Objects.requireNonNull(tests, "tests cannot be null");
        Objects.requireNonNull(performance, "performance cannot be null");
    }

    public String toXML() {
        Map<String, Integer> targetPortMap = images.stream().collect(Collectors.toMap(Image::imageId, Image::port));
        return String.format("""
                <hashTree>
                <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Test Plan">
                <boolProp name="TestPlan.serialize_threadgroups">true</boolProp>
                <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
                <collectionProp name="Arguments.arguments"/>
                </elementProp>
                <boolProp name="TestPlan.functional_mode">false</boolProp>
                </TestPlan>
                %s
                </hashTree>
                """,
                tests.toXML(performance, targetPortMap));
    }
}
