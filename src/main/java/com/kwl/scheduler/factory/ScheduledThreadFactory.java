package com.kwl.scheduler.factory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class ScheduledThreadFactory implements ThreadFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledThreadFactory.class); 
	
	private final String poolName;
    final AtomicLong threadCount = new AtomicLong(0);
    
    public ScheduledThreadFactory(String poolName) {
		this.poolName = poolName;
	}

	@Override
	public Thread newThread(Runnable r) {

		Thread thread = Executors.defaultThreadFactory().newThread(r);
		thread.setName(poolName + "-thread-" + String.format("%4d", threadCount.getAndIncrement()));
		
		UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				LOGGER.error("", e);
			}
			
		};		
		thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		
		return thread;
		
	}

}
