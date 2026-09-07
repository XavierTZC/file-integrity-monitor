package fileintegritymonitor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.io.IOException;

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

    public void addFile(String filePath) throws IOException{
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

    public IntegrityStatus checkFile(String filePath){

        for (MonitoredFile oneFile: fileList){

            if(oneFile.getFilePath().equals(filePath)){
                return determineIntegrityStatus(oneFile);
            }
        }

        return IntegrityStatus.NOT_MONITORED;
    }

    public void removeFile(String filePath){

        for(int i = 0; i < fileList.size(); i++){

            MonitoredFile oneFile = fileList.get(i);

            if(oneFile.getFilePath().equals(filePath)){
                fileList.remove(i);

                try{
                    StorageUtility.saveFiles(fileList,storagePath);
                }catch (IOException e){
                    System.out.println("File was removed in memory, but changes could not be saved.");
                }

                System.out.println("File removed from monitoring.");
                return;
            }
        }

        System.out.println("File is not found in the list.");
    }

    public ArrayList<IntegrityCheckResult> checkAllFiles() {

        ArrayList<IntegrityCheckResult> results = new ArrayList<>();

        for(MonitoredFile oneFile: fileList){
            IntegrityStatus status = determineIntegrityStatus(oneFile);

            IntegrityCheckResult result = new IntegrityCheckResult(oneFile.getFilePath(),status);

            results.add(result);
        }

        return results;
    }

    public void updateFileHash(String filePath) throws IOException{

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

    private IntegrityStatus determineIntegrityStatus(MonitoredFile monitoredFile){

        try{
            String currentHash = HashUtility.calculateSHA256(monitoredFile.getFilePath());

            if(monitoredFile.getOriginalHash().equals(currentHash)){
                return IntegrityStatus.UNCHANGED;
            } else{
                return IntegrityStatus.MODIFIED;
            }
        } catch (IOException e){
            return IntegrityStatus.MISSING_OR_INACCESSIBLE;
        }
    }
}

