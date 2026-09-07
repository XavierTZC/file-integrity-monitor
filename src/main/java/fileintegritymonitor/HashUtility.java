package fileintegritymonitor;

import java.security.MessageDigest;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class HashUtility{


    public static String calculateSHA256(String filePath) throws IOException{

        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

        MessageDigest md;

        try{
          md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 is not available in this Java runtime.",e);
        }

        byte[] hashBytes = md.digest(fileBytes);
        StringBuilder hexHash = new StringBuilder();

        for(byte oneByte: hashBytes){
            String toHex = String.format("%02x", oneByte & 0xff);
            hexHash.append(toHex);
        }

        return hexHash.toString();
    }

}
