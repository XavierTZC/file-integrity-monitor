# File Integrity Monitor

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

## Testing

The project includes unit tests for `HashUtility` using JUnit 4.

The tests verify that:

- An empty file produces the expected SHA-256 hash.
- Known text produces the expected SHA-256 hash.
- Modifying file contents produces a different hash.
- Hashing a missing file throws the expected exception.

To run the tests in Apache NetBeans:

1. Expand **Test Packages**.
2. Right-click `HashUtilityTest.java`.
3. Select **Test File**.

A successful run should report that all four tests passed.

## Notes

- `monitored-files.txt` is created automatically to store local monitoring data and is intentionally excluded from Git.
- This is an educational integrity-monitoring project. It reports changes; it does not prevent, repair, or remove them.

## Example output

```text
unchanged.txt is unchanged.
modified.txt has been modified.
deleted.txt is missing or inaccessible.
```

```text
The file has been modified.
Baseline hash updated for baseline-text.txt.
The file is unchanged.
```
