package com.inhouse.archive.handle_files.service;

import java.io.IOException;
import java.time.LocalDateTime;

import com.inhouse.archive.handle_files.helper.ReadAndWriteServicePool;
import com.inhouse.archive.handle_files.helper.StringWrapper;

public class ReadAndWriteService implements Runnable {

    private ReadFileAbstract readFileProcessor;
    private WriteFileProcessor writeFileProcessor;  
    private WriteFileProcessor writeFileError;
    private ReadAndWriteServicePool readAndWriteServicePool;

    public ReadAndWriteService(ReadAndWriteServicePool readAndWriteServicePool) {
        this.readAndWriteServicePool = readAndWriteServicePool;
    }

    public ReadAndWriteService(ReadFileAbstract readFileProcessor, 
        WriteFileProcessor writeFileProcessor, WriteFileProcessor writeFileError) {

        this.setReadAndWrites(readFileProcessor, writeFileProcessor, writeFileError);
    }

    public void setReadAndWrites(ReadFileAbstract readFileProcessor, 
        WriteFileProcessor writeFileProcessor, WriteFileProcessor writeFileError) {

        this.readFileProcessor = readFileProcessor;
        this.writeFileProcessor = writeFileProcessor;
        this.writeFileError = writeFileError;
    }

    public void releaseResources() {
        this.readFileProcessor = null;
        this.writeFileProcessor = null;
        this.writeFileError = null;
    }
    
    @Override
    public void run() {
		
		StringWrapper lineString = new StringWrapper();
        LocalDateTime startTime = LocalDateTime.now();

        System.out.println("START PROCESSING FILE: " + readFileProcessor.getFileName() 
            + " : thread : " + Thread.currentThread().getName());
            
        while (readFileProcessor.readNext(lineString)) {
            try {
				writeFileProcessor.writeLine(lineString.getValue());
                //Thread.sleep(1);//try to force a race condition
			} catch (Exception e) {
				System.out.println("ERROR writing to file: " + e.getMessage());
				try {
					writeFileError.writeLine(Thread.currentThread().getName() + " : " + LocalDateTime.now().toString() + " : " + e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
        }
		System.out.println("FINISHING PROCESSING FILE: " + readFileProcessor.getFileName() 
            + " : thread : " + Thread.currentThread().getName() 
            + " : duration : " + java.time.Duration.between(startTime, LocalDateTime.now()).toSeconds() + " seconds");	

        // return this instance to the pool to be reused
        readAndWriteServicePool.returnToPool(this);
    }
}
