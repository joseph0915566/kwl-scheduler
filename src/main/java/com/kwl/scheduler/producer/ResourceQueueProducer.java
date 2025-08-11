package com.kwl.scheduler.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.kwler.commons.db.scheduler.job.nosql.mongo.model.Job;
import com.kwler.commons.db.scheduler.query.resource.nosql.mongo.dao.ResourceQueryDAO;
import com.kwler.commons.db.scheduler.query.resource.nosql.mongo.model.ResourceQuery;
import com.kwler.commons.messaging.model.QueueMessage;
import com.kwler.commons.messaging.service.SQSService;
import com.kwler.commons.messaging.service.SQSService.QueueName;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class ResourceQueueProducer implements QueueProducer{
	
	private final SQSService sqsService;
	private final ResourceQueryDAO dao;

	@Inject
	public ResourceQueueProducer(SQSService sqsService, ResourceQueryDAO dao) {
		this.sqsService = sqsService;
		this.dao = dao;
	}

	@Override
	public void populateQueue(Job job) {
		
		Map<String, String> jobParams = job.getJobParams();
		if(jobParams == null || jobParams.isEmpty()) return;

		String jobParam = jobParams.get("targetQueue");
		if(jobParam == null) return;
		QueueName queueName = QueueName.valueOf(jobParam);
		
		jobParam = jobParams.get("tag");
		if(jobParam == null) return;
		
		Thread.currentThread().setName(jobParam + "-queue-producer");

		List<ResourceQuery> queries = dao.getByTag(jobParam);
		if(queries != null && !queries.isEmpty()){
			
			List<QueueMessage> messages = new ArrayList<>();
			for(ResourceQuery query : queries){

				QueueMessage queueMessage = new QueueMessage();
				Map<String, String> messageParams = new HashMap<>();
				queueMessage.setAttributes(messageParams);					
				
				Map<String, String> queryParams = query.getResourceQueryParams();
				if(queryParams != null){
					for(Map.Entry<String, String> entry : queryParams.entrySet()) messageParams.put(entry.getKey(), entry.getValue());					
				}
				messages.add(queueMessage);					
				
				if(messages.size() == 10){
					sqsService.sendMessageBatch(queueName, messages);
					messages = new ArrayList<>();					
				}
				
			}
			
			if(!messages.isEmpty()) sqsService.sendMessageBatch(queueName, messages);
			
		}
		
	}

}
