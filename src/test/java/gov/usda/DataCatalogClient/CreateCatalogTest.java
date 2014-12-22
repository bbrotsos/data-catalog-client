package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CreateCatalogTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateCatalog()
	{
		String sampleCatalogCKAN_JSON = "";
		String sample_CKAN_Catalog_path = "sample_data/sample_ckan_catalog_result.json";
		Object obj = new Object();
		JSONObject resourceCKAN_JSON = new JSONObject();

		try 
		{
			sampleCatalogCKAN_JSON = new String(Files.readAllBytes(Paths.get(sample_CKAN_Catalog_path)));
			JSONParser parser = new JSONParser();
			obj = parser.parse(sampleCatalogCKAN_JSON);
			resourceCKAN_JSON = (JSONObject)obj;
		} 
		catch (IOException | ParseException pe) 
		{
			Assert.fail(pe.toString());
			System.out.println(pe.toString());
		}
		
		//run ds.loadJSON
		
		Catalog catalog = new Catalog();
		catalog.loadCatalogFromCKAN_JSON(resourceCKAN_JSON);
		
		Assert.assertEquals(resourceCKAN_JSON, catalog.toCKAN_JSON());
	}
	
	@Test
	public void testProjectOpenDataOutput()
	{
		String sample_CKAN_Catalog_path = "sample_data/sample_ckan_catalog_result.json";
		Catalog catalog = new Catalog();
		catalog.loadCatalogFromCKAN(sample_CKAN_Catalog_path);
		catalog.toProjectOpenDataJSON("sample_data/sample_project_open_data_catalog.json");
		
		catalog.outputCSV("sample_data/sample_csv.txt");
		
		//assert that it is valid here
		
	}

}
