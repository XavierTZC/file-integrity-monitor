package fileintegritymonitor;

import java.util.Scanner;

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
                               4. Remove file
                               5. Exit
                               """);
            
            System.out.println("Choose our service from (1-5)");
            if (!sc.hasNextInt()) {
                System.out.println("Invalid choice. Please enter a number from 1 to 5.");
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
                                }catch (Exception e){       
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 2 -> fileMonitorManager.listFiles();
                case 3 -> {System.out.println("Enter your file name");
                                String filePath = sc.nextLine();
                                try{
                                    fileMonitorManager.checkFile(filePath);
                                }catch (Exception e){
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 4 -> {System.out.println("Enter your file name");
                                String filePath = sc.nextLine();
                                try{
                                    fileMonitorManager.removeFile(filePath);
                                }catch (Exception e){
                                    System.out.println("Unable to process that file. Check the path and try again.");
                                }
                }
                case 5 -> running = false;
                
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
                    
        }
        
    }
    
}
