package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NetworkRequestTest {

	private static final Logger log = Logger.getLogger(NetworkRequestTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test this manually so not to hit the server every time.
	 * @throws ParseException
	 */
	@Test
	public void testGetOrganization() throws ParseException {
		
		//manually check this test
		if (false)
		{
		String response = "";
		try{
			NetworkRequest nr = new NetworkRequest();
			response = nr.getOrganizationCatalog("usda-gov");
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		Catalog testCatalog = new Catalog();
		CatalogException catalogException = null;
		try{
			testCatalog.loadCatalogFromJSONString(response);
		}
		catch(CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
			catalogException = e;
		}
		assertNull(catalogException);
		}
	}
	
	/**
	 * Added a filename parameter to NetworkRequest to the config file, this makes for easier testing.
	 * @throws ParseException
	 */
	@Test
	public void testCKANGetOrganization() throws ParseException {
		String response = "";
		try{
			NetworkRequest nr = new NetworkRequest("sample_data/config-ckan-demo.json");
			response = nr.getOrganizationCatalog("corporation-test");
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		Catalog testCatalog = new Catalog();
		CatalogException catalogException = null;
		try{
			testCatalog.loadCatalogFromJSONString(response);
		}
		catch(CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
			catalogException = e;
		}
		try{
			testCatalog.toProjectOpenDataJSON("ckan_data.json", true);
		}
		catch(IOException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		assertNull(catalogException);
	}
	
	/**
	 * Needs to create unique name every time it send in new dataset request for testing.
	 */
	@Test
	public void testCreateDataset()
	{
		fail("Not yet implemented");
	}

}
