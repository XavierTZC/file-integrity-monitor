package fileintegritymonitor;

import java.nio.file.Path;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class StorageUtilityTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void saveFiles_oneFile_writesExpectedLine() throws Exception{
        Path storagePath = tempDirectory.resolve("storage.txt");

        ArrayList<MonitoredFile> fileList = new ArrayList<>();
        fileList.add(new MonitoredFile("example123.txt","abc123"));

        StorageUtility.saveFiles(fileList,storagePath);

        List<String> savedLines = Files.readAllLines(storagePath);

        assertEquals(1,savedLines.size());
        assertEquals("example123.txt|abc123",savedLines.get(0));
    }

    @Test
    public void loadFiles_oneStoredLine_returnsExpectedFile() throws Exception{

        Path storagePath = tempDirectory.resolve("storage.txt");

        Files.writeString(storagePath, "example123.txt|abc123" + System.lineSeparator());

        ArrayList<MonitoredFile> loadedFiles = StorageUtility.loadFiles(storagePath);

        assertEquals(1,loadedFiles.size());

        MonitoredFile loadedFile = loadedFiles.get(0);
        assertEquals("example123.txt",loadedFile.getFilePath());
        assertEquals("abc123",loadedFile.getOriginalHash());
    }

    @Test
    public void loadFiles_missingStorageFile_returnsEmptyList() {

        Path missingPath = tempDirectory.resolve("missing.txt");

        ArrayList<MonitoredFile> loadedFiles = StorageUtility.loadFiles(missingPath);
        assertTrue(loadedFiles.isEmpty());
    }

    @Test
    public void saveAndLoadFiles_multipleFiles_preservesAllRecords() throws Exception{

        Path storagePath = tempDirectory.resolve("storage.txt");

        ArrayList<MonitoredFile> fileList = new ArrayList<>();
        fileList.add(new MonitoredFile("first.txt","abc123"));
        fileList.add(new MonitoredFile("second.txt","def456"));

        StorageUtility.saveFiles(fileList,storagePath);

        ArrayList<MonitoredFile> loadedFiles = StorageUtility.loadFiles(storagePath);

        assertEquals(2,loadedFiles.size());

        MonitoredFile firstFile = loadedFiles.get(0);
        MonitoredFile secondFile = loadedFiles.get(1);

        assertEquals("first.txt",firstFile.getFilePath());
        assertEquals("abc123",firstFile.getOriginalHash());
        assertEquals("second.txt",secondFile.getFilePath());
        assertEquals("def456",secondFile.getOriginalHash());
    }
}
