package LoadoutFileEditor;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class ConverterNKVD extends ConverterGeneral {

    ConverterNKVD(Controller controller) {
        super(controller);
    }

    void replaceStrings(File file) {
        try {
            file = changeFileToTxt(file);

            // Read all lines
            List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
            boolean altered = false;

            // Runs through all the lines and tries to find certain strings in order to replace them with new ones
            for (int i = 0; i < fileContent.size(); i++) {
                String oldLine = fileContent.get(i);
                String newLine = oldLine;

                // Do not alter already changed strings, recognisable by the @ symbol in them
                if (oldLine.contains("@"))
                    continue;

                // Cases:
                //      Platoon
                //          1st Squad
                //              1st Team
                //              2nd Team
                //          2nd Squad
                //              1st Team
                //              2nd Team
                //          3rd Squad
                //              1st Team
                //              2nd Team
                if (oldLine.contains("1st Squad")) {
                    newLine = changeLine(oldLine,"1st");

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("2nd Squad")) {
                    newLine = changeLine(oldLine,"2nd");

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("3rd Squad")) {
                    newLine = changeLine(oldLine,"3rd");

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("Platoon")) {
                    newLine = changeLine(oldLine);

                    fileContent.set(i, newLine);
                    altered = true;
                }
            }

            // Write changes to file
            Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);

            // If anything was altered, increase the file count and update the label
            if (altered)
                increaseFileCount();

            changeFileToSqe(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Changes line to new Platoon string
    private String changeLine(String line) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@Platoon HQ", role)).toString();
    }

    // Change line to the new squad string
    private String changeLine(String line, String squad) {
        String role = getRole(line, true);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$s Squad", role, squad)).toString();
    }
}
