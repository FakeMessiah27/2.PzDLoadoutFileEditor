package LoadoutFileEditor;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConverterNKVD implements IConversionLanguage {

    private final Controller controller;

    ConverterNKVD(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void replaceStrings(File file) {
        try {
            String fileName = file.getAbsolutePath(); // Gets the absolute path of the file, required for renaming to .txt in order to be edited
            int dotIndex = fileName.indexOf(".");
            String extension = fileName.substring(dotIndex);

            if (!extension.equals(".sqe")) // Only use .sqe files
                return;

            // Change extension to .txt
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
            boolean fileNameChanged = file.renameTo(new File(fileNameWithoutExtension + ".txt"));
            if (!fileNameChanged)
                return;

            file = new File(fileNameWithoutExtension + ".txt");

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
                    if (oldLine.contains("1st Team"))
                        newLine = changeLine(oldLine,"1st", "1st");
                    else if (oldLine.contains("2nd Team"))
                        newLine = changeLine(oldLine,"1st", "2nd");
                    else
                        newLine = changeLine(oldLine,"1st");

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("2nd Squad")) {
                    if (oldLine.contains("1st Team"))
                        newLine = changeLine(oldLine,"2nd", "1st");
                    else if (oldLine.contains("2nd Team"))
                        newLine = changeLine(oldLine,"2nd", "2nd");
                    else
                        newLine = changeLine(oldLine,"2nd");

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("3rd Squad")) {
                    if (oldLine.contains("1st Team"))
                        newLine = changeLine(oldLine,"3rd", "1st");
                    else if (oldLine.contains("2nd Team"))
                        newLine = changeLine(oldLine,"3rd", "2nd");
                    else
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

            // Change file extension back to .sqe
            fileName = file.getAbsolutePath(); // Gets the absolute path of the file, required for renaming to .txt in order to be edited
            dotIndex = fileName.indexOf(".");
            extension = fileName.substring(dotIndex);

            // Change extension to .txt
            fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
            file.renameTo(new File(fileNameWithoutExtension + ".sqe"));

            file = new File(fileNameWithoutExtension + ".sqe");
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

    // Changes line to new Squad (with team) string
    private String changeLine(String line, String squad, String team) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$s Squad | %3$s Team", role, squad, team)).toString();
    }

    // Change line to the new squad string
    private String changeLine(String line, String squad) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$s Squad", role, squad)).toString();
    }

    // Extracts the role of the soldier that the line belongs to and returns it
    private String getRole(String line) {
        if (line.contains("|")) {
            String reverseOldLine = new StringBuilder(line).reverse().toString();
            String reserveRole = reverseOldLine.substring(2, reverseOldLine.indexOf("|") - 1);
            return new StringBuilder(reserveRole).reverse().toString();
        } else {
            return new StringBuilder(line).substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        }
    }

    // Increases the file counter in the UI
    private void increaseFileCount() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.increaseFileCount();
            }
        });
    }
}
