package com.inhouse.archive.handle_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.helper.ReadAndWrite;
import com.inhouse.archive.handle_files.service.ReadFileService;

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) {

		// When the main method is executed, it runs before Spring has a chance to initialize
		// the application context and perform dependency injection, then you need to
		// run the Spring Boot application and get the ApplicationContext 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the bean
        ReadFileService readFileService = context.getBean(ReadFileService.class);		
		
		// TODO: apply thread safe write to file, next a distributed lock
		// using Redis to prevent N instances running in different nodes
		// which can lead to race conditions as well  

		ReadAndWrite readAndWrite1 = new ReadAndWrite("input1.txt", readFileService);
		ReadAndWrite readAndWrite2 = new ReadAndWrite("input2.txt", readFileService);
		Thread thread1 = new Thread(readAndWrite1);
		Thread thread2 = new Thread(readAndWrite2);

		thread1.start();
		thread2.start();
	}	
}
