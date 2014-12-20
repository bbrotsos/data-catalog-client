/**
 * 
 */
package gov.usda.DataCatalogClient;

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
		try
		{
			ds.setBureauCode("12345");
		}
		catch (DatasetException dsEx)
		{
			assertEquals (dsEx.toString(), "[Dataset Error, Bureau Code must be \\d{3}:\\d{2}: 12345]");
		}
		try
		{
			ds.setBureauCode("015:03");
		}
		catch(DatasetException dsEx)
		{
			Assert.fail("bureau should pass this regex test");;
		}
		assertEquals (ds.getBureauCode(), "015:03");
	}
	
	@Test
	public void testCreateDatasetFromCKAN_JSON()
	{
		//get sample json
		
		//run ds.loadJSON
		
		//test the values
	}

}
