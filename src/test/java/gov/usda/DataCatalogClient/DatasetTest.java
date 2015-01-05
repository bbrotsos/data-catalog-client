package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatasetTest {

	private static final Logger log = Logger.getLogger(DatasetTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * This test loads same dataset from CKAN and POD and compares.
	 */
	@Test
	public void testLoadDataset() {
		Dataset dsPOD = new Dataset();
		Dataset dsCKAN = new Dataset();

		final String datasetTestProjectOpenDataFile = "sample_data/test/datasetTestProjectOpenData.json";
		final String datasetTestCKANFile = "sample_data/test/datasetTestCKAN.json";

		JSONObject datasetPOD_JSON = new JSONObject();
		JSONObject datasetCKAN_JSON = new JSONObject();

		
		try{
			datasetPOD_JSON = Utils.loadJsonObjectFile(datasetTestProjectOpenDataFile);
			datasetCKAN_JSON = Utils.loadJsonObjectFile(datasetTestCKANFile);
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			dsPOD.loadFromProjectOpenDataJSON(datasetPOD_JSON);
			dsCKAN.loadDatasetFromCKAN_JSON(datasetCKAN_JSON);
		}
		catch(DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		assertEquals(dsPOD, dsCKAN);
	}
	
	/**
	 * This tests the required Project Open Data business rules
	 */
	@Test
	public void testRequiredDatasetFields(){
		Dataset dsPOD = new Dataset();
		final String datasetTestProjectOpenDataFile = "sample_data/test/datasetTestRequiredMissing_POD.json";
		JSONObject datasetPOD_JSON = new JSONObject();
		DatasetException datasetException = null;
		
		try{
			datasetPOD_JSON = Utils.loadJsonObjectFile(datasetTestProjectOpenDataFile);
		}
		catch(Exception e)
		{
			Assert.fail(e.toString());
		}
		
		try{
			dsPOD.loadFromProjectOpenDataJSON(datasetPOD_JSON);
		}
		catch(DatasetException e){
			datasetException = e;
			assertEquals(e.toString(), "Dataset error for title: null [Publisher cannot be null], Contact Invalid: [contact cannot be empty], Title is required., Description is required., At least one tag is required., Modified is required., [Publisher invalid: Name is required], Contact Invalid: [Full Name is required, Email Address is required], Identifier is required., Access Level is required., Bureau Code is required., Program Code is required.");
		}
		
		assertNotNull(datasetException);
		
		dsPOD = null;
		datasetPOD_JSON = null;
		
		//test accesslevel = public and no distributions
		final String datasetMissingDistributionFile = "sample_data/test/datasetTestRequiredMissingAccessLevel_POD.json";
		try{
			datasetPOD_JSON = Utils.loadJsonObjectFile(datasetMissingDistributionFile);
		}
		catch(Exception e)
		{
			Assert.fail(e.toString());
		}
		
		try{
			dsPOD = new Dataset();
			dsPOD.loadFromProjectOpenDataJSON(datasetPOD_JSON);
		}
		catch(DatasetException e){
			datasetException = e;
			assertEquals(e.toString(), "Dataset error for title: null [Publisher cannot be null], Contact Invalid: [contact cannot be empty], Title is required., Description is required., At least one tag is required., Modified is required., [Publisher invalid: Name is required], Contact Invalid: [Full Name is required, Email Address is required], Identifier is required., At least one distribution is required when dataset is public or restricted., Bureau Code is required., Program Code is required.");
		}
		
		assertNotNull(datasetException);
	}
	
	private void runDatasetTest(String fileName, String expectedErrorText)
	{
		Dataset dsPOD = new Dataset();
		final String datasetTestProjectOpenDataFile = fileName;
		JSONObject datasetPOD_JSON = new JSONObject();
		DatasetException datasetException = null;
		
		try{
			datasetPOD_JSON = Utils.loadJsonObjectFile(datasetTestProjectOpenDataFile);
		}
		catch(Exception e)
		{
			Assert.fail(e.toString());
		}
		
		try{
			dsPOD.loadFromProjectOpenDataJSON(datasetPOD_JSON);
		}
		catch(DatasetException e){
			datasetException = e;
			log.log(Level.SEVERE, e.toString());
			assertEquals(e.toString(),  expectedErrorText);
		}
		
		assertNotNull(datasetException);
	}
	
	/**
	 * This looks for invalid elements such as accesslevel = "new access level", invalid bureauCodes
	 * invalid urls, invalid programcodes, invalid primary_investment_uii
	 */
	@Test
	public void testInvalidDataset(){
		runDatasetTest("sample_data/test/datasetInvalid_POD.json", "Dataset error for title: Test Resources and Distribution field java.text.ParseException: Bureau Code must be \\d{3}:\\d{2}: 018:101, java.text.ParseException: Program Code must be \\d{3}:\\d{3}: 018:0021, Bureau Code is required., Program Code is required.");
	}
	
	/**
	 * Takes in CKAN dataset, outputs dataset to 1.1, loads back in and compares.
	 */
	//TODO combine this with testLoadDataset test
	@Test
	public void testOutputPOD11()
	{
		final String outputPOD = "sample_data/test/datasetOutputPOD.json";
		Dataset dsPOD = new Dataset();
		Dataset dsCKAN = new Dataset();

		final String datasetTestCKANFile = "sample_data/test/datasetTestCKAN.json";

		JSONObject datasetPOD_JSON = new JSONObject();
		JSONObject datasetCKAN_JSON = new JSONObject();

		try{			
			datasetCKAN_JSON = Utils.loadJsonObjectFile(datasetTestCKANFile);
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			dsCKAN.loadDatasetFromCKAN_JSON(datasetCKAN_JSON);
			datasetPOD_JSON = dsCKAN.toProjectOpenDataJSON();
			dsPOD.loadFromProjectOpenDataJSON(datasetPOD_JSON);
		}
		catch(DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}
		
		assertEquals(dsPOD, dsCKAN);
		
	}

}
