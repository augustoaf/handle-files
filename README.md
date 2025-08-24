# Handle Files - IO Race Condition Simulation

## Purpose

This project is designed to **simulate IO race conditions** that can occur when multiple threads or processes write to the same file concurrently. It demonstrates:

- How concurrent file writes can lead to data corruption or loss (race conditions).
- How to apply **thread-safe solutions** (such as Java synchronization) to prevent conflicts within a single JVM.
- How to use a **distributed lock** (e.g., with Redis) to coordinate file access across multiple instances or nodes.

## Features

- Reads from multiple input files using separate threads.
- Writes to a shared output file, exposing potential race conditions.
- Provides examples and solutions for:
  - Thread-safe file writing using Java synchronization.
  - Distributed locking using Redis for multi-node coordination.

## Usage

1. **Simulate Race Condition:**  
   Run the application as-is to observe possible file write conflicts.
2. **Apply Thread-Safe Solution:**  
   Use Java synchronization to prevent concurrent writes within a single JVM.
3. **Apply Distributed Lock:**  
   Integrate Redis-based distributed locking to coordinate writes across multiple instances.

## Requirements

- Java 17+
- Maven
- (Optional for distributed lock) Redis server and Redisson or Lettuce library

## How to Build and Run

Use the provided batch scripts:

- `package.bat` — Build the project with Maven.
- `run.bat` — Run the Spring Boot application.
