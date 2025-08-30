package com.inhouse.archive.handle_files;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.service.FileProcessorFactory;
import com.inhouse.archive.handle_files.service.ReadAndWriteService;
import com.inhouse.archive.handle_files.service.ReadFileAbstract;
import com.inhouse.archive.handle_files.service.WriteFileProcessor;
import com.inhouse.dto.FileChunkDTO;

/*	
 * This is a Spring Boot application that demonstrates concurrent file reading and writing
 * considering inputs from Redis queue. Each queue item is a file chunk (start to end in bytes).
 */

@SpringBootApplication
public class HandleFilesChunksApplication {

	// TODO: read from application.properties
    private static final String CHUNKS_QUEUE = "file_chunks_queue";
		
    public static void main(String[] args) throws InterruptedException {
        
        // When this main method is executed, it runs before Spring initialize
		// the application context and perform dependency injection, then you can 
		// run the Spring Boot application manually to get access to the Beans 
        ApplicationContext context = SpringApplication.run(HandleFilesChunksApplication.class, args);

        // Get the FileProcessorFactory bean and instantiate shared output and error file processors
        FileProcessorFactory fileProcessorFactory = context.getBean(FileProcessorFactory.class);		
		WriteFileProcessor writeOutput = fileProcessorFactory.getWriteFileProcessor("output.txt");
		WriteFileProcessor writeError = fileProcessorFactory.getWriteFileProcessor("error.txt");	
		
        // Get the RedissonClient bean to interact with Redis
        RedissonClient redissonClient = context.getBean(RedissonClient.class);

		RBlockingQueue<FileChunkDTO> queue = redissonClient.getBlockingQueue(CHUNKS_QUEUE, new JsonJacksonCodec());
		queue.clear();//for testing, clear the queue on each run
        
		System.out.println("Starting consumer loop...");
		
		// loop to consume from a redis list - each item represents a file chunk
		while (true) {
			FileChunkDTO chunk = queue.take(); // This will block until an item is available

			System.out.println("Dequeued chunk for processing: " + chunk);
			String threadName = "ChunkProcessor-" + chunk.getFileName() + "-" + chunk.getStartByte();
			
			ReadFileAbstract readInput = fileProcessorFactory.getReadFileChunkProcessor(
				chunk.getFilePath(), chunk.getStartByte(), chunk.getEndByte());

			ReadAndWriteService readAndWriteTask = new ReadAndWriteService(readInput, writeOutput, writeError);
			
			//write in a distinct file for each thread - use for debugging
			/* 
			WriteFileProcessor writeOutputTemp = fileProcessorFactory.getWriteFileProcessor(threadName + "-output.txt");
			ReadAndWriteService readAndWriteTask = new ReadAndWriteService(readInput, writeOutputTemp, writeError);
			*/

			// TODO: implement a thread pool to limit the number of concurrent threads
			// consider using ExecutorService with a fixed thread pool
			Thread worker = new Thread(readAndWriteTask, threadName);
			worker.start();
		}
    }
}
