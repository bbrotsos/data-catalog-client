package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

/**
 * Example Client use
 *
 */
public class App 
{
	private static final Logger log = Logger.getLogger(App.class.getName());
	
    public static void main( String[] args )
    {
    	Catalog catalog = new Catalog();
    	Client odpClient = new Client();
 
    	//take in filepath to store saved downloads from network, update filepath for fresh results
    	try{
    		catalog = odpClient.loadOrganizationsIntoCatalog("edi_2014-12-30");
    	}
    	catch(CatalogException | IOException e)
    	{
			log.log(Level.SEVERE, e.toString());
    	}
    
    	if (catalog.validateCatalog())
    	{
    		try{
    			catalog.toProjectOpenDataJSON("data.json",Catalog.DataListingCode.PUBLIC_DATA_LISTING);
    		}
    		catch(IOException e)
    		{
    			log.log(Level.SEVERE, e.toString());
    		}
    	}
    	System.out.println("Total Count" + catalog.size());
    	
    	catalog.produceQuarterlyReport("quarterly_report.doc");
    	catalog.produceBureauMetrics("bureau_metrics.csv");
    	
    	
    	//catalog.outputCSV("datalisting.csv");
    	
    	//Add new dataset
    	Dataset ds = new Dataset();
    	try{
        	JSONObject createObject = Utils.loadJsonObjectFile("sample_data/project_open_data_dataset_full.json");
    		ds.loadFromProjectOpenDataJSON(createObject);
    	}
    	catch (Exception e)
    	{
    		log.log(Level.SEVERE, e.toString(), e);
    	}
    	
    	//odpClient.createDataset(ds);
    	//ds.setDescription("This is a new description");
    	//odpClient.updateDataset(ds);
    	
    	//odpClient.deleteDataset(ds);
    	
    }
    
}
