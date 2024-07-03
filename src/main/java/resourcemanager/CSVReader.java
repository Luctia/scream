package resourcemanager;

import resourcemanager.domain.TestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CSVReader {
    public static void main(String[] args) {
        TestResult t = parseCSV("src/test/resources/GET_nginxtest_0.csv");
        System.out.println(t.toReport());
    }

    public static TestResult parseCSV(String filename) {
        try {
            File growlJson = new File(filename);
            Scanner myReader = new Scanner(growlJson);
            StringBuilder fileContents = new StringBuilder();
            while (myReader.hasNextLine()) {
                fileContents.append(myReader.nextLine()).append("\n");
            }
            myReader.close();
            return TestResult.testResultFromCSVLines(fileContents.toString());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            throw new RuntimeException(e);
        }
    }
}
