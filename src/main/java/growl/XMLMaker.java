package growl;

import growl.domain.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Scanner;

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
    static void exportXML(Configuration configuration, boolean prettyPrint) {
        String xml = createTestplanXML(configuration);
        if (prettyPrint) xml = prettyPrintByTransformer(xml);
        try {
            PrintWriter writer = new PrintWriter("config.jmx");
            writer.print(xml);
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not create config.jmx file");
        }
    }

    /**
     * Turns "ugly" XML string into pretty XML string.
     * @param xmlString string to be transformed
     * @return given XML with indents and other readability improvements
     * @see <a href="https://www.baeldung.com/java-pretty-print-xml">By Kai Yuan on Baeldung.</a>
     */
    private static String prettyPrintByTransformer(String xmlString) {
        try {
            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);

            File xslFile = new File("src/main/resources/prettyprint.xsl");
            Scanner reader = new Scanner(xslFile);
            StringBuilder xslt = new StringBuilder();
            while (reader.hasNextLine()) {
                xslt.append(reader.nextLine());
            }
            reader.close();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xslt.toString())));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }
}
