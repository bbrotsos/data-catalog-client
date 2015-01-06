package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PublisherTest {

	private static final Logger log = Logger.getLogger(PublisherTest.class
			.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test the load specifically the recursive suborganization load.
	 * 
	 * { "@type": "org:Organization", "name": "Widget Services",
	 * "subOrganizationOf": { "@type": "org:Organization", "name":
	 * "Office of Citizen Services and Innovative Technologies",
	 * "subOrganizationOf": { "@type": "org:Organization", "name":
	 * "General Services Administration", "subOrganizationOf": { "@type":
	 * "org:Organization", "name": "U.S. Government" } } } }
	 */
	@Test
	public void testLoadPublisher() {
		Publisher publisher = new Publisher();
		Publisher publisherCompare = new Publisher();
		JSONObject publisherObject = new JSONObject();

		String publisherTestFile = "sample_data/test/publisherTest.json";
		try {
			publisherObject = Utils.loadJsonObjectFile(publisherTestFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		try {
			publisher.loadDatasetFromPOD_JSON(publisherObject);
		} catch (PublisherException e) {
			log.log(Level.SEVERE, e.toString());
		}

		Publisher subOrganization4 = new Publisher();
		subOrganization4.setName("U.S. Government");
		subOrganization4.setType("org:Organization");

		Publisher subOrganization3 = new Publisher();
		subOrganization3.setName("General Services Administration");
		subOrganization3.setType("org:Organization");
		subOrganization3.setSubOrganization(subOrganization4);

		Publisher subOrganization2 = new Publisher();
		subOrganization2
				.setName("Office of Citizen Services and Innovative Technologies");
		subOrganization2.setType("org:Organization");
		subOrganization2.setSubOrganization(subOrganization3);

		publisherCompare.setType("org:Organization");
		publisherCompare.setName("Widget Services");
		publisherCompare.setSubOrganization(subOrganization2);

		assertEquals(publisher, publisherCompare);
	}

	@Test
	public void testRequiredPublisher() {
		Publisher publisher = new Publisher();
		JSONObject publisherObject = new JSONObject();
		PublisherException publisherException = null;

		String publisherRequiredTestFile = "sample_data/test/publisherRequiredTest.json";
		try {
			publisherObject = Utils
					.loadJsonObjectFile(publisherRequiredTestFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		try {
			publisher.loadDatasetFromPOD_JSON(publisherObject);
		} catch (PublisherException e) {
			publisherException = new PublisherException();
			publisherException = e;
			log.log(Level.SEVERE, e.toString());
			assertEquals(e.toString(), "Publisher invalid: Name is required");
		}

		assertNotNull(publisherException);

	}

}
