package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Tests for Contact class.
 * @author bbrotsos
 *
 */
public class ContactTest {

	private static final Logger log = Logger.getLogger(Contact.class.getName());
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that Contact loads from Project Open Data JSON Object correctly.  
	 */
	@Test
	public void testLoadContact() 
	{
		Contact contactPoint = new Contact();
		Contact contactPointCompare = new Contact();
		JSONObject contactObject = new JSONObject();
		
		String invalidContactTestFile = "sample_data/test/contactPointTest.json";
		try{
			contactObject = Utils.loadJsonObjectFile(invalidContactTestFile);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		try
		{
			contactPoint.loadDatasetFromPOD_JSON(contactObject);
		}
		catch(ContactException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		
		contactPointCompare.setType("vcard:Contact");
		contactPointCompare.setFullName("Jane Doe");
		contactPointCompare.setEmailAddress("mailto:jane.doe@us.gov");
		
		assertEquals(contactPoint, contactPointCompare);
	}
	
	/**
	 * Test that Contact class handles passing null correctly
	 */
	@Test
	public void testNullContact()
	{
		Contact contactPoint = new Contact();
		try
		{
			contactPoint.loadDatasetFromPOD_JSON(null);
		}
		catch(ContactException e)
		{
			assertEquals (e.toString(), "Contact Invalid: [contact cannot be empty]");
		}
	}

	/**
	 * Tests that Contact class will throw errors for not having required fields.
	 */
	@Test
	public void testRequiredContactAttributes() 
	{
		Contact contactPoint = new Contact();
		JSONObject contactObject = new JSONObject();
		ContactException contactException = null;
		
		String invalidContactTestFile = "sample_data/test/contactPointTestRequiredFields.json";
		try{
			contactObject = Utils.loadJsonObjectFile(invalidContactTestFile);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		try
		{
			contactPoint.loadDatasetFromPOD_JSON(contactObject);
		}
		catch(ContactException e)
		{
			assertEquals(e.toString(), "Contact Invalid: [Full Name is required, Email Address is required]");
			contactException = e;
		}		
		assertNotNull(contactException);

	}
	
	/**
	 * Test that Contact class with throw error for invalid email address like jane.doe without the @us.gov
	 */
	@Test
	public void testInvalidEmail() 
	{
		Contact contactPoint = new Contact();
		JSONObject contactObject = new JSONObject();
		ContactException contactException = null;
		
		String invalidContactTestFile = "sample_data/test/contactPointTestInvalid.json";
		try{
			contactObject = Utils.loadJsonObjectFile(invalidContactTestFile);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		try
		{
			contactPoint.loadDatasetFromPOD_JSON(contactObject);
		}
		catch(ContactException e)
		{
			assertEquals(e.toString(), "Contact Invalid: [Email Address: jane.doe is not a valid address.]");
			contactException = e;
		}
		
		assertNotNull(contactException);
	}
}
