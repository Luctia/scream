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
import java.util.Objects;
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
    public static String createTestplanXML(Configuration configuration, boolean prettyPrint) {
        String uglyXML = String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
                %s
                </jmeterTestPlan>
                """, configuration.toXML());
        return prettyPrint ? prettyPrintByTransformer(uglyXML) : uglyXML;
    }

    /**
     * Export the configuration as a JMeter-compatible Testplan XML file.
     * @param configuration the configuration to be used
     */
    public static void exportXML(Configuration configuration, boolean prettyPrint) {
        String xml = createTestplanXML(configuration, prettyPrint);
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

            Scanner reader = new Scanner(Objects.requireNonNull(XMLMaker.class.getClassLoader().getResourceAsStream("prettyprint.xsl")));
            StringBuilder xslt = new StringBuilder();
            while (reader.hasNextLine()) {
                xslt.append(reader.nextLine());
            }
            // dException: nested:/jmeter/scream-0.0.1-SNAPSHOT.jar/!BOOT-INF/classes/!/prettyprint.xsl (No such file or
            reader.close();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xslt.toString())));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString().replaceAll("\r\n", "\n");
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }
}
