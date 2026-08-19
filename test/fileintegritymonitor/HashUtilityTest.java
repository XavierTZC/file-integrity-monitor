
package fileintegritymonitor;

import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;


public class HashUtilityTest {
    
    
    @Test
    public void calculateSHA256_emptyFile_returnsExpectedHash() throws Exception {
        Path testFile = Files.createTempFile("empty-file-", ".txt");

        try {
            String expectedHash =
                    "e3b0c44298fc1c149afbf4c8996fb924"
                  + "27ae41e4649b934ca495991b7852b855";

            String actualHash =
                    HashUtility.calculateSHA256(testFile.toString());

            assertEquals(expectedHash, actualHash);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
    
    @Test
    public void calculateSHA256_helloWorldFile_returnsExpectedHash() throws Exception{
        
        Path testFile = Files.createTempFile("hello-world-file",".txt");
        
        try{
            Files.writeString(testFile, "Hello World!");
            
            String expectedHash =
                    "7f83b1657ff1fc53b92dc18148a1d65d"
                  + "fc2d4b1fa3d677284addd200126d9069";
                    
            String actualHash = HashUtility.calculateSHA256(testFile.toString());
            
            assertEquals(expectedHash, actualHash);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
    
    @Test
    public void calculateSHA256_firstHashFile_isDifferentFrom_changedFirstHashFile() throws Exception{
        
        Path testFile = Files.createTempFile("test-file",".txt");
        
        try{
            
            Files.writeString(testFile,"Hello World!");
            String firstHash = HashUtility.calculateSHA256(testFile.toString());
            Files.writeString(testFile,"Hello World 2!");
            String secondHash = HashUtility.calculateSHA256(testFile.toString());
            
            assertNotEquals(firstHash,secondHash);
            
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
    
    @Test
    public void calculateSHA256_missingFile_throwsException() throws Exception{
        
        Path missingFile = Files.createTempFile("missing-file",".txt");
        Files.delete(missingFile);
        
        assertThrows(
                NoSuchFileException.class,
                () -> HashUtility.calculateSHA256(missingFile.toString())
        );

    }
}
