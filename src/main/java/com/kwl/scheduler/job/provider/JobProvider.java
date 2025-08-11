package com.kwl.scheduler.job.provider;

import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;

import java.util.List;

/**
 * 
 * @author Joseph Siegar
 *
 */
public interface JobProvider {

	void reload();
	List<Job> getSchedule();
	
}
