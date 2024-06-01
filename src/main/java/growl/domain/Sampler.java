package growl.domain;

import java.util.Objects;

/**
 * Record to represent samplers for benchmarking.
 * @param method HTTP method to use. Defaults to GET
 * @param path path to send request to. Required
 * @param percentage percentage of total load taken by this path. Required
 * @param requestBody body to send with the request
 */
public record Sampler(method method, String path, double percentage, String requestBody) {
    public enum method { GET, POST, PUT, DELETE }
    /** The duration of a benchmark. The steps in the throughput shaper will be evenly spaced out in this timeframe. */
    private static final int TEST_DURATION = 60;

    public Sampler {
        Objects.requireNonNull(path, "path cannot be null");
        if (percentage < 0) {throw new IllegalArgumentException("Percentage cannot be negative");}
    }

    public String toXML(int maxRPS, int index) {
        // TODO resolve domain; what to do here?
        String domain = "localhost";
        String bodySection;
        if (requestBody != null) {
            bodySection = String.format("""
                    <collectionProp name="Arguments.arguments">
                    <elementProp name="" elementType="HTTPArgument">
                    <boolProp name="HTTPArgument.always_encode">false</boolProp>
                    <stringProp name="Argument.value">%s</stringProp>
                    <stringProp name="Argument.metadata">=</stringProp>
                    </elementProp>
                    </collectionProp>
                    """, requestBody.replaceAll("\"", "&quot;"));
        } else {
            bodySection = "<collectionProp name=\"Arguments.arguments\"/>";
        }
        String tstIdentifier = this.method + "_" + this.path.replaceAll("/", "") + "_" + index;
        return String.format("""
                <com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup guiclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui" testclass="com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup" testname="bzm - Concurrency Thread Group">
                <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <stringProp name="TargetLevel">${__tstFeedback(%s,%d,%d,%d)}</stringProp>
                <stringProp name="RampUp"></stringProp>
                <stringProp name="Steps"></stringProp>
                <stringProp name="Hold">35</stringProp>
                <stringProp name="LogFilename"></stringProp>
                <stringProp name="Iterations"></stringProp>
                <stringProp name="Unit">M</stringProp>
                </com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup>
                <hashTree>
                %s
                <hashTree/>
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="%s" enabled="true">
                <stringProp name="HTTPSampler.domain">%s</stringProp>
                <stringProp name="HTTPSampler.protocol">%s</stringProp>
                <stringProp name="HTTPSampler.path">%s</stringProp>
                <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                <stringProp name="HTTPSampler.method">%s</stringProp>
                <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                <boolProp name="HTTPSampler.postBodyRaw">%s</boolProp>
                <elementProp name="HTTPsampler.Arguments" elementType="Arguments"%s>
                %s
                </elementProp>
                </HTTPSamplerProxy>
                """,
                // Throughput shaping timer name
                tstIdentifier,
                // Starting threads
                10,
                // Maximum threads
                maxRPS / 2,
                // Spare threads
                maxRPS / 100,
                generateVariableThroughputTimer(maxRPS, tstIdentifier),
                method + " " + path,
                domain,
                // TODO choose between http and https
                "http",
                path,
                method,
                requestBody == null ? "false" : "true",
                requestBody == null ? " guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\"" : "",
                bodySection
        );
    }

    private String generateVariableThroughputTimer(int maxRPS, String identifier) {
        String firstLine = String.format("<kg.apc.jmeter.timers.VariableThroughputTimer guiclass=\"kg.apc.jmeter.timers.VariableThroughputTimerGui\" testclass=\"kg.apc.jmeter.timers.VariableThroughputTimer\" testname=\"%s\">\n<collectionProp name=\"load_profile\">\n", identifier);
        StringBuilder out = new StringBuilder(firstLine);
        int stepDuration = Math.ceilDiv(TEST_DURATION, 20);
        int stepIncrease = Math.ceilDiv(maxRPS, 10);
        int requests = 1;
        int name = 1;
        for (int i = 0; i < 10; i++) {
            out.append(String.format("""
                       <collectionProp name="%d">
                       <stringProp name="1">%d</stringProp>
                       <stringProp name="2">%d</stringProp>
                       <stringProp name="54">%d</stringProp>
                       </collectionProp>
                       <collectionProp name="%d">
                       <stringProp name="1">%d</stringProp>
                       <stringProp name="2">%d</stringProp>
                       <stringProp name="54">%d</stringProp>
                       </collectionProp>
                       """, name,
                    requests,
                    requests + stepIncrease,
                    stepDuration,
                    name + 1,
                    requests + stepIncrease,
                    requests + stepIncrease,
                    stepDuration));
            requests += stepIncrease;
            name += 2;
        }
        out.append("</collectionProp>\n</kg.apc.jmeter.timers.VariableThroughputTimer>\n");
        return out.toString();
    }
}
