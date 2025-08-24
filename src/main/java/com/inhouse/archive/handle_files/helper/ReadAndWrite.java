package com.inhouse.archive.handle_files.helper;

import java.io.IOException;
import java.time.LocalDateTime;

import com.inhouse.archive.handle_files.service.ReadFileProcessor;
import com.inhouse.archive.handle_files.service.ReadFileService;
import com.inhouse.archive.handle_files.service.WriteFileProcessor;

public class ReadAndWrite implements Runnable {

    private String fileName;
    private WriteFileProcessor writeFileProcessor;  
    private WriteFileProcessor writeFileError;
    private ReadFileProcessor readFileProcessor;

    public ReadAndWrite(String fileName, ReadFileService readFileService) {
        this.fileName = fileName;
        writeFileProcessor = readFileService.getWriteFileProcessor("output.txt");
	    writeFileError = readFileService.getWriteFileProcessor("error.txt");
        readFileProcessor =  readFileService.getFileProcessor(fileName);
    }

    @Override
    public void run() {
		
		StringWrapper lineString = new StringWrapper();

        LocalDateTime startTime = LocalDateTime.now();
		System.out.println("START PROCESSING FILE: " + fileName + " : thread : " + Thread.currentThread().getName());
        while (readFileProcessor.readNext(lineString)) {
            try {
				writeFileProcessor.writeLine(lineString.getValue());
			} catch (IOException e) {
				System.out.println("ERROR writing to file: " + e.getMessage());
				try {
					writeFileError.writeLine(Thread.currentThread().getName() + " : " + LocalDateTime.now().toString() + " : " + e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
        }
		System.out.println("FINISHING PROCESSING FILE: " + fileName 
            + " : thread : " + Thread.currentThread().getName() 
            + " : duration : " + java.time.Duration.between(startTime, LocalDateTime.now()).toSeconds() + " seconds");	
    }
}
