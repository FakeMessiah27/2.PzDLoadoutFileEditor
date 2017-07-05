package LoadoutFileEditor;

import java.io.File;

class Converter {

    private final Controller controller;

    Converter(Controller controller) {
        this.controller = controller;
    }

    // Starts the conversion. Called from the Controller
    void startConversion(File selectedDirectory) {
        // Run the conversion on the selected directory
        convert(selectedDirectory);

        //Re-enable the browse button
        controller.btnBrowse.setDisable(false);
    }

    // Cycles through the selected folder and edits the files. Uses recursion to go into subfolders.
    private void convert(File folder) {
        File[] files = folder.listFiles();

        if (files != null) {
            ConverterGeneral converterGeneral;

            for (File f : files) {
                String fileName = f.getAbsolutePath();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
                File parent = f.getParentFile();

                if (f.isDirectory()) { // If a directory is found, go into it and run the same method again
                    convert(f);
                }
                else if (extension.equals("sqe")) { // If a file is found, ensure it is a .sqe file and perform the correct conversion on it
                    if (parent.getName().contains("WHR") || parent.getName().contains("FSJ") || parent.getName().contains("DAK")) { // German files
                        converterGeneral = new ConverterGerman(controller);
                        ((ConverterGerman)converterGeneral).replaceStrings(f);
                    }
                    else if (parent.getName().contains("USAB") || parent.getName().contains("US")) { // American files
                        converterGeneral = new ConverterAmerican(controller);
                        ((ConverterAmerican)converterGeneral).replaceStrings(f);
                    }
                    else if (parent.getName().contains("Rus")) { // Russian files
                        converterGeneral = new ConverterRussian(controller);
                        ((ConverterRussian)converterGeneral).replaceStrings(f);
                    }
                    else if (parent.getName().contains("NKVD")) { // NKVD files
                        converterGeneral = new ConverterNKVD(controller);
                        ((ConverterNKVD)converterGeneral).replaceStrings(f);
                    }
                    else if (parent.getName().contains("CdnRWR") || parent.getName().contains("CdnBCR")
                            || parent.getName().contains("UKEsx") || parent.getName().contains("UK")) { // Commonwealth files
                        converterGeneral = new ConverterCommonwealth(controller);
                        ((ConverterCommonwealth)converterGeneral).replaceStrings(f);
                    }
                }
            }
        }
    }
}
