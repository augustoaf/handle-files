package com.inhouse.archive.handle_files.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.inhouse.archive.handle_files.helper.StringWrapper;

public class ReadAndWriteService implements Runnable {

    private String fileName;
    private WriteFileProcessor writeFileProcessor;  
    private WriteFileProcessor writeFileError;
    private ReadFileProcessor readFileProcessor;

    public ReadAndWriteService(String fileName, FileProcessorFactory fileProcessorFactory) {
        this.fileName = fileName;
        
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        String formattedTime = currentTime.format(formatter);

        writeFileProcessor = fileProcessorFactory.getWriteFileProcessor("output.txt");
	    writeFileError = fileProcessorFactory.getWriteFileProcessor("error_" + formattedTime + ".txt");
        readFileProcessor =  fileProcessorFactory.getFileProcessor(fileName);
    }

    @Override
    public void run() {
		
		StringWrapper lineString = new StringWrapper();
        LocalDateTime startTime = LocalDateTime.now();

        System.out.println("START PROCESSING FILE: " + fileName + " : thread : " + Thread.currentThread().getName());
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
		System.out.println("FINISHING PROCESSING FILE: " + fileName 
            + " : thread : " + Thread.currentThread().getName() 
            + " : duration : " + java.time.Duration.between(startTime, LocalDateTime.now()).toSeconds() + " seconds");	
    }
}
