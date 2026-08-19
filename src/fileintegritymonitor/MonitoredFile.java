package fileintegritymonitor;

public class MonitoredFile {
    private String filePath;
    private String originalHash;
    
    public MonitoredFile(){
        
    }
    
    public MonitoredFile(String filePath, String originalHash){
        this.filePath = filePath;
        this.originalHash = originalHash;
    }
    
    public String getFilePath(){
        return filePath;
    }
    
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getOriginalHash() {
        return originalHash;
    }

    public void setOriginalHash(String originalHash) {
        this.originalHash = originalHash;
    }
    
    public void displayInfo(){
        System.out.println("Current FilePath= " + filePath);
        System.out.println("Current OriginalHash= " + originalHash);
    }
    
    @Override
    public String toString() {
        return "MonitoredFile{" + "filePath=" + filePath + ", originalHash=" + originalHash + '}';
    }
    
}
