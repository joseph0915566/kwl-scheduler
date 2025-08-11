package com.kwl.scheduler.hk2;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwl.scheduler.factory.ExecutorFactory;
import com.kwl.scheduler.service.SchedulerService;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class SchedulerServiceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceManager.class);

	private final SchedulerService schedulerService;
	private final ExecutorFactory executorFactory;
	
	@Inject
	public SchedulerServiceManager(SchedulerService schedulerService, ExecutorFactory executorFactory) {
		this.schedulerService = schedulerService;
		this.executorFactory = executorFactory;
	}
	
	public void start(){
		
		LOGGER.info("Starting KWL Scheduler");
		
		executorFactory.executeLongTask(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.currentThread().setName("scheduler-main-thread");
					schedulerService.start();															
				} catch (Exception e) {
					LOGGER.error("Error encountered when starting scheduler", e);
				}
			}
			
		});
		
	}
	
	public void stop(){

		LOGGER.info("Stopping KWL Scheduler");
		
		try {			
			schedulerService.stop();
		} catch (Throwable e) {
			LOGGER.error("Error encountered when stopping scheduler", e);
		}
		
	}
	
}
