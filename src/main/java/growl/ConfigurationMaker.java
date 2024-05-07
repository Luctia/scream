package growl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import growl.domain.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Used to turn JSON (files) into {@link growl.domain.Configuration} objects.
 * @author Luc Timmerman
 */
public class ConfigurationMaker {
    public static Configuration makeConfiguration(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Configuration.class);
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + json);
            throw new RuntimeException(e);
        }
    }

    /**
     * Take a JSON file and turn it into an {@link growl.domain.Configuration} object.
     * @param filename the name of the file to be read
     * @return a {@link growl.domain.Configuration} object corresponding to the JSON file
     */
    public static Configuration makeConfigurationFromFilename(String filename) {
        try {
            File growlJson = new File(filename);
            Scanner myReader = new Scanner(growlJson);
            StringBuilder jsonString = new StringBuilder();
            while (myReader.hasNextLine()) {
                jsonString.append(myReader.nextLine());
            }
            myReader.close();
            return makeConfiguration(jsonString.toString());
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }
}
