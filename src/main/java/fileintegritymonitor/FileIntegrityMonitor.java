package fileintegritymonitor;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.InvalidPathException;
public class FileIntegrityMonitor {


    public static void main(String[] args) {

        FileMonitorManager fileMonitorManager = new FileMonitorManager();
        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while(running){

            System.out.println("""
                               1. Add file
                               2. List monitored files
                               3. Check file integrity
                               4. Check all files integrity
                               5. Update file baseline
                               6. Remove file
                               7. Exit
                               """);

            System.out.println("Choose our service from (1-7)");
            if (!sc.hasNextInt()) {
                System.out.println("Invalid choice. Please enter a number from 1 to 7.");
                sc.nextLine();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch(choice){
                case 1 -> {System.out.println("Enter your file name");
                                String filePath = sc.nextLine();
                                try{
                                    fileMonitorManager.addFile(filePath);
                                }catch (IOException e){
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 2 -> fileMonitorManager.listFiles();
                case 3 -> {
                    System.out.println("Enter your file name");
                    String filePath = sc.nextLine();

                    IntegrityStatus status =
                            fileMonitorManager.checkFile(filePath);

                    String message = switch (status) {
                        case UNCHANGED ->
                            "The file is unchanged.";
                        case MODIFIED ->
                            "The file has been modified.";
                        case NOT_MONITORED ->
                            "This file is not being monitored.";
                        case MISSING_OR_INACCESSIBLE ->
                            "The file is missing or inaccessible.";
                    };

                    System.out.println(message);
                }
                case 4 -> {ArrayList<IntegrityCheckResult> results = fileMonitorManager.checkAllFiles();
                                if (results.isEmpty()){
                                    System.out.println("There are no monitored files.");
                                } else {
                                    for (IntegrityCheckResult result : results){
                                        String message = switch (result.status()){
                                            case UNCHANGED -> result.filePath() + " is unchanged.";
                                            case MODIFIED -> result.filePath() + " has been modified.";
                                            case MISSING_OR_INACCESSIBLE -> result.filePath() + " is missing or inaccessible.";
                                            case NOT_MONITORED -> result.filePath() + " is not being monitored.";
                                        };
                                        System.out.println(message);
                                    }
                                }
                }
                case 5 -> {System.out.println("Enter your file name");
                                String filePath = sc.nextLine();
                                try{
                                    fileMonitorManager.updateFileHash(filePath);
                                }catch (IOException e){
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 6 -> {System.out.println("Enter your file name");
                                String filePath = sc.nextLine();
                                try{
                                    fileMonitorManager.removeFile(filePath);
                                }catch (InvalidPathException e){
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 7 ->  {System.out.println("Exiting File Integrity Monitor.");
                           running = false;}

                default -> System.out.println("Invalid choice. Please enter a number from 1 to 7.");
            }

        }

    }

}
