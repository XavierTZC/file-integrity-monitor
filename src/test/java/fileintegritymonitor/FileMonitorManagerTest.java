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

    @Test
    public void listFiles_emptyList_printsEmptyMessage(){

        Path storagePath = tempDirectory.resolve("storage.txt");
        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try{
            System.setOut(new PrintStream(capturedOutput));
            manager.listFiles();
        }finally{
            System.setOut(originalOutput);
        }

        assertEquals("There is nothing in the list." + System.lineSeparator(),capturedOutput.toString());
    }

    @Test
    public void listFiles_oneFile_printsMonitoredFile() throws Exception{

        Path storagePath = tempDirectory.resolve("storage.txt");
        Path monitoredPath = tempDirectory.resolve("example.txt");

        Files.writeString(monitoredPath, "Hello World!");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        String expectedHash = HashUtility.calculateSHA256(monitoredPath.toString());

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try{
            System.setOut(new PrintStream(capturedOutput));
            manager.listFiles();
        }finally{
            System.setOut(originalOutput);
        }

        String expectedOutput = "MonitoredFile{filePath=" + monitoredPath
                                + ", originalHash=" + expectedHash + "}"
                                + System.lineSeparator();

        assertEquals(expectedOutput, capturedOutput.toString());
    }

    @Test
    public void checkAllFiles_emptyList_printsNoFilesMessage(){

        Path storagePath = tempDirectory.resolve("storage.txt");
        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try{
            System.setOut(new PrintStream(capturedOutput));
            manager.checkAllFiles();
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput = "There are no monitored files." + System.lineSeparator();

        assertEquals(expectedOutput,capturedOutput.toString());
    }

    @Test
    public void checkAllFiles_mixedFiles_printsEachStatus() throws Exception{

        Path unchangedPath = tempDirectory.resolve("unchanged.txt");
        Path modifiedPath = tempDirectory.resolve("modified.txt");
        Path missingPath = tempDirectory.resolve("missing.txt");

        Files.writeString(unchangedPath, "unchanged content");
        Files.writeString(modifiedPath, "original content");
        Files.writeString(missingPath, "temporary content");

        Path storagePath = tempDirectory.resolve("storage.txt");
        FileMonitorManager manager = new FileMonitorManager(storagePath);

        manager.addFile(unchangedPath.toString());
        manager.addFile(modifiedPath.toString());
        manager.addFile(missingPath.toString());

        Files.writeString(modifiedPath,"modified content.");
        Files.delete(missingPath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.checkAllFiles();
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput =
                    unchangedPath + " is unchanged." + System.lineSeparator()
                    + modifiedPath + " has been modified." + System.lineSeparator()
                    + missingPath + " is missing or inaccessible." + System.lineSeparator();

        assertEquals(expectedOutput, capturedOutput.toString());
    }

    @Test
    public void removeFile_unmonitoredFile_printsNotFoundMessage() {

        Path unmonitoredPath = tempDirectory.resolve("unmonitored.txt");
        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.removeFile(unmonitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput = "File is not found in the list." + System.lineSeparator();

        assertEquals(expectedOutput,capturedOutput.toString());
    }

    @Test
    public void updateFileHash_unmonitoredFile_printsNotMonitoredMessage() throws Exception {
        Path unmonitoredPath = tempDirectory.resolve("unmonitored.txt");
        Path storagePath = tempDirectory.resolve("storage.txt");

        FileMonitorManager manager = new FileMonitorManager(storagePath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.updateFileHash(unmonitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput = "The file is not in the monitored list." + System.lineSeparator();

        assertEquals(expectedOutput,capturedOutput.toString());
    }

    @Test
    public void checkFile_monitoredFileDeleted_printsMissingMessage() throws Exception{
        Path monitoredPath = tempDirectory.resolve("monitored.txt");
        Path storagePath = tempDirectory.resolve("storage.txt");

        Files.writeString(monitoredPath, "Hello World!");

        FileMonitorManager manager = new FileMonitorManager(storagePath);
        manager.addFile(monitoredPath.toString());

        Files.delete(monitoredPath);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(capturedOutput));
            manager.checkFile(monitoredPath.toString());
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput =  "The file is missing or inaccessible." + System.lineSeparator();

        assertEquals(expectedOutput,capturedOutput.toString());
    }
}
