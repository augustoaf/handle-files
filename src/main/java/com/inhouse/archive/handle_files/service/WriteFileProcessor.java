package com.inhouse.archive.handle_files.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;
import java.util.concurrent.TimeUnit;

public class WriteFileProcessor {

    private final String filePath;
    private final RedissonClient redissonClient;
    private int count = 0;

    // static variable to log only once a message, regardless how many instances of this class
    private static boolean LOG_ONCE = false;
    // A static, shared lock for all instances of this class 
    // (static lock because all WriteFileProcessor instances write to the same output file)
    private static final Object FILE_LOCK = new Object();

    public WriteFileProcessor(String filePath, RedissonClient redissonClient) {
        this.filePath = filePath;
        this.redissonClient = redissonClient;
        this.clearFile();
    }

    public void writeLine(String line) throws IOException {
        //writeLineWithDistributedLock(line);
        //writeLineWithThreadSafe(line);
        writeLineWithoutLock(line);
    }

    public void writeLineWithoutLock(String line) throws IOException {
       
        if (!LOG_ONCE) {
            System.out.println("INFO: writeLineWithoutLock is not thread safe. " + Thread.currentThread().getName());
            LOG_ONCE = true;
        }

        int counter = ++count;

        Files.write(
            Paths.get(filePath),
            java.util.Collections.singletonList(line + " : " + LocalDateTime.now() + " : " + Thread.currentThread().getName() + " : count " + counter),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }

    public void writeLineWithThreadSafe(String line) throws IOException {

        // TODO: improve this lock to consider the file name, otherwise it may lock threads to write to different files (like writes in error files and output files)
        synchronized(FILE_LOCK) {

            if (!LOG_ONCE) {
                System.out.println("INFO: writeLineWithThreadSafe is thread safe using synchronized block. " + Thread.currentThread().getName());
                LOG_ONCE = true;
            }

            int counter = ++count;

            Files.write(
                Paths.get(filePath),
                java.util.Collections.singletonList(line + " : " + LocalDateTime.now() + " : " + Thread.currentThread().getName() + " : count " + counter),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
    }
     
    public void writeLineWithDistributedLock(String line) throws IOException {

        //use file path as lock due each WriteFileProcessor instance write to a different file
        RLock lock = redissonClient.getLock(filePath + "_lock");

        try {
            // Try to acquire the lock with a maximum wait time of X seconds (1st parameter).
            // If the lock is not acquired within this time, it will return false.
            // The 2nd parameter (the lease time) is the time the lock will be automatically released
            // in case the process fails to release it.
            boolean isLocked = lock.tryLock(500, 1000, TimeUnit.MILLISECONDS);

            if (isLocked) {

                if (!LOG_ONCE) {
                   System.out.println("INFO: writeLineWithDistributedLock is thread safe using distributed lock. " + Thread.currentThread().getName());
                    LOG_ONCE = true;
                }

                int counter = ++count;

                try {
                    Files.write(
                        Paths.get(filePath),
                        java.util.Collections.singletonList(line + " : " + LocalDateTime.now() + " : " + Thread.currentThread().getName() + " : count " + counter),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                    );
                } finally {
                    // release the lock
                    lock.unlock();
                }
            } else {
                // This block is executed if the lock could not be acquired within the X timeout (1st parameter).
                throw new IOException("Could not acquire distributed lock within the time limit. Another process is holding it.");
            }
        } catch (InterruptedException e) {
            // Handle the interrupted exception properly.
            Thread.currentThread().interrupt();
            throw new IOException("Thread was interrupted while waiting for the lock.", e);
        }    
    }
    
    public void clearFile() {
        try {
            Files.write(Paths.get(filePath), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
}