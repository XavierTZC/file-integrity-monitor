package fileintegritymonitor;

import java.security.MessageDigest;
import java.nio.file.Paths;
import java.nio.file.Files;

public class HashUtility{
    
    
    public static String calculateSHA256(String filePath) throws Exception{
        
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        byte[] hashBytes = md.digest(fileBytes);
        StringBuilder hexHash = new StringBuilder();
        
        for(byte oneByte: hashBytes){
            String toHex = String.format("%02x", oneByte & 0xff);
            hexHash.append(toHex);
        }
        
        return hexHash.toString();        
    }
    
}
