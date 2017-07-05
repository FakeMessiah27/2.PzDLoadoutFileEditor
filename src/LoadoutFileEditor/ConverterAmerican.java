package LoadoutFileEditor;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConverterAmerican implements IConversionLanguage {

    private final Controller controller;

    ConverterAmerican(Controller controller) {
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
                //      Company HQ
                //      1 Platoon
                //          1 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //          2 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //          3 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //      2 Platoon
                //          1 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //          2 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //          3 Squad
                //              Alpha Team
                //              Bravo Team
                //              Charlie Team
                //      Machine Gun Team
                //      Mortar Team
                //      Tank
                if (oldLine.contains("Company")) {
                    newLine = changeLine(oldLine, false);
                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("1 Platoon")) {
                    if (oldLine.contains("1 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 1, 1, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 1, 1, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 1, 1, "Charlie");
                        else
                            newLine = changeLine(oldLine, 1, 1);
                    }
                    else if (oldLine.contains("2 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 1, 2, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 1, 2, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 1, 2, "Charlie");
                        else
                            newLine = changeLine(oldLine, 1, 2);
                    }
                    else if (oldLine.contains("3 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 1, 3, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 1, 3, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 1, 3, "Charlie");
                        else
                            newLine = changeLine(oldLine, 1, 3);
                    }
                    else
                        newLine = changeLine(oldLine, 1);

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("2 Platoon")) {
                    if (oldLine.contains("1 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 2, 1, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 2, 1, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 2, 1, "Charlie");
                        else
                            newLine = changeLine(oldLine, 2, 1);
                    }
                    else if (oldLine.contains("2 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 2, 2, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 2, 2, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 2, 2, "Charlie");
                        else
                            newLine = changeLine(oldLine, 2, 2);
                    }
                    else if (oldLine.contains("3 Squad")) {
                        if (oldLine.contains("Alpha"))
                            newLine = changeLine(oldLine, 2, 3, "Alpha");
                        else if (oldLine.contains("Bravo"))
                            newLine = changeLine(oldLine, 2, 3, "Bravo");
                        else if (oldLine.contains("Charlie"))
                            newLine = changeLine(oldLine, 2, 3, "Charlie");
                        else
                            newLine = changeLine(oldLine, 2, 3);
                    }
                    else
                        newLine = changeLine(oldLine, 2);

                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("Machine Gun Team")) {
                    newLine = changeLine(oldLine, "Machine Gun Team");
                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("Mortar Team")) {
                    newLine = changeLine(oldLine, "Mortar Team");
                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("Tank")) {
                    newLine = changeLine(oldLine, true);
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

    // Changes line to new Company HQ or Tank string
    private String changeLine(String line, boolean isTank) {
        String role = getRole(line);

        if (!isTank) {
            return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                    String.format("\"%1$s@Company HQ", role)).toString();
        }
        else {
            return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                    String.format("\"%1$s@Tank", role)).toString();
        }
    }

    // Changes line to new Platoon string
    private String changeLine(String line, int platoon) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$d Platoon | Platoon HQ", role, platoon)).toString();
    }

    // Changes line to new Squad (with team) string
    private String changeLine(String line, int platoon, int squad, String team) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$d Platoon | %3$d Squad | %4$s Team", role, platoon, squad, team)).toString();
    }

    // Change line to the new squad string
    private String changeLine(String line, int platoon, int squad) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$d Platoon | %3$d Squad", role, platoon, squad)).toString();
    }

    // Change line to new heavy weapons team string
    private String changeLine(String line, String teamType) {
        String role = getRole(line);

        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$s", role, teamType)).toString();
    }

    // Extracts the role of the soldier that the line belongs to and returns it
    private String getRole(String line) {
        if (line.contains("|")) {
            String reverseOldLine = new StringBuilder(line).reverse().toString();
            String reserveRole = reverseOldLine.substring(2, reverseOldLine.indexOf("|") - 1);
            return new StringBuilder(reserveRole).reverse().toString();
        }
        else {
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