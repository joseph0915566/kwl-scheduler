package com.kwl.scheduler.job.provider;

import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Joseph Siegar
 *
 */
public abstract class BaseJobProvider implements JobProvider {

	protected List<Job> schedule = new ArrayList<>();
	
}
