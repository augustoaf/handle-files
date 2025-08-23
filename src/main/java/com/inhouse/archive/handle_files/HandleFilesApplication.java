package com.inhouse.archive.handle_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.helper.StringWrapper;
import com.inhouse.archive.handle_files.service.ReadFileService;
import com.inhouse.archive.handle_files.service.ReadFileProcessor;

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) {

		// When the main method is executed, it runs before Spring has a chance to initialize
		// the application context and perform dependency injection, then you need to
		// run the Spring Boot application and get the ApplicationContext 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the bean
        ReadFileService readFile = context.getBean(ReadFileService.class);

		String fileName = "input1.txt";// = "D:\\repos\\handle-files\\inputs\\input1.txt"; // Path to your file
		ReadFileProcessor readFileProcessor =  readFile.getFileProcessor(fileName);

		StringWrapper lineString = new StringWrapper();
		while (readFileProcessor.readNext(lineString)) {
			System.out.println(lineString);
		}
	
	}
}
