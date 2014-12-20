/**
 * 
 */
package gov.usda.DataCatalogClient;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author bbrotsos
 *
 */
public class CreateDatasetTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testCreateDataSet()
	{
		Dataset ds = new Dataset();
		
		//test regexs
		//validate bureau code
		
		ds.setBureauCodeList("12345");
		ds.setBureauCodeList("015:03");
		//assertEquals (ds.getBureauCodeList(), "015:03");
	}
	
	@Test
	public void testCreateDatasetFromCKAN_JSON()
	{
		//get sample json
		String sampleDataSetCKAN_JSON = "";
		String sample_CKAN_Dataset_path = "sample_data/sample_ckan_package.json";
		JSONObject datasetCKAN_JSON = new JSONObject();
		
		try 
		{
			sampleDataSetCKAN_JSON = new String(Files.readAllBytes(Paths.get(sample_CKAN_Dataset_path)));
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(sampleDataSetCKAN_JSON);
			datasetCKAN_JSON = (JSONObject) obj;
		} 
		catch (IOException | ParseException pe) 
		{
			Assert.fail(pe.toString());
		}
		
		//run ds.loadJSON
		
		Dataset ds = new Dataset();
		ds.loadDatasetFromCKAN_JSON(datasetCKAN_JSON);
		
		Assert.assertEquals(datasetCKAN_JSON, ds.toCKAN_JSON());
		
		//test the values
	}

}
