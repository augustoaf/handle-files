package com.inhouse.archive.handle_files;

import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.helper.ReadAndWrite;
import com.inhouse.archive.handle_files.service.ReadFileService;

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) throws InterruptedException {

		// When the main method is executed, it runs before Spring has a chance to initialize
		// the application context and perform dependency injection, then you need to
		// run the Spring Boot application and get the ApplicationContext 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the bean
        ReadFileService readFileService = context.getBean(ReadFileService.class);		
		
		//threads 1A and 1B have the same ReadAndWrite instance, so they will read the same file only once, but each thread will read a distinct line
		ReadAndWrite readAndWrite1 = new ReadAndWrite("input1.txt", readFileService);
		Thread thread1A = new Thread(readAndWrite1,"Thread-1A");
		Thread thread1B = new Thread(readAndWrite1,"Thread-1B");
		
		ReadAndWrite readAndWrite2 = new ReadAndWrite("input2.txt", readFileService);
		Thread thread2 = new Thread(readAndWrite2,"Thread-2");
		
		thread1A.start();
		thread1B.start();
		thread2.start();

		//using join() means this 'main' thread will wait all other threads to finish before proceeding
		//this is needed to release resources like RedissonClient
		thread1A.join();
		thread1B.join();
		thread2.join();
		
		// Redis client need to shutdown, otherwise it will keep the main thread hang
        RedissonClient redissonClient = context.getBean(RedissonClient.class);
		redissonClient.shutdown();
	}	
}
