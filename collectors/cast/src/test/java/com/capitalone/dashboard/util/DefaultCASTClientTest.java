package com.capitalone.dashboard.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.collector.CASTSettings;
import com.capitalone.dashboard.collector.DefaultCASTClient;
import com.capitalone.dashboard.model.CASTProject;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCASTClientTest {
	private static Logger logger = LoggerFactory.getLogger("APIConnectionTest");
	@Mock
	private Supplier<RestOperations> restOperationsSupplier;
	@Mock
	private RestOperations rest;
	@Mock
	private CASTSettings settings;
	private DefaultCASTClient defaultCASTClient;

	@Before
	public final void init() {
		when(restOperationsSupplier.get()).thenReturn(rest);
		settings = new CASTSettings();
		//TODO url cannot be localhost
		settings.setUrl("http://localtest");
		settings.setProjectListSuffix("list");
		defaultCASTClient = new DefaultCASTClient(restOperationsSupplier,
				settings);
	}

	@Test
	public void CAST_getProjects() throws IOException {
		String response = "[{\"number\": 13,\"name\": \"BIO\",\"href\": \"AAD/applications/390\",\"adgDatabase\": \"demo_7017_central\"},{\"number\": 14,\"name\": \"BIO2\",\"href\": \"AAD/applications/391\",\"adgDatabase\": \"demo_7017_central\"},{\"number\": 15,\"name\": \"BIO3\",\"href\": \"AAD/applications/392\",\"adgDatabase\": \"demo_7017_central\"}]";
		String projectsUrl = settings.getUrl() + "/rest/"
				+ settings.getProjectListSuffix();
		doReturn(new ResponseEntity<String>(response, HttpStatus.OK))
				.when(rest).exchange(eq(URI.create(projectsUrl)),
						eq(HttpMethod.GET), Matchers.any(HttpEntity.class),
						eq(String.class));

		ObjectId id = new ObjectId();
		List<CASTProject> list = defaultCASTClient.getCASTProjects(id);
		
		//TODO to be fixed
		assertThat(list.size(), is(3));
		assertThat(list.get(0).getCollectorId(), is(id));
	}

	private String getJson(String fileName) throws IOException {
		InputStream inputStream = DefaultCASTClientTest.class
				.getResourceAsStream(fileName);
		return IOUtils.toString(inputStream);
	}

}