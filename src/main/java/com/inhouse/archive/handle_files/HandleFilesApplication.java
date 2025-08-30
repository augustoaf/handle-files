package com.inhouse.archive.handle_files;

import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.service.FileProcessorFactory;
import com.inhouse.archive.handle_files.service.ReadAndWriteService;
import com.inhouse.archive.handle_files.service.ReadFileProcessor;
import com.inhouse.archive.handle_files.service.WriteFileProcessor;

/*	
 * This is a Spring Boot application that demonstrates concurrent file reading and writing
 * considering an input file at once as the unit of work.
 */

@SpringBootApplication
public class HandleFilesApplication {

	public static void main(String[] args) throws InterruptedException {

		// When this main method is executed, it runs before Spring initialize
		// the application context and perform dependency injection, then you can 
		// run the Spring Boot application manually to get access to the Beans 
        ApplicationContext context = SpringApplication.run(HandleFilesApplication.class, args);

        // Get the FileProcessorFactory bean and instantiate shared output and error file processors
        FileProcessorFactory fileProcessorFactory = context.getBean(FileProcessorFactory.class);		
		WriteFileProcessor writeOutput = fileProcessorFactory.getWriteFileProcessor("output.txt");
		WriteFileProcessor writeError = fileProcessorFactory.getWriteFileProcessor("error.txt");	
		
		// Threads 1A and 1B have access to the same ReadAndWrite instance (consequently same 
		// ReadFileProcessor instance), so they will compete to read the same file, then the 
		// order is not guaranteed 
		// (analogy: it is like N consumers from same group reading from a Kafka topic w/out ordering)
		ReadFileProcessor readInput1 = fileProcessorFactory.getReadFileProcessor("input1.txt");
		ReadAndWriteService readAndWrite1 = new ReadAndWriteService(readInput1, writeOutput, writeError);
		Thread thread1A = new Thread(readAndWrite1,"Thread-1A");
		Thread thread1B = new Thread(readAndWrite1,"Thread-1B");
		
		// This thread although it is reading the same file as threads 1A and 1B, it has its own 
		// ReadFileProcessor instance, so it will read the file independently
		// (analogy: it is like a different consumer group reading from same Kafka topic)
		ReadFileProcessor readInput1Dedicated = fileProcessorFactory.getReadFileProcessor("input1.txt");
		ReadAndWriteService readAndWrite2 = new ReadAndWriteService(readInput1Dedicated, writeOutput, writeError);
		Thread thread2 = new Thread(readAndWrite2,"Thread-2");
		
		ReadFileProcessor readInput2 = fileProcessorFactory.getReadFileProcessor("input2.txt");
		ReadAndWriteService readAndWrite3 = new ReadAndWriteService(readInput2, writeOutput, writeError);
		Thread thread3 = new Thread(readAndWrite3,"Thread-3");
		
		thread1A.start();
		thread1B.start();
		thread2.start();
		thread3.start();

		// join() means this 'main' thread will wait all other threads to finish before proceeding
		// this is needed to release resources like RedissonClient
		thread1A.join();
		thread1B.join();
		thread2.join();
		thread3.join();
		
		// Redis client need to shutdown, otherwise it will keep the main thread hang
        RedissonClient redissonClient = context.getBean(RedissonClient.class);
		redissonClient.shutdown();
	}	
}
