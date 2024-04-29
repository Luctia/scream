package growl.domain;

import java.util.Map;

public record Image(String id, String containerId, int port, boolean isTestEndpoint, int minNumInstances,
                    Map<String, String> env) {
}
