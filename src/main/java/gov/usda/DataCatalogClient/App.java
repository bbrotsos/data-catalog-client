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
 
    	catalog = odpClient.loadOrganizationsIntoCatalog();
    	catalog.toProjectOpenDataJSON("data.json");
    	
    	catalog.produceQuarterlyReport("quarterly_report.doc");
    	catalog.produceBureauMetrics("bureau_metrics.csv");
    	
    	//Add new dataset
    	Dataset ds = new Dataset();
    	ds.setTitle("My New Title");
    	ds.setDescription("New dataset for CKAN");
    	
    	odpClient.createDataset(ds);
    }
    
}
