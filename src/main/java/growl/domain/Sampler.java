package growl.domain;

public record Sampler(method method, String path, double percentage, String requestBody) {
    public enum method { GET, POST, PUT, DELETE }

    public String toXML() {
        // TODO resolve domain; what to do here?
        String domain = "localhost";
        String base = """
                        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="%s">
                          <stringProp name="HTTPSampler.domain">%s</stringProp>
                          <stringProp name="HTTPSampler.protocol">%s</stringProp>
                          <stringProp name="HTTPSampler.path">%s</stringProp>
                          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
                          <stringProp name="HTTPSampler.method">%s</stringProp>
                          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
                          <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
                          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
                            <collectionProp name="Arguments.arguments">
                              <elementProp name="" elementType="HTTPArgument">
                                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                                <stringProp name="Argument.value">{&quot;someProperty&quot;: true}</stringProp>
                                <stringProp name="Argument.metadata">=</stringProp>
                              </elementProp>
                            </collectionProp>
                          </elementProp>
                        </HTTPSamplerProxy>\
                """;
        return String.format(base,
                method.toString() + " " + path,
                domain,
                "http",
                path,
                method,
                requestBody.replaceAll("\"", "&quot;")
        );
    }
}
