package LoadoutFileEditor;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

import java.io.*;

public class Controller {
    // UI elements, from fxml file
    public Label lbFileCount;
    public Button btnBrowse;

    // Private variables
    private String lastPath;
    private Converter converter;

    // Called when Browse button is pressed
    public void openBrowseWindow(ActionEvent actionEvent) {
        btnBrowse.setDisable(true);

        // Open directory chooser to select target directory
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        if (lastPath != null) // If the directory was used before, save the last path to open at the same place
            directoryChooser.setInitialDirectory(new File(lastPath));

        final File selectedDirectory = directoryChooser.showDialog(lbFileCount.getScene().getWindow());

        if (selectedDirectory == null) {
            btnBrowse.setDisable(false);
            return;
        }

        // Reset File count counter
        lbFileCount.setText("0");

        // Set last path to the currently found path to allow for easier access when the directory chooser is used again
        lastPath = selectedDirectory.getParentFile().getAbsolutePath();

        // Initialise the converter and start the conversion in a new thread
        converter = new Converter(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                converter.startConversion(selectedDirectory);
            }
        }).start();
    }

    // Gets the current number of the file count label and increases it by 1
    void increaseFileCount() {
        int currentFileCount = Integer.valueOf(lbFileCount.getText());
        lbFileCount.setText(String.valueOf(currentFileCount + 1));
    }
}

