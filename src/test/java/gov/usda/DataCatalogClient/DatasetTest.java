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
		fail("Not yet implemented");
	}
	
	/**
	 * This looks for invalid elements such as accesslevel = "new access level", etc.
	 */
	@Test
	public void testInvalidDataset(){
		fail("Not yet implemented");
	}
	
	/**
	 * Takes in CKAN dataset, outputs dataset to 1.1, loads back in and compares.
	 */
	@Test
	public void testOutputPOD11()
	{
		
	}

}
