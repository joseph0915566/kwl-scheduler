package com.kwl.scheduler.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import com.kwl.scheduler.hk2.SchedulerServiceManager;
import com.kwl.scheduler.hk2.SchedulerServiceBinder;

/**
 * 
 * @author Joseph Siegar
 *
 */
@WebListener
public class SchedulerServletContextListener implements ServletContextListener {
	
	private static final ServiceLocator SERVICE_LOCATOR;
	
	static{		
		SERVICE_LOCATOR = ServiceLocatorUtilities.createAndPopulateServiceLocator();
		ServiceLocatorUtilities.bind(SERVICE_LOCATOR, new SchedulerServiceBinder());		
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		SERVICE_LOCATOR.getService(SchedulerServiceManager.class).stop();
		SERVICE_LOCATOR.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		SERVICE_LOCATOR.getService(SchedulerServiceManager.class).start();
	}

	public static<T> T getService(Class<T> serviceClass){
		return SERVICE_LOCATOR.getService(serviceClass);
	}

}
