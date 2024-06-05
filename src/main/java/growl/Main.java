package growl;

import growl.domain.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = ConfigurationMaker.makeConfigurationFromFilename("config.json");
        XMLMaker.exportXML(configuration, true);
    }
}
