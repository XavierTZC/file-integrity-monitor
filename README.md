# File Integrity Monitor

A Java console application that detects whether monitored files have changed by comparing SHA-256 hashes.

## Features

- Add a file to the monitored list.
- Calculate and store the file's original SHA-256 hash.
- Check whether a monitored file has been modified.
- List and remove monitored files.
- Persist monitored file paths and hashes between application restarts.
- Handle invalid menu input and inaccessible file paths with user-friendly messages.

## How it works

When a file is added, the application calculates its SHA-256 hash and stores it as the original hash. During an integrity check, the application calculates a new hash for the same file. Matching hashes mean the file is unchanged; different hashes mean it has been modified.

## Project structure

| Class | Responsibility |
| --- | --- |
| `FileIntegrityMonitor` | Console menu and user input |
| `FileMonitorManager` | Add, list, check, and remove monitored files |
| `MonitoredFile` | Stores a file path and its original hash |
| `HashUtility` | Calculates SHA-256 file hashes |
| `StorageUtility` | Saves and reloads monitored files locally |

## Requirements

- Java 25 (the project is configured for Java 25)
- Apache NetBeans, or another Java IDE that supports Ant projects

## Running the project

1. Clone or download this repository.
2. Open the project in Apache NetBeans.
3. Run `FileIntegrityMonitor.java`.
4. Select an option from the console menu and enter a path to a file you own or are authorized to monitor.

## Notes

- `monitored-files.txt` is created automatically to store local monitoring data and is intentionally excluded from Git.
- This is an educational integrity-monitoring project. It reports changes; it does not prevent, repair, or remove them.
