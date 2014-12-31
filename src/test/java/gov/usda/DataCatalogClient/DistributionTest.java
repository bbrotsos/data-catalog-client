package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DistributionTest {

	private static final Logger log = Logger.getLogger(DistributionTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Load a distribution from CKAN, load distribution from Project Open Data and compare.
	 */
	@Test
	public void testLoadDistribution() {
		String distributionTestFile = "sample_data/test/distributionTestProjectOpenData.json";
		String distributionCKAN_TestFile = "sample_data/test/distributionTestCKAN.json";

		List<Distribution> distributionList = new ArrayList<Distribution>();
		List<Distribution> distributionListOther = new ArrayList<Distribution>();
		
		JSONArray distributionArray = new JSONArray();
		JSONArray distributionArrayOther = new JSONArray();

		try{
			distributionArray = Utils.loadJsonArrayFile(distributionTestFile);
			distributionArrayOther = Utils.loadJsonArrayFile(distributionCKAN_TestFile);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		try
		{
			//load Project Open Data formatted
			for (int i = 0; i < distributionArray.size(); i++)
			{
				Distribution distribution = new Distribution();
				JSONObject distributionObject = new JSONObject();
				distributionObject = (JSONObject)distributionArray.get(i);
				distribution.loadFromProjectOpenDataJSON(distributionObject);
				distributionList.add(distribution);
			}
			
			//load CKAN formatted
			for (int i = 0; i < distributionArrayOther.size(); i++)
			{
				Distribution distribution = new Distribution();
				JSONObject distributionObject = new JSONObject();
				distributionObject = (JSONObject)distributionArrayOther.get(i);
				distribution.loadDistributionFromCKAN_JSON(distributionObject);
				distributionListOther.add(distribution);
			}
		}
		catch(DistributionException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail(e.toString());
		}

		assertEquals(distributionList, distributionListOther);
	}
	
	/**
	 * Make sure that when required fields are not provided correct error is sent.  Send in null
	 */
	@Test
	public void testRequiredDistribution() {
		fail("Not yet implemented");
	}
	
	/**
	 * Make sure that when required fields are not provided correct error is sent. Send in invalid data.
	 */
	@Test
	public void testValidDistribution() {
		fail("Not yet implemented");
	}
	
	/**
	 * Loads a distribution, outputs into POD, inputs back into another object. Compare.
	 */
	@Test
	public void testRoundTrip(){
		
	}

}
