package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

	private static final Logger log = Logger.getLogger(ClientTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createEDI() {
		Catalog catalog = new Catalog();
		Catalog catalog_ag_data_commons = new Catalog();
		Catalog catalog_fs = new Catalog();

    	Client odpClient = new Client();
    	
    	try{
    		
    		catalog = odpClient.getProjectOpenDataFromURL("https://enterprisecontent-usfs.opendata.arcgis.com/data.json", "input/fs.json");
    		//catalog_ag_data_commons = odpClient.getProjectOpenDataFromURL("https://data.nal.usda.gov/data-ars.json", "input/ag-data-commons.json");
    		
    			/* 
    			 * If the Ag Data Commons times out, download fresh copy, uncomment the following line 
    			 * and comment line above that downloads from data.nal.usda.gov file.
    			 */
    			//catalog.loadFromProjectOpenDataJSON(Utils.loadJsonObjectFile("input/ag-data-commons.json"));
    			
    			/* 
    			 * If the FS times out, download fresh copy, uncomment the following line 
    			 * and comment line above that downloads from opendata.archis.com
    			 */
    			//catalog.loadFromProjectOpenDataJSON(Utils.loadJsonObjectFile("input/fs.json"));
    			
    			catalog.hardcodeBureauCodeProgramCode();

    			//catalog.loadFromProjectOpenDataJSON(Utils.loadJsonObjectFile("input/data-2018-01-31.json"));
    			//catalog.addFromOtherCatalog(catalog_ag_data_commons);
    			//catalog.addFromOtherCatalog(catalog_fs);
    		
    		
    		catalog.toCSV("output/catalog_full.csv", Catalog.DataListingCode.PUBLIC_DATA_LISTING);
    		catalog.toProjectOpenDataJSON("output/data.json", Catalog.DataListingCode.ENTERPRISE_DATA_INVENTORY);
    		catalog.toLegacyXML("output/catalog.xml", Catalog.DataListingCode.PUBLIC_DATA_LISTING);
    	}
    	catch(CatalogException | IOException e)
    	{
			log.log(Level.SEVERE, e.toString());
    	}
    
	}

}
