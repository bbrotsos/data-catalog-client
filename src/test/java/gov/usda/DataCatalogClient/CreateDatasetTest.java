/**
 * 
 */
package gov.usda.DataCatalogClient;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
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

	private static final Logger log = Logger.getLogger(CreateDatasetTest.class.getName());

	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/*
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
	*/
	
	@Test
	public void testCKAN_load()
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
		catch (IOException | ParseException e) 
		{
			Assert.fail(e.toString());
		}
		
		//run ds.loadJSON
		
		Dataset ds = new Dataset();
		try{
			ds.loadDatasetFromCKAN_JSON(datasetCKAN_JSON);
		}catch (DatasetException e)
		{
			Assert.fail (e.toString());
		}
		
		Assert.assertEquals(datasetCKAN_JSON, ds.toCKAN_JSON());
		
		//test the values
	}
	
	@Test
	public void testProjectOpenDataLoad()
	{
		String samplePOD_DatasetFileName = "sample_data/project_open_data_dataset.json";
		JSONObject datasetJSON = null;
		
		try{
			datasetJSON = Utils.loadJsonObjectFile(samplePOD_DatasetFileName);
		}catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		Dataset ds = new Dataset();
		try{
			ds.loadFromProjectOpenDataJSON((JSONObject)datasetJSON);
		}catch (DatasetException e)
		{
			Assert.fail (e.toString());
		}
		
		try {
			Utils.printJSON("sample_data/output/open_data_dataset.json", ds.toProjectOpenDataJSON());
		} catch (IOException e) {
			Assert.fail (e.toString());
		}
		
		String input="";
		String output="";
		
		try{
			input = new String(Files.readAllBytes(Paths.get("sample_data/project_open_data_dataset.json")));
			output = new String (Files.readAllBytes(Paths.get("sample_data/output/open_data_dataset.json")));
		}
		catch (IOException ex)
		{
			Assert.fail(ex.toString());
		}
		
		//strips spacing
		input.replaceAll("\\s+","");
		output.replaceAll("\\s+","");
		
		Assert.assertEquals(input,output);
		
	}
	
	/**
	 * Loads CKAN based dataset and loads Project Open dataset.  compare.
	 * This is flawed because this software might do both wrong, but it has some coverage.
	 */
	@Test
	public void testDatasetLoading()
	{
		JSONObject podJSON = new JSONObject();
		JSONObject ckanJSON = new JSONObject();
		
		Dataset projectOpenDataset = new Dataset();
		Dataset ckanDataset = new Dataset();
		
		String podFileName = "sample_data/test/project_open_data_dataset.json";
		String ckanFileName = "sample_data/test/sample_ckan_package.json";
		
		try{
			podJSON= Utils.loadJsonObjectFile(podFileName);
			ckanJSON = Utils.loadJsonObjectFile(ckanFileName);
		}
		catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		try{
			projectOpenDataset.loadFromProjectOpenDataJSON(podJSON);
			ckanDataset.loadDatasetFromCKAN_JSON(ckanJSON);
		}
		catch(DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		Assert.assertEquals(projectOpenDataset, ckanDataset);
	}
	
	/**
	 * Give loadDataset with no required fields and should return errors.
	 */
	@Test
	public void testDatasetRequiredFields()
	{
		String invalidPODFileName = "sample_data/test/no_required_project_open_data_dataset.json";
		JSONObject podJSON = new JSONObject();
		Dataset projectOpenDataset = new Dataset();

		try{
			podJSON = Utils.loadJsonObjectFile(invalidPODFileName);
		}
		catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		try{
			projectOpenDataset.loadFromProjectOpenDataJSON(podJSON);
		}
		catch(DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
	}
	
	
	/**
	 * Test the following fields:
	 * Intentional Fail on all URLs that are not urls
	 * Intentional Fail on bureaucodes != nnn:nn
	 * Intentional Fail on programecodes != nnn:nnn
	 * Intentional Fail on email addresses != valid emailAddress
	 * 
	 */
	@Test
	public void testInvalidFields()
	{
		String invalidPODFileName = "sample_data/test/invalid_project_open_data.json";
		JSONObject podJSON = new JSONObject();
		Dataset projectOpenDataset = new Dataset();

		try{
			podJSON = Utils.loadJsonObjectFile(invalidPODFileName);
		}
		catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		try{
			projectOpenDataset.loadFromProjectOpenDataJSON(podJSON);
		}
		catch(DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
	}

	

}
