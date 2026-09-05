package fileintegritymonitor;

import java.nio.file.Path;
import java.util.ArrayList;

public class FileMonitorManager {

    private final ArrayList<MonitoredFile> fileList;
    private final Path storagePath;

    public FileMonitorManager() {
        this(StorageUtility.STORAGE_FILE);
    }

    FileMonitorManager(Path storagePath) {
        this.storagePath = storagePath;
        this.fileList = StorageUtility.loadFiles(storagePath);
    }

    public void addFile(String filePath) throws Exception{
        for (MonitoredFile oneFile : fileList) {
            if (oneFile.getFilePath().equals(filePath)) {
                System.out.println("This file is already being monitored.");
                return;
            }
        }

        String hash = HashUtility.calculateSHA256(filePath);
        MonitoredFile mf = new MonitoredFile(filePath, hash);

        fileList.add(mf);
        StorageUtility.saveFiles(fileList,storagePath);
    }

    public void listFiles(){

        if(!fileList.isEmpty()){

            for(MonitoredFile oneFile: fileList){
                System.out.println(oneFile.toString());
            }
        }else{
            System.out.println("There is nothing in the list.");
        }
    }

    public void checkFile(String filePath) throws Exception{

        for (MonitoredFile oneFile: fileList){

            if(oneFile.getFilePath().equals(filePath)){

                try{
                    String hash = HashUtility.calculateSHA256(filePath);

                    if(oneFile.getOriginalHash().equals(hash)){
                        System.out.println("The file is unchanged.");
                        return;
                    }else{
                        System.out.println("The file has been modified.");
                        return;
                    }
                }catch (Exception e){
                    System.out.println("The file is missing or inaccessible.");
                }

                return;
            }
        }
        System.out.println("This file is not being monitored.");
    }

    public void removeFile(String filePath){

        for(int i = 0; i < fileList.size(); i++){

            MonitoredFile oneFile = fileList.get(i);

            if(oneFile.getFilePath().equals(filePath)){
                fileList.remove(i);

                try{
                    StorageUtility.saveFiles(fileList,storagePath);
                }catch (Exception e){
                    System.out.println("File was removed in memory, but changes could not be saved.");
                }

                System.out.println("File removed from monitoring.");
                return;
            }
        }

        System.out.println("File is not found in the list.");
    }

    public void checkAllFiles() {
        if (fileList.isEmpty()) {
            System.out.println("There are no monitored files.");
            return;
        }

        for (MonitoredFile oneFile : fileList) {
            String filePath = oneFile.getFilePath();

            try {
                String hash = HashUtility.calculateSHA256(filePath);

                if (oneFile.getOriginalHash().equals(hash)) {
                    System.out.println(filePath + " is unchanged.");
                } else {
                    System.out.println(filePath + " has been modified.");
                }

            } catch (Exception e) {
                System.out.println(filePath + " is missing or inaccessible.");
            }
        }
    }

    public void updateFileHash(String filePath) throws Exception{

        for (MonitoredFile oneFile : fileList){

            if(oneFile.getFilePath().equals(filePath)){

                String hash = HashUtility.calculateSHA256(filePath);
                oneFile.setOriginalHash(hash);
                StorageUtility.saveFiles(fileList,storagePath);

                System.out.println("The file has been updated for " + filePath + ".");
                return;
            }
        }

        System.out.println("The file is not in the monitored list.");
    }
}

