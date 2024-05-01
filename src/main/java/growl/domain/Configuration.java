package growl.domain;

import java.util.List;

public record Configuration(String platform, List<Image> images, TestSpecs tests, PerformanceDemands performance) {
    public String toXML() {
        return "";
    }
}
