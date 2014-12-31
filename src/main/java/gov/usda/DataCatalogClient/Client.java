package gov.usda.DataCatalogClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Client class is used for interacting with a CKAN instance where Project Open Data attributes
 * have been added to the extra field.  
 * 
 * Create Dataset
 * 
 * To create a dataset, use method @see public void createDataset(Dataset ds).  This method
 * takes in a Project Open Data compliant dataset and imports it into CKAN.
 * 
 * Read Full Catalog
 * This class offers a method to compile a catalog from multiple CKAN organizations.
 * @see loadOrganizationsIntoCatalog(String downloadFilePath)  This method will load datasets
 * from CKAN Organizations that are in "bureau_reference_data.json" organization.  The file path parameter 
 * will be the temp location for the CKAN downloads.  To produce a fresh downloand, this filepath
 * must be renamed.
 * 
 * 
 * @author bbrotsos
 *
 */
public class Client {
	
	private static final Logger log = Logger.getLogger(Client.class.getName());
	
	private List<String> datasetErrors = new ArrayList<String>();

	/**
	 * This method takes in a dataset and updates it on CKAN.  Lots of issues with this method because
	 * of id=title of the dataset.
	 * 
	 * One approach is to search on the title, grab the CKAN id and then do a another call to 
	 * update the dataset.
	 * 
	 * 
	 * @param updateDS
	 * @return Dataset This is the dataset returned by CKAN after the update request.
	 * @throws ParseException
	 * @throws IOException
	 * @throws DatasetException
	 */
	public Dataset updateDataset(Dataset updateDS) throws ParseException, IOException, DatasetException
	{		
		//TODO: First get id, we will use this later because we can't rely on names not changing.
		Dataset ckanDataset = getDatasetFromCKAN(updateDS.getName());
		
		String datasetCKANString = "";
		NetworkRequest nr = new NetworkRequest();
		datasetCKANString = nr.updateDataset(updateDS.getName(), updateDS.toCKAN_JSON());
		
		JSONObject dataSetJSON = new JSONObject();
		Dataset ds = new Dataset();
		
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result")); 
	
		return ds;
	}
	
	/**
	 * Takes in title of data set, calls CKAN to get dataset , loads response into Dataset object, returns that
	 * object.
	 * 
	 * @param name
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws DatasetException
	 */
	public Dataset getDatasetFromCKAN(String name) throws ParseException, IOException, DatasetException
	{
		Dataset ds = new Dataset();
		String datasetCKANString = "";
		
		NetworkRequest nr = new NetworkRequest();
		datasetCKANString = nr.getDataset(name);
		JSONObject dataSetJSON = new JSONObject();
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result"));
		return ds;
	}
	
	public Dataset deleteDataset (Dataset deleteDS) throws DatasetException, ParseException, IOException
	{
		NetworkRequest nr = new NetworkRequest();
		String datasetCKANString = "";
		
		datasetCKANString = nr.deleteDataset(deleteDS.getName(), deleteDS.toCKAN_JSON());
		JSONObject dataSetJSON = new JSONObject();
		Dataset ds = new Dataset();
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result"));
		return ds;
	}
	
	/**
	 * Calls network request to get all an organizations datasets.
	 * @param organizationIdentifier
	 * @param bureauFileName
	 * @return
	 * @throws IOException
	 * @throws CatalogException
	 */
	public Catalog getOrganizationCatalogCKAN(String organizationIdentifier, String bureauFileName) throws IOException, CatalogException
	{
		
		String catalogJSONString = "";
		NetworkRequest nr;
		try{
			nr = new NetworkRequest();	
		}
		catch(ParseException e)
		{
			throw (new CatalogException(e.toString()));
		}
		
		log.log(Level.FINE, "Making Network Request for: " + organizationIdentifier);
		try
		{
			catalogJSONString = nr.getOrganizationCatalog(organizationIdentifier);
		}
		catch(IOException e)
		{
			throw (new CatalogException(e.toString()));
		}
		
		PrintWriter out = new PrintWriter(bureauFileName);
		out.print(catalogJSONString);
		out.close();
		
		Catalog catalog = new Catalog();
		try{
			catalog.loadCatalogFromJSONString(catalogJSONString);
		}
		catch(CatalogException e)
		{
			//TODO: Remove this catch
			datasetErrors.add(e.toString());
		}
		return catalog;
	}
	
