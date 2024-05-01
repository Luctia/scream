package growl;

import growl.domain.Configuration;

/**
 * Used to turn {@link growl.domain.Configuration} objects into JMeter-compatible XML files.
 * @author Luc Timmerman
 */
public class XMLMaker {
    /**
     * Transform the configuration to JMeter-compatible Testplan XML.
     * @param configuration the configuration to be used
     * @return the resulting XML as a string
     */
    static String createTestplanXML(Configuration configuration) {
        return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
                %s
                </jmeterTestPlan>
                """, configuration.toXML())
                .replaceAll("\n\n", "\n");
    }

    /**
     * Export the configuration as a JMeter-compatible Testplan XML file.
     * @param configuration the configuration to be used
     */
    void exportXML(Configuration configuration) {
        String xml = createTestplanXML(configuration);
        // TODO save as XML file
    }
}
