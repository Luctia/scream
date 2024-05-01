package growl.domain;

import java.util.List;

public record Configuration(String platform, List<Image> images, TestSpecs tests, PerformanceDemands performance) {
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
     * TODO this should be derived from configuration settings instead of being static.
     */
    private String createThreadGroupConfiguration() {
        return """
                <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group">
                <intProp name="ThreadGroup.num_threads">5</intProp>
                <intProp name="ThreadGroup.ramp_time">0</intProp>
                <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller">
                <stringProp name="LoopController.loops">1</stringProp>
                <boolProp name="LoopController.continue_forever">false</boolProp>
                </elementProp>
                </ThreadGroup>
                """;
    }
}
