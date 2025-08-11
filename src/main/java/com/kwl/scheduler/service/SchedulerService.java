package com.kwl.scheduler.service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwler.commons.datetime.DatetimeService;
import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;
import com.kwler.commons.thread.ThreadService;
import com.kwl.scheduler.factory.ExecutorFactory;
import com.kwl.scheduler.job.provider.JobProvider;
import com.kwl.scheduler.job.runner.JobRunner;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class SchedulerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
	
	private AtomicBoolean isDeactivated = new AtomicBoolean(false);

	private final String interval;
	private final DatetimeService datetimeService;
	private final ThreadService threadService;
	private final JobProvider scheduleProvider;
	private final ExecutorFactory executorFactory;
	private final int wakeInterval;
	
	private final Object startLock = new Object();
	
	@Inject
	public SchedulerService(
							Properties properties
							, DatetimeService datetimeService
							, ThreadService threadService
							, JobProvider scheduleProvider
							, ExecutorFactory executorFactory
							) {
		this.datetimeService = datetimeService;
		this.threadService = threadService;
		this.interval = properties.getProperty("poll.interval", "minute");
		this.scheduleProvider = scheduleProvider;
		this.executorFactory = executorFactory;
		
		switch (interval) {
			case "second":
				wakeInterval = 200;
				break;
			default:
				wakeInterval = 5000;
				break;
		}
		
	}
	
	public void stop(){		
		LOGGER.info("Shutting down Scheduler");
		isDeactivated.set(true);		
	}
	
	public void start() throws InterruptedException {
		
		if(isDeactivated.get()) return;
		
		synchronized (startLock) {
			
			LOGGER.info("Scheduler started");
			
			long nextPeriod = setInitialPeriod();
			while(!isDeactivated.get()){

				List<Job> schedule = scheduleProvider.getSchedule();				
				
				nextPeriod = getNextPeriod(nextPeriod);
				longSleep(nextPeriod, wakeInterval);
				
				if(schedule != null && !schedule.isEmpty()){
					for(Job job : schedule)	executorFactory.executeShortTask(new JobRunner(job, nextPeriod, interval));							
				}
				
			}
			
			LOGGER.info("Scheduler shut down successfully");
			
		}
		
	}
	
	public long setInitialPeriod(){
		
		long initialPeriod;
		switch (interval.toLowerCase()) {
		case "second":
			initialPeriod = datetimeService.addDay(0, 0, 0, 0, true);
			break;
		case "minute":
			initialPeriod = datetimeService.addDay(0, 0, 0, true);
			break;
		default:
			initialPeriod = datetimeService.addDay(0, 0, true);
			break;
		}
		
		return initialPeriod;
		
	}
	
	public void longSleep(long target, long wakeInterval) throws InterruptedException {

		long diff = target - datetimeService.now();
		while(!isDeactivated.get() && diff > 0){
			if(diff > wakeInterval) threadService.sleepMillis(wakeInterval);
			else threadService.sleepMillis(diff);
			diff = target - datetimeService.now();
		}

	}
	
	public long getNextPeriod(long previousPeriod){
		
		synchronized (startLock) {
						
			long periodIncrease = 3600000L;
			if(interval.equalsIgnoreCase("second")) periodIncrease = 1000L;
			else if(interval.equalsIgnoreCase("minute")) periodIncrease = 60000L;

			long nextPeriod = previousPeriod + periodIncrease;			
			while(true){			
				if(nextPeriod - datetimeService.now() > 20)	return nextPeriod;			
				nextPeriod += periodIncrease;			
			}
			
		}
		
	}
	
}
