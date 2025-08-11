package com.kwl.scheduler.hk2;

import java.util.Properties;

import javax.inject.Inject;

import com.kwler.commons.io.PropertyService;
import org.glassfish.hk2.api.Factory;

/**
 * 
 * @author Joseph Siegar
 *
 */
public class PropertiesFactory implements Factory<Properties> {
	
	private final PropertyService propertyService;
	
	@Inject
	public PropertiesFactory(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@Override
	public void dispose(Properties properties) {
	}

	@Override
	public Properties provide() {
		return propertyService.load(
									"/usr/local/etc/kwl"
									, "scheduler.properties"
									, "db.properties"
									);
	}

}
