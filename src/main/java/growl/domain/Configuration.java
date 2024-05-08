package growl.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Record for representing the highest configuration parameters in the framework.
 * @param platform information about the platform the deploying and benchmarking will take place on. Required
 * @param images list of images to be deployed. Required and may not be empty
 * @param tests test specifications. Required
 * @param performance performance demands. Required
 */
public record Configuration(String platform, List<Image> images, TestSpecs tests, PerformanceDemands performance) {
    public Configuration {
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(images, "images cannot be null");
        if (images.isEmpty()) throw new IllegalArgumentException("images should not be empty");

        Set<String> imageIds = images.stream().map(Image::id).collect(Collectors.toSet());
        if (imageIds.size() != images.size()) throw new IllegalArgumentException("Duplicate image IDs");

        Objects.requireNonNull(tests, "tests cannot be null");
        Objects.requireNonNull(performance, "performance cannot be null");
    }

    public String toXML() {
        return String.format("""
                <hashTree>
                <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Test Plan">
                <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
                <collectionProp name="Arguments.arguments"/>
                </elementProp>
                <boolProp name="TestPlan.functional_mode">false</boolProp>
                <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
                </TestPlan>
                <hashTree>
                %s
                %s
                </hashTree>
                </hashTree>
                """, createThreadGroupConfiguration(), tests.toXML());
    }

    /**
     * Creates a thread group configuration with 5 threads, 0 ramp-up time and no loops.
     */
    private String createThreadGroupConfiguration() {
        return String.format("""
                <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group">
                <intProp name="ThreadGroup.num_threads">%s</intProp>
                <intProp name="ThreadGroup.ramp_time">50</intProp>
                <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller">
                <stringProp name="LoopController.loops">1</stringProp>
                <boolProp name="LoopController.continue_forever">false</boolProp>
                </elementProp>
                </ThreadGroup>
                """, performance.throughput());
    }
}
