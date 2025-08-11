package com.kwl.scheduler.producer;

import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;

/**
 * 
 * @author Joseph Siegar
 *
 */
public interface QueueProducer {

	void populateQueue(Job job);
	
}
