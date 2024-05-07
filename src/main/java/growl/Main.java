package growl;

import growl.domain.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = ConfigurationMaker.makeConfigurationFromFilename("src/test/resources/sample.json");
        XMLMaker.exportXML(configuration, true);
    }
}