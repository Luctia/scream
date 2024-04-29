package growl.domain;

public record Sampler(method method, String path, double percentage, String requestBody) {
    public enum method { GET, POST, PUT, DELETE }
}
