package gov.usda.DataCatalogClient;

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
    	
    	catalog.outputCSV("datalisting.csv");
    	
    	//Add new dataset
    	Dataset ds = new Dataset();
    	ds.setTitle("My New Title");
    	ds.setDescription("New dataset for CKAN");
    	
    	odpClient.createDataset(ds);
    	
    	Dataset updateDS = new Dataset();
    	updateDS.setUniqueIdentifier("12345");
    	updateDS.setTitle("This is a different title");
    	
    	odpClient.updateDataset(updateDS);
    	
    	Dataset deleteDS = new Dataset();
    	deleteDS.setUniqueIdentifier("12345");
    	odpClient.deleteDataset(deleteDS);
    	
    }
    
}
