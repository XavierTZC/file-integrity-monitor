package fileintegritymonitor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Files;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class FileMonitorManagerTest {
    // Add , List , Check , Update , Remove

    @TempDir
    Path tempDirectory;

    @Test
    public void addFile_validFile_persistsBaseline() throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        ArrayList<MonitoredFile> savedFiles = StorageUtility.loadFiles(storagePath);

        assertEquals(1, savedFiles.size());

        MonitoredFile firstFile = savedFiles.get(0);
        assertEquals(monitoredPath.toString(), firstFile.getFilePath());
        assertEquals(HashUtility.calculateSHA256(monitoredPath.toString()), firstFile.getOriginalHash());
    }

    @Test
    public void addFile_samePathTwice_preservesOriginalBaseline()
            throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        String originalHash
                = HashUtility.calculateSHA256(monitoredPath.toString());

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        Files.writeString(monitoredPath, "HelloWorld!");

        String modifiedHash
                = HashUtility.calculateSHA256(monitoredPath.toString());

        manager.addFile(monitoredPath.toString());

        ArrayList<MonitoredFile> savedFiles
                = StorageUtility.loadFiles(storagePath);

        assertEquals(1, savedFiles.size());

        MonitoredFile firstFile = savedFiles.get(0);

        assertEquals(monitoredPath.toString(), firstFile.getFilePath());
        assertEquals(originalHash, firstFile.getOriginalHash());
        assertNotEquals(modifiedHash, firstFile.getOriginalHash());
    }

    @Test
    public void removeFile_existingFile_removesPersistedRecord() throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        ArrayList<MonitoredFile> savedFiles = StorageUtility.loadFiles(storagePath);

        manager.removeFile(monitoredPath.toString());

        ArrayList<MonitoredFile> updatedSavedFiles = StorageUtility.loadFiles(storagePath);

        assertEquals(1, savedFiles.size());
        assertTrue(updatedSavedFiles.isEmpty());
    }

    @Test
    public void updateFileHash_modifiedFile_persistsNewBaseline()
            throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        String originalHash = HashUtility.calculateSHA256(monitoredPath.toString());

        ArrayList<MonitoredFile> savedFiles = StorageUtility.loadFiles(storagePath);

        Files.writeString(monitoredPath, "modified Hello World!");

        String newHash = HashUtility.calculateSHA256(monitoredPath.toString());

        manager.updateFileHash(monitoredPath.toString());

        ArrayList<MonitoredFile> updatedSavedFiles = StorageUtility.loadFiles(storagePath);

        MonitoredFile updatedFile = updatedSavedFiles.get(0);

        assertEquals(1, savedFiles.size());
        assertEquals(1, updatedSavedFiles.size());

        assertEquals(originalHash, savedFiles.get(0).getOriginalHash());
        assertEquals(newHash, updatedFile.getOriginalHash());
        assertNotEquals(originalHash, updatedFile.getOriginalHash());
    }

    @Test
    public void check_File_unchangedFile_printsUnchangedMessage() throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.checkFile(monitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("The file is unchanged." + System.lineSeparator(), capturedOutput.toString());
    }

    @Test
    public void checkFile_modifiedFile_printsModifiedMessage() throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        Files.writeString(monitoredPath, "Modified Hello World!");

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.checkFile(monitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("The file has been modified." + System.lineSeparator(), capturedOutput.toString());
    }

    @Test
    public void checkFile_unmonitoredFile_printsNotMonitoredMessage() throws Exception {

        Path monitoredPath = tempDirectory.resolve("example.txt");
        Files.writeString(monitoredPath, "Hello World!");

        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {

            System.setOut(new PrintStream(capturedOutput));
            manager.checkFile(monitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("This file is not being monitored." + System.lineSeparator(), capturedOutput.toString());
    }

    @Test
    public void checkFile_missingUnmonitoredFile_printsNotMonitoredMessage() throws Exception {

        Path missingPath = tempDirectory.resolve("missing.txt");
        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {

            System.setOut(new PrintStream(capturedOutput));
            manager.checkFile(missingPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("This file is not being monitored." + System.lineSeparator(), capturedOutput.toString());
    }
}
