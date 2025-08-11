package com.kwl.scheduler.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.glassfish.hk2.api.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class ExecutorFactory implements PreDestroy {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorFactory.class);
	
	private final ExecutorService shortTaskExecutorService = Executors.newFixedThreadPool(20, new ScheduledThreadFactory("short-task"));
	private final ExecutorService longTaskExecutorService = Executors.newFixedThreadPool(20, new ScheduledThreadFactory("long-task"));
	
	public void executeShortTask(Runnable shortTask){
		try {
			shortTaskExecutorService.execute(shortTask);			
		} catch (RejectedExecutionException e) {
			LOGGER.info("Shutting down Short Task Executor Service, rejecting incoming task");
		} catch (Exception e) {
			LOGGER.error("Error encountered when executing short task", e);
		}
	}
	
	public void executeLongTask(Runnable longTask){		
		try {
			longTaskExecutorService.execute(longTask);			
		} catch (RejectedExecutionException e) {
			LOGGER.info("Shutting down Long Task Executor Service, rejecting incoming task");
		} catch (Exception e) {
			LOGGER.error("Error encountered when executing long task", e);
		}
	}

	@Override
	public void preDestroy() {
		
		LOGGER.info("Attempting to shut down Short Task Executor Service");
		
		shortTaskExecutorService.shutdown();
		try {
			if(!shortTaskExecutorService.awaitTermination(5, TimeUnit.SECONDS)) shortTaskExecutorService.shutdownNow();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		LOGGER.info("Short Task Executor Service shut down successfully");
		
		LOGGER.info("Attempting to shut down Long Task Executor Service");
		
		longTaskExecutorService.shutdown();
		try {
			if(!longTaskExecutorService.awaitTermination(10, TimeUnit.SECONDS)) longTaskExecutorService.shutdownNow();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		LOGGER.info("Long Task Executor Service shut down successfully");
		
	}
	
}
