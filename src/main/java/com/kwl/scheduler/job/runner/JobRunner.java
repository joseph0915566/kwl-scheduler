package com.kwl.scheduler.job.runner;

import java.util.List;

import com.kwler.commons.datetime.DatetimeService;
import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Cron;
import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwl.scheduler.factory.ExecutorFactory;
import com.kwl.scheduler.producer.ResourceQueueProducer;
import com.kwl.scheduler.servlet.SchedulerServletContextListener;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class JobRunner implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);
	
	private final Job job;
	private final ExecutorFactory executorFactory;
	private final DatetimeService datetimeService;
	private final ResourceQueueProducer resourceQueueProducer;
	private final long timestamp;
	private final String interval;
	
	public JobRunner(Job job, long timestamp, String interval) {
		this.job = job;
		this.executorFactory = SchedulerServletContextListener.getService(ExecutorFactory.class);
		this.datetimeService = SchedulerServletContextListener.getService(DatetimeService.class);
		this.resourceQueueProducer = SchedulerServletContextListener.getService(ResourceQueueProducer.class);
		this.timestamp = timestamp;
		this.interval = interval;
	}
	
	@Override
	public void run() {
		
		Thread.currentThread().setName("scheduler-job-runner");
		if(shouldRunJob()){
			
			LOGGER.info("Attempting to run " + job.getName());
			executorFactory.executeLongTask(new Runnable() {
			
				@Override
				public void run() {
					resourceQueueProducer.populateQueue(job);				
				}
			
			});
			
		} else LOGGER.info(job.getName() + " not executed");
		
	}
	
	public boolean shouldRunJob(){
		
		List<Cron> cronlist = job.getCronList();
		if(cronlist != null && !cronlist.isEmpty()){
			
			for(Cron cron : cronlist){

				//month
				int month = cron.getMonth();
				int timestampMonth = datetimeService.getMonth(timestamp); 
				if(month * timestampMonth != month * month) continue;
				
				//day
				int day = cron.getDay();
				int timestampDay = datetimeService.getDayOfWeek(timestamp);
				if(day * timestampDay != day * day) continue;
				
				//date
				int date = cron.getDate();
				int timestampDate = datetimeService.getDate(timestamp);
				if(date * timestampDate != date * date) continue;
				
				//hour
				if(cron.getHour() != -1 && cron.getHour() != datetimeService.getHour(timestamp)) continue;
				if(interval.equalsIgnoreCase("hour")) return true;

				//minute
				if(cron.getMinute() != -1 && cron.getMinute() != datetimeService.getMinute(timestamp)) continue;
				if(interval.equalsIgnoreCase("minute")) return true;

				//second
				if(cron.getSecond() != -1 && cron.getSecond() != datetimeService.getSeconds(timestamp)) continue;
				
				return true;
				
			}
			
		}
		
		return false;
		
	}

}
