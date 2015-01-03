package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CatalogTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Load CKAN organization cataglog, load project open data catalog, compare.
	 */
	@Test
	public void testLoadCatalog() {
		fail("Not yet implemented");
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


}
