package com.inhouse.archive.handle_files;

import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.service.FileProcessorFactory;
import com.inhouse.archive.handle_files.service.ReadAndWriteService;

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) throws InterruptedException {

		// When this main method is executed, it runs before Spring initialize
		// the application context and perform dependency injection, then you can 
		// run the Spring Boot application manually to get access to the Beans 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the FileProcessorFactory bean
        FileProcessorFactory fileProcessorFactory = context.getBean(FileProcessorFactory.class);		
		
		// Threads 1A and 1B have access to the same ReadAndWrite instance, so they will compete to 
		// read the same file, then the order is not guaranteed
		ReadAndWriteService readAndWrite1 = new ReadAndWriteService("input1.txt", fileProcessorFactory);
		Thread thread1A = new Thread(readAndWrite1,"Thread-1A");
		Thread thread1B = new Thread(readAndWrite1,"Thread-1B");
		
		ReadAndWriteService readAndWrite2 = new ReadAndWriteService("input2.txt", fileProcessorFactory);
		Thread thread2 = new Thread(readAndWrite2,"Thread-2");
		
		thread1A.start();
		thread1B.start();
		thread2.start();

		// join() means this 'main' thread will wait all other threads to finish before proceeding
		// this is needed to release resources like RedissonClient
		thread1A.join();
		thread1B.join();
		thread2.join();
		
		// Redis client need to shutdown, otherwise it will keep the main thread hang
        RedissonClient redissonClient = context.getBean(RedissonClient.class);
		redissonClient.shutdown();
	}	
}
