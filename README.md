# Handle Files - IO Race Condition Simulation

## Purpose

This project is designed to **simulate IO race conditions** that can occur when multiple threads or processes write to the same file concurrently. It demonstrates:

- How concurrent file writes can lead to data corruption or loss (race conditions).
- How to apply **thread-safe solutions** (such as Java synchronization) to prevent conflicts within a single JVM.
- How to use a **distributed lock** (e.g., with Redis) to coordinate file access across multiple instances or nodes.

## Important

In the writeLineWithoutLock method, the race condition may manifest as both inconsistent file output (potentially corrupted lines or incorrect line counts) and likely an incorrect final value for the shared counter. This inconsistency confirms that multiple threads are competing for and corrupting a shared resource. 
Note: may be hard to simulate IO write race condition, but the amount of lines and the counter will likely be affected more easily. 

Conversely, the writeLineWithThreadSafe and writeLineWithDistributedLock methods, which uses a mechanism lock, guarantees that the counter will reach the exact number of lines processed and that the output file will be consistently written without corruption. The lock ensures that the IO operations are performed atomically.

It's important to note that even with the lock, there is no guaranteed processing order for the lines from input1.txt when the same 'Read' object shared among N threads. E.g., Threads 1A and 1B share the same ReadAndWrite instance, they will compete for input, leading to a non-deterministic order of lines being written to the output file. The lock only guarantees the integrity of the writes, not their sequencing.

## Features

- Reads from multiple input files using separate threads.
- Reads from multiple input files breakdown per chunks, having Redis Queue as source to trigger the processing.
- Writes to a shared output file, exposing potential race conditions.
- Provides examples and solutions for:
  - Thread-safe file writing using Java synchronization.
  - Distributed locking using Redis for multi-node coordination.

## Usage

1. **Simulate Race Condition:**  
   Run the application using writeLineWithoutLock to observe possible file write conflicts.
2. **Simulate Thread-Safe Solution:**  
   Use Java synchronization to prevent concurrent writes within a single JVM.
   example in writeLineWithThreadSafe
3. **Simulate Distributed Lock:**  
   Integrate Redis-based distributed locking to coordinate writes across multiple instances.
   example in writeLineWithDistributedLock

Note: change the simulation on writeLine method inside the WriteFileProcessor class.

## Requirements

- Java 17+
- Maven
- (Optional for distributed lock) Redis server and Redisson or Lettuce library

## How to Build and Run

Use the provided batch scripts:

- `package.bat` — Build the project with Maven.
- `run.bat` — Run the Spring Boot application.
