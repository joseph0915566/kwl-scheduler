package com.kwl.scheduler.hk2;

import java.util.Properties;

import javax.inject.Singleton;

import com.kwler.commons.hk2.CommonService;
import com.kwler.commons.hk2.ServiceClass;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.kwl.scheduler.factory.ExecutorFactory;
import com.kwl.scheduler.job.provider.DBJobProvider;
import com.kwl.scheduler.job.provider.JobProvider;
import com.kwl.scheduler.producer.ResourceQueueProducer;
import com.kwl.scheduler.service.SchedulerService;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class SchedulerServiceBinder extends AbstractBinder {

	private void addService(ServiceClass... serviceClasses){
		for(ServiceClass serviceClass : serviceClasses){
			bind(serviceClass.getImplClass()).to(serviceClass.getInterfaceClass()).in(serviceClass.getScope());
		}
	}

	@Override
	protected void configure() {

		addService(CommonService.registerServices());
		
		//services
		addService(
				new ServiceClass(SchedulerService.class)
				, new ServiceClass(ResourceQueueProducer.class)
				, new ServiceClass(ExecutorFactory.class)
				, new ServiceClass(SchedulerServiceManager.class)
				, new ServiceClass(JobProvider.class, DBJobProvider.class)
				);

		//Properties
		bindFactory(PropertiesFactory.class).to(Properties.class).in(Singleton.class);
		
	}

}
