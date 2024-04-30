package growl;

import growl.domain.Configuration;

/**
 * Used to turn {@link growl.domain.Configuration} objects into JMeter-compatible XML files.
 * @author Luc Timmerman
 */
public class XMLMaker {
    /**
     * Transform the configuration to JMeter-compatible XML.
     * @param configuration the configuration to be used
     * @return the resulting XML as a string
     */
    String createXML(Configuration configuration) {
        String output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

        return output;
    }

    /**
     * Export the configuration as a JMeter-compatible XML file.
     * @param configuration the configuration to be used
     */
    void exportXML(Configuration configuration) {
        String xml = createXML(configuration);
        // TODO save as XML file
    }
}
