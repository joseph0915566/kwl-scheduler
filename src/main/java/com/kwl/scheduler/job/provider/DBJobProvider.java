package com.kwl.scheduler.job.provider;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import com.kwler.commons.datetime.DatetimeService;
import com.kwler.commons.db.scheduler.job.nosql.mongo.dao.JobDAO;
import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class DBJobProvider extends BaseJobProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBJobProvider.class);
	
	private final JobDAO dao;
	private final long reloadInterval;
	private final DatetimeService datetimeService;

	private long lastReload = 0;
	
	@Inject
	public DBJobProvider(
						JobDAO dao
						, Properties properties
						, DatetimeService datetimeService
						) {
		this.dao = dao;
		this.reloadInterval = Long.parseLong(properties.getProperty("schedule.reload.interval", "60"));
		this.datetimeService = datetimeService;
	}

	@Override
	public void reload() {
		
		LOGGER.info("Loading schedule from database");
		
		List<Job> schedule = dao.getAll();
		synchronized (this.schedule) {
			this.schedule = schedule;
			lastReload = datetimeService.now();
		}
		
		LOGGER.info("Schedule loaded successfully from database");
		
	}

	@Override
	public List<Job> getSchedule() {
		
		synchronized (this.schedule) {
			
			if(reloadInterval > 0){				
				long now = datetimeService.now();
				if(now - lastReload > reloadInterval * 60000) reload();				
			}
			
			return schedule;
			
		}
		
	}
	
}
