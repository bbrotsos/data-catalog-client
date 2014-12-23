package gov.usda.DataCatalogClient;

import org.json.simple.JSONObject;

/**
 * Example Client use
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Catalog catalog = new Catalog();
    	Client odpClient = new Client();
 
    	//take in filepath to store saved downloads from network, update filepath for fresh results
    	
    	
    	catalog = odpClient.loadOrganizationsIntoCatalog("edi_2014-12-22");
    
    	if (catalog.validateCatalog())
    	{
    		Boolean privateIndicator = false;
    		catalog.toProjectOpenDataJSON("data.json", privateIndicator);
    	}
    	
    	catalog.produceQuarterlyReport("quarterly_report.doc");
    	catalog.produceBureauMetrics("bureau_metrics.csv");
    	
    	
    	//catalog.outputCSV("datalisting.csv");
    	
    	//Add new dataset
    	Dataset ds = new Dataset();
    	JSONObject createObject = Utils.loadJsonObjectFile("sample_data/project_open_data_dataset_full.json");
    	ds.loadFromProjectOpenDataJSON(createObject);
    	
    	odpClient.createDataset(ds);
    	//ds.setDescription("This is a new description");
    	//odpClient.updateDataset(ds);
    	
    	//odpClient.deleteDataset(ds);
    	
    }
    
}
