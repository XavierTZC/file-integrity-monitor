# File Integrity Monitor

[![Maven CI](https://github.com/XavierTZC/file-integrity-monitor/actions/workflows/maven.yml/badge.svg)](https://github.com/XavierTZC/file-integrity-monitor/actions/workflows/maven.yml)

A Java console application that detects whether monitored files have changed by comparing SHA-256 hashes.

## Features

- Add files to the monitored list.
- Calculate and store original SHA-256 hashes.
- Check the integrity of one monitored file.
- Check all monitored files in a single scan.
- Detect modified, missing, and inaccessible files.
- List and remove monitored files.
- Persist file paths and hashes between application restarts.
- Handle invalid input and inaccessible paths with user-friendly messages.
- Update a file's baseline hash after an authorized change.

## How it works

When a file is added, the application calculates its SHA-256 hash and stores it as the original hash. During an integrity check, the application calculates a new hash for the same file. Matching hashes mean the file is unchanged; different hashes mean it has been modified. When a modification is authorized, the stored baseline can be updated to the file's current hash.

## Project structure

| Class | Responsibility |
| --- | --- |
| `FileIntegrityMonitor` | Console menu and user input |
| `FileMonitorManager` | Add, list, check, update, and remove monitored files |
| `MonitoredFile` | Stores a file path and its original hash |
| `HashUtility` | Calculates SHA-256 file hashes |
| `StorageUtility` | Saves and reloads monitored files locally |
| `HashUtilityTest` | Verifies SHA-256 hashing behaviour using JUnit 5 |
| `StorageUtilityTest` | Verifies saving and loading monitored-file records |
| `FileMonitorManagerTest` | Verifies manager behavior and persisted baseline changes |
| `IntegrityStatus` | Represents the possible integrity-check outcomes |
| `IntegrityCheckResult` | Pairs a monitored file path with its integrity status |

## Requirements

- JDK 25 or newer
- Apache Maven 3.9 or newer

Apache NetBeans is optional. The project follows Maven's standard directory layout and can be built from any compatible IDE or terminal.

## Building and running

1. Clone or download this repository.
2. Open a terminal in the project directory.
3. Build and test the project:

   ```text
   mvn clean package
   ```

4. Run the packaged application:

   ```text
   java -jar target/file-integrity-monitor-1.0.0-SNAPSHOT.jar
   ```

5. Select an option from the console menu and enter a path to a file you own or are authorized to monitor.

## Testing

The project includes JUnit 5 tests for `HashUtility`, `StorageUtility`, and `FileMonitorManager`.

The tests verify that:

- An empty file produces the expected SHA-256 hash.
- Known text produces the expected SHA-256 hash.
- Modifying file contents produces a different hash.
- Hashing a missing file throws the expected exception.
- Saving a monitored-file record produces the expected storage format.
- Loading a stored record reconstructs the expected `MonitoredFile`.
- Loading a missing storage file returns an empty list.
- Multiple records survive a save-and-load round trip.
- Adding a file persists its path and baseline hash.
- Re-adding the same path preserves the original trusted baseline.
- Removing a monitored file removes its persisted record.
- Updating a baseline persists the file's new hash.
- Integrity checks identify unchanged and modified files.
- Unmonitored paths are rejected before the program attempts to read them.
- Empty and populated monitoring lists produce the expected output.
- Checking all files reports unchanged, modified, and missing files.
- Unmonitored removal and baseline-update requests are handled safely.
- A monitored file that is later deleted is reported as missing or inaccessible.

Run all tests with:

```text
mvn test
```

A successful run reports 23 tests with no failures or errors.

## Notes

- `monitored-files.txt` is created automatically to store local monitoring data and is intentionally excluded from Git.
- This is an educational integrity-monitoring project. It reports changes; it does not prevent, repair, or remove them.

## Example output

```text
unchanged.txt is unchanged.
modified.txt has been modified.
deleted.txt is missing or inaccessible.

The file has been modified.
Baseline hash updated for baseline-text.txt.
The file is unchanged.
```