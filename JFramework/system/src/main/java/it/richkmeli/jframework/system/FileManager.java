package it.richkmeli.jframework.system;

import it.richkmeli.jframework.util.log.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileManager {
    public static String loadDataFromFile(String path) {
        return loadDataFromFile(new File(path));
    }

    public static String loadDataFromFile(File file) {
        if (file != null) {
            if (file.exists()) {

                Scanner sc = null;
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException e) {
                    Logger.error("file '" + file.getName() + "' not found", e);
                    return null;
                }

                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }
                return sb.toString();

            } else {
                try {
                    if (file.createNewFile()) {
                        Logger.info("file '" + file.getName() + "' created");
                    }else {
                        // if the named file already exists
                        Logger.info("file '" + file.getName() + "' already exists");
                    }
                    return "";
                } catch (IOException e) {
                    Logger.error("file '" + file.getName() + "' creation error.", e);
                    return null;
                }
            }
        } else {
            Logger.error("file is null");
            return null;
        }
    }

    public static boolean saveDataToFile(String path, String input) {
        return saveDataToFile(new File(path), input);
    }

        public static boolean saveDataToFile(File file, String input) {
        if (file.exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file, false);
                fileWriter.write(input);
                fileWriter.close();
                return true;
            } catch (IOException e) {
                Logger.error("Error writing file '" + file.getName() + "' to file", e);
                return false;
            }

//            Path path = Paths.get(file.getPath());
//            byte[] strToBytes = file.getBytes();
//
//            try {
//                Files.write(path, strToBytes);
//            } catch (IOException e) {
//                Logger.error("Error writing SecureData to file", e);
//                return false;
//            }
//            //Logger.info("SecureData set");
//            return true;
        } else {
            try {
                if (file.createNewFile()) {
                    Logger.info("file '" + file.getName() + "' created");
                    return saveDataToFile(file,input);
                }else {
                    // if the named file already exists
                    return false;
                }
            } catch (IOException e) {
                Logger.error("file '" + file.getName() + "' creation error.", e);
                return false;
            }
        }

    }
}
