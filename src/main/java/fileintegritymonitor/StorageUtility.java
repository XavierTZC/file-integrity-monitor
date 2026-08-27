package fileintegritymonitor;

import java.util.ArrayList;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StorageUtility {

    private static final Path STORAGE_FILE = Path.of("monitored-files.txt");
    private static final Logger log = Logger.getLogger(StorageUtility.class.getName());

    public static void saveFiles(ArrayList<MonitoredFile> fileList) throws Exception{

        saveFiles(fileList, STORAGE_FILE);

    }

    static void saveFiles(ArrayList<MonitoredFile> fileList, Path storageFile) throws Exception{

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(storageFile.toFile()))){
            for(MonitoredFile oneFile: fileList){
                String path = oneFile.getFilePath();
                String hash = oneFile.getOriginalHash();

                bw.append(path + "|" + hash);
                bw.newLine();
            }
        }

    }

    public static ArrayList<MonitoredFile> loadFiles(){
        return loadFiles(STORAGE_FILE);
    }

    static ArrayList<MonitoredFile> loadFiles(Path storagePath){
        File storageFile = storagePath.toFile();

        ArrayList<MonitoredFile> fileList = new ArrayList<>();

        if(!storageFile.exists()){
            return fileList;
        }

        try(Scanner sc = new Scanner(storageFile)){

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] parts =line.split("\\|",2);

                if (parts.length == 2){
                    MonitoredFile file = new MonitoredFile(parts[0],parts[1]);
                    fileList.add(file);
                }

            }
            return fileList;
        }catch (FileNotFoundException e){
            log.log(Level.SEVERE, "File not found or created.");
            return fileList;
        }
    }
}
