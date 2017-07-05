package LoadoutFileEditor;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class ConverterGerman implements IConversionLanguage {

    private final Controller controller;

    ConverterGerman(Controller controller) {
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
                String newLine;

                // Do not alter already changed strings, recognisable by the @ symbol in them
                if (oldLine.contains("@"))
                    continue;

                // Cases:
                //      Kompanie
                //          Kompanietrupp
                //          Ersatztruppen
                //      Eva (Panzer)
                //      1. Zug
                //          1. Gruppe
                //          2. Gruppe
                //          3. Gruppe
                //      2. Zug
                //          1. Gruppe
                //          2. Gruppe
                //          3. Gruppe
                //      Schwerer Waffentrupp
                //          Mortar
                //          HMG
                //          Panzerschreck
                if (oldLine.contains("1.Kompanie")) {
                    if (oldLine.contains("Ersatztruppen")) {
                        newLine = changeLine(oldLine, true);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else {
                        newLine = changeLine(oldLine, false);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                }
                else if (oldLine.contains("Eva")) {
                    newLine = changeLine(oldLine);
                    fileContent.set(i, newLine);
                    altered = true;
                }
                else if (oldLine.contains("1.Zug")) {
                    if (oldLine.contains("1.Gruppe")) {
                        newLine = changeLine(oldLine, 1, 1);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("2.Gruppe")) {
                        newLine = changeLine(oldLine, 1, 2);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("3.Gruppe")) {
                        newLine = changeLine(oldLine, 1, 3);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else {
                        newLine = changeLine(oldLine, 1);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                }
                else if (oldLine.contains("2.Zug")) {
                    if (oldLine.contains("1.Gruppe")) {
                        newLine = changeLine(oldLine, 2, 1);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("2.Gruppe")) {
                        newLine = changeLine(oldLine, 2, 2);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("3.Gruppe")) {
                        newLine = changeLine(oldLine, 2, 3);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else {
                        newLine = changeLine(oldLine, 2);
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                }
                else if (oldLine.contains("Schwerer Waffentrupp")) {
                    if (oldLine.contains("HMG")) {
                        newLine = changeLine(oldLine, "HMG");
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("Mortar")) {
                        newLine = changeLine(oldLine, "Mortar");
                        fileContent.set(i, newLine);
                        altered = true;
                    }
                    else if (oldLine.contains("Panzerschreck")) {
                        newLine = changeLine(oldLine, "Panzerschreck");
                        fileContent.set(i, newLine);
                        altered = true;
                    }
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

    // Changes line to new Kompanie string. Will either create Kompanietrupp or Ersatztruppen, depending on the boolean parameter
    private String changeLine(String line, boolean isErsatz) {
        String role = getRole(line);
        if (isErsatz)
            return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                    String.format("\"%1$s@1. Kompanie | Ersatztruppen", role)).toString();
        else
            return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                    String.format("\"%1$s@1. Kompanie | Kompanietrupp", role)).toString();
    }

    // Changes line to new Zugtrupp string
    private String changeLine(String line, int zug) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$d. Zug | Zugtrupp", role, zug)).toString();
    }

    // Changes line to new Gruppe string
    private String changeLine(String line, int zug, int gruppe) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@%2$d. Zug | %3$d. Gruppe", role, zug, gruppe)).toString();
    }

    // Changes line to new Schwerer Waffentrupp string
    private String changeLine(String line, String swtType) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@Schwerer Waffentrupp | %2$s", role, swtType)).toString();
    }

    // Changes line to new Eva (Panzer) string
    private String changeLine(String line) {
        String role = getRole(line);
        return new StringBuilder(line).replace(line.indexOf("\""), line.lastIndexOf("\""),
                String.format("\"%1$s@Eva", role)).toString();
    }

    // Extracts the role of the soldier that the line belongs to and returns it
    private String getRole(String line) {
        String reverseOldLine = new StringBuilder(line).reverse().toString();
        String reserveRole = reverseOldLine.substring(2, reverseOldLine.indexOf("|") - 1);
        return new StringBuilder(reserveRole).reverse().toString();
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
