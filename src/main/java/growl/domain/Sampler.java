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

    public Sampler {
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(percentage, "percentage cannot be null");
        if (percentage < 0) {throw new IllegalArgumentException("Percentage cannot be negative");}
    }

    public String toXML() {
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
        String base = """
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="%s">
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
                """;
        return String.format(base,
                method.toString() + " " + path,
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
}
