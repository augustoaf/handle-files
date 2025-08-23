package com.inhouse.archive.handle_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.helper.StringWrapper;
import com.inhouse.archive.handle_files.service.ReadFileService;
import com.inhouse.archive.handle_files.service.ReadFileProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) {

		// When the main method is executed, it runs before Spring has a chance to initialize
		// the application context and perform dependency injection, then you need to
		// run the Spring Boot application and get the ApplicationContext 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the bean
        ReadFileService readFile = context.getBean(ReadFileService.class);

		String fileName = "input1.txt";
		
		ReadFileProcessor readFileProcessor =  readFile.getFileProcessor(fileName);

		StringWrapper lineString = new StringWrapper();
        String outputFile = "D:\\repos\\handle-files\\outputs\\output.txt"; // = "output.txt";
        // Clear the output file at the start (optional)
        try {
			Files.write(Paths.get(outputFile), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("START PROCESSING FILE: " + fileName);
        while (readFileProcessor.readNext(lineString)) {
            try {
				Files.write(
				    Paths.get(outputFile),
				    Collections.singletonList(lineString.toString() + " : thread " + Thread.currentThread().getName()),
				    StandardOpenOption.CREATE,
				    StandardOpenOption.APPEND
				);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		System.out.println("FINISHING PROCESSING FILE: " + fileName);	
	}
}
