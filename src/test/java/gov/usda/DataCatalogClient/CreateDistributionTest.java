package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CreateDistributionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateDistribution()
	{
		String sampleDistributionCKAN_JSON = "";
		String sample_CKAN_Distribution_path = "sample_data/sample_ckan_resource.json";
		JSONObject resourceCKAN_JSON = new JSONObject();
		
		try 
		{
			sampleDistributionCKAN_JSON = new String(Files.readAllBytes(Paths.get(sample_CKAN_Distribution_path)));
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(sampleDistributionCKAN_JSON);
			resourceCKAN_JSON = (JSONObject) obj;
		} 
		catch (IOException | ParseException pe) 
		{
			Assert.fail(pe.toString());
		}
		
		//run ds.loadJSON
		
		Distribution distribution = new Distribution();
		distribution.loadDistributionFromCKAN_JSON(resourceCKAN_JSON);
		
		Assert.assertEquals(resourceCKAN_JSON, distribution.toCKAN_JSON());
	}

}
