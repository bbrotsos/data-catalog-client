package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CatalogTest {

	private static final Logger log = Logger.getLogger(CatalogTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Load CKAN organization catalog, load project open data catalog, compare.
	 */
	@Test
	public void testLoadCatalog() {
		final String catalogProjectOpenDataFileName = "sample_data/test/catalogTestProjectOpenData.json";
		final String catalogCkanFileName = "sample_data/test/catalogTestCKAN.json";

		JSONObject catalogObjectPod = null;
		JSONObject catalogObjectCkan = null;
		
		Catalog catalogPod = new Catalog();
		Catalog catalogCkan = new Catalog();
		
		try
		{
			catalogObjectPod = Utils.loadJsonObjectFile(catalogProjectOpenDataFileName);
			catalogObjectCkan = Utils.loadJsonObjectFile(catalogCkanFileName);
		}
		catch(ParseException | IOException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			catalogPod.loadFromProjectOpenDataJSON(catalogObjectPod);
			catalogCkan.loadCatalogFromCKAN_JSON(catalogObjectCkan);
		}
		catch (CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		assertEquals (catalogPod, catalogCkan);
		
	}
	
	/**
	 * Tests if invalid catalog sends out errors such as when a unique_id is used twice
	 */
	@Test
	public void testInvalidCatalog(){
		fail("Not yet implemented");
	}
	
	/**
	 * Tests adding a catalog to another catalog.  This would be a merge but there must be a choice
	 * in the catalog level attributes.
	 */
	@Test
	public void testAddCatalogToCatalog(){
		fail("Not yet implemented");
	}
	
	/**
	 * This tests bureau names adding to datasets correctly
	 */
	@Test
	public void testAddBureauNamesToDatasets(){
		final String catalogProjectOpenDataFileName = "sample_data/test/catalogTestProjectOpenData.json";

		JSONObject catalogObjectPod = null;
		
		Catalog catalogPod = new Catalog();
		
		try
		{
			catalogObjectPod = Utils.loadJsonObjectFile(catalogProjectOpenDataFileName);
		}
		catch(ParseException | IOException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			catalogPod.loadFromProjectOpenDataJSON(catalogObjectPod);
		}
		catch (CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			catalogPod.toCSV("sample_data/test/catalog.csv", Catalog.DataListingCode.PUBLIC_DATA_LISTING);
		}
		catch(IOException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
	}


}