	/**
	 * Loads a bureau list json array from a configuration file.
	 * @return
	 */
	public JSONArray getBureauList()
	{
		JSONArray bureauList = new JSONArray();
    	String bureauJSONString = "";
    	
		try{
    		bureauJSONString = new String(Files.readAllBytes(Paths.get("sample_data/bureau_reference_data.json")));
			JSONParser parser = new JSONParser();
			Object obj = new Object();
			obj = parser.parse(bureauJSONString);
			bureauList = (JSONArray)obj;
    	}
    	catch(IOException | ParseException pe)
    	{
    		System.out.println(pe.toString());
    	}	
		return bureauList;
	}
	
	/**
	 * Loads a catalog object of a CKAN organization from a CKAN server.  
	 * @param bureau
	 * @param bureauFileName
	 * @param downloadFilePath
	 * @return
	 * @throws IOException
	 * @throws CatalogException
	 */
	public Catalog getCatalogFromNetwork(JSONObject bureau, String bureauFileName, String downloadFilePath) throws IOException, CatalogException
	{
		createDirectory("ckan/" + downloadFilePath);
		String bureau_ckan_identifier = (String)bureau.get("bureau_ckan_identifier");
		return getOrganizationCatalogCKAN(bureau_ckan_identifier, bureauFileName);
	}
	
	/**
	 * Loads a CKAN based JSON file into a string.  
	 * @param bureauFileName
	 * @return
	 */
	public String getOrganizationFromDisk(String bureauFileName) throws IOException
	{
		String organisationJSONString = "";
		organisationJSONString = new String(Files.readAllBytes(Paths.get(bureauFileName)));
		return organisationJSONString;
	}
	

	/**
	 * This method uses file "bureau_reference_data.json" to load list of bureaus with their corresponding
	 * CKAN organization identifier.  It will first look to see if ckan/<downloadFilePath>/<bureauname> exists.
	 * If the file does not exist, it will go to CKAN server to retrieve it.
	 * 
	 * If the file exists, this method will load the existing file into a string.
	 * @param downloadFilePath
	 * @return
	 * @throws CatalogException
	 * @throws IOException
	 */
	public Catalog loadOrganizationsIntoCatalog(String downloadFilePath) throws CatalogException, IOException
	{
		Catalog entireCatalog = new Catalog();
		JSONArray bureauList = getBureauList();
    	
    	for (int i=0; i< bureauList.size(); i++)
    	{
    		JSONObject bureau = (JSONObject) bureauList.get(i);
    		String bureauFileName = "ckan/" + downloadFilePath +"/" + (String)bureau.get("bureau_abbreviation") + "-data.json";
    		File bureauFile = new File(bureauFileName);
    		if (!bureauFile.exists())
    		{
    			Catalog bureauCatalog = new Catalog();
    			bureauCatalog = getCatalogFromNetwork(bureau, bureauFileName, downloadFilePath);
    			entireCatalog.addFromOtherCatalog(bureauCatalog);
    		}
    		else
    		{
    			try
    			{
    				entireCatalog.loadCatalogFromJSONString(getOrganizationFromDisk(bureauFileName));
    			}
    			catch(CatalogException e)
    			{
    				datasetErrors.add(e.toString());
    			}
    		}
    	}
    	return entireCatalog;
	}
	
	/**
	 * Takes in a Project Open Data compliant Dataset object and creates this on CKAN server.
	 * This will return the dataset created in that call.
	 * 
	 * 
	 * @param ds
	 * @return
	 * @throws DatasetException
	 * @throws IOException
	 */
	public Dataset createDataset(Dataset ds) throws DatasetException, IOException
	{
		NetworkRequest nr;
		Dataset returnedDataset = new Dataset();
		
		try{
			nr = new NetworkRequest();
		}
		catch(ParseException e)
		{
			throw (new DatasetException("Problem with bureau_reference_data.json: " + e.toString()));
		}
		String newDatasetJSONString = "";
		newDatasetJSONString = nr.createDataset(ds.toCKAN_JSON());
		JSONObject datasetObject = new JSONObject();
		try{
			datasetObject = Utils.loadJsonObjectFromString(newDatasetJSONString);
		}
		catch(ParseException e)
		{
			throw (new DatasetException(e.toString()));
		}
		returnedDataset.loadDatasetFromCKAN_JSON(datasetObject);
		return returnedDataset;
	}
	
	/**
	 * Create a directory if it does not already exist
	 * @param filePath  Path to create file
	 * @throws IOException
	 */
	public void createDirectory(String filePath) throws IOException
	{
		Path ckanOrganizationDirectory = Paths.get(filePath);
		if (Files.notExists(ckanOrganizationDirectory))
		{
			Files.createDirectories(ckanOrganizationDirectory);
		}
	}

}
