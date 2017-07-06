package LoadoutFileEditor;

import javafx.application.Platform;

import java.io.File;

class ConverterGeneral {

    private final Controller controller;

    ConverterGeneral(Controller controller) {
        this.controller = controller;
    }

    // Changes a file's extension to .txt
    File changeFileToTxt(File file) {
        String fileName = file.getAbsolutePath(); // Gets the absolute path of the file, required for renaming to .txt in order to be edited
        int dotIndex = fileName.indexOf(".");
        String extension = fileName.substring(dotIndex);

        if (!extension.equals(".sqe")) // Only use .sqe files
            return null;

        // Change extension to .txt
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        boolean fileNameChanged = file.renameTo(new File(fileNameWithoutExtension + ".txt"));

        if (!fileNameChanged)
            return null;

        file = new File(fileNameWithoutExtension + ".txt");
        return file;
    }

    // Changes a file's extension to .sqe
    void changeFileToSqe(File file) {
        String fileName = file.getAbsolutePath();
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));

        file.renameTo(new File(fileNameWithoutExtension + ".sqe"));
    }

    // Extracts the role of the soldier that the line belongs to and returns it
    String getRole(String line) {
        if (line.contains("|")) {
            String reverseOldLine = new StringBuilder(line).reverse().toString();
            String reserveRole = reverseOldLine.substring(2, reverseOldLine.indexOf("|") - 1);
            return new StringBuilder(reserveRole).reverse().toString();
        }
        else {
            return new StringBuilder(line).substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        }
    }

    // Same as the above, but for nations who use teams inside of a squad
    String getRole(String line, boolean hasTeam) {
        if (line.contains("|")) {
            String reverseOldLine = new StringBuilder(line).reverse().toString();
            String reserveRole = reverseOldLine.substring(2, reverseOldLine.indexOf("|", reverseOldLine.indexOf("|") + 1) - 1);
            return new StringBuilder(reserveRole).reverse().toString();
        }
        else {
            return new StringBuilder(line).substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        }
    }

    // Increases the file counter in the UI
    void increaseFileCount() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.increaseFileCount();
            }
        });
    }
}
