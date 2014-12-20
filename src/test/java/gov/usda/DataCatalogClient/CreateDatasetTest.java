/**
 * 
 */
package gov.usda.DataCatalogClient;

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
		assertEquals(0,0);
	}

}
