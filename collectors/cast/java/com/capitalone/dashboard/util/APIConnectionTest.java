package com.capitalone.dashboard.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capitalone.dashboard.client.DefaultPivotalTrackerClient;
import com.capitalone.pivotal.api.dao.ProjectsDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class APIConnectionTest {
	private static Logger logger = LoggerFactory.getLogger("APIConnectionTest");	
	
	@Mock ProjectsDAO projectsDAO;	
	@Mock DefaultPivotalTrackerClient defaultPivotalTrackerClient;	
	private FeatureSettings settings;
	
	@Autowired
	private MultiFeatureSettings featureProps;

	@Before
	public final void init() {
		FeatureConfigData config = new FeatureConfigData(); 
		config.setPivotalCredentials("cred");
		config.setPivotalBaseUrl("http://pivotal.com");
		settings = new FeatureSettings();
		settings.setActiveSource(new String[]{"Pivotal"});
		settings.getConfig().put("Pivotal", config);
	}
	
	@Test
	public void testDefaluPivotalClients_projectsNotNull()
	{
		DefaultPivotalTrackerClient client = new DefaultPivotalTrackerClient(settings);
		ProjectsDAO projectDAO= client.projects("Pivotal");
		Assert.assertNotNull(projectDAO);
	}

}