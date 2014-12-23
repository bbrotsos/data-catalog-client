package gov.usda.DataCatalogClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {
	
	public Dataset updateDataset(Dataset updateDS)
	{		
		//TODO: First get id, we will use this later because we can't rely on names not changing.
		Dataset ckanDataset = getDatasetFromCKAN(updateDS.getName());
		
		NetworkRequest nr = new NetworkRequest();
		String datasetCKANString = "";
		try
		{
			datasetCKANString = nr.updateDataset(updateDS.getName(), updateDS.toCKAN_JSON());
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
		JSONObject dataSetJSON = new JSONObject();
		Dataset ds = new Dataset();
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result"));
		return ds;
	}
	
	public Dataset getDatasetFromCKAN(String name)
	{
		Dataset ds = new Dataset();
		String datasetCKANString = "";
		NetworkRequest nr = new NetworkRequest();
		try
		{
			datasetCKANString = nr.getDataset(name);
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
		JSONObject dataSetJSON = new JSONObject();
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result"));
		return ds;
	}
	
	public Dataset deleteDataset (Dataset deleteDS)
	{
		NetworkRequest nr = new NetworkRequest();
		String datasetCKANString = "";
		try
		{
			datasetCKANString = nr.deleteDataset(deleteDS.getName(), deleteDS.toCKAN_JSON());
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
		JSONObject dataSetJSON = new JSONObject();
		Dataset ds = new Dataset();
		dataSetJSON = Utils.loadJsonObjectFromString(datasetCKANString);
		ds.loadDatasetFromCKAN_JSON((JSONObject)dataSetJSON.get("result"));
		return ds;
	}
	
	public Catalog getOrganizationCatalogCKAN(String organizationIdentifier, String bureauFileName)
	{
		
		String catalogJSONString = "";
		NetworkRequest nr = new NetworkRequest();	
		
		//I want feedback on when I'm hitting the server vs. hitting local file.
		System.out.println("Making Network Request for: " + organizationIdentifier);
		try
		{
			catalogJSONString = nr.getOrganizationCatalog(organizationIdentifier);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		try
		{
			PrintWriter out = new PrintWriter(bureauFileName);
			out.print(catalogJSONString);
			out.close();
		}
		catch (Exception ex)	
		{
			System.out.println(ex.toString());
		}
		
		Catalog catalog = new Catalog();
		catalog.loadCatalogFromJSONString(catalogJSONString);
		return catalog;
	}
	
	//TODO:  pull some of these functions out.  it's too complex because I added save files feature
	//       I don't want to hit server everytime i run this.
	public Catalog loadOrganizationsIntoCatalog(String downloadFilePath)
	{
		 //example create master catalog...
		Catalog masterCatalog = new Catalog();
		
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
    	
    	for (int i=0; i< bureauList.size(); i++)
    	{
    		JSONObject bureau = (JSONObject) bureauList.get(i);
    		String bureauFileName = "ckan/" + downloadFilePath +"/" + (String)bureau.get("bureau_abbreviation") + "-data.json";
    		File bureauFile = new File(bureauFileName);
    		if (!bureauFile.exists())
    		{
    			createDirectory("ckan/" + downloadFilePath);
    			Catalog bureauCatalog = new Catalog();
        		String bureau_ckan_identifier = (String)bureau.get("bureau_ckan_identifier");
    			bureauCatalog = getOrganizationCatalogCKAN(bureau_ckan_identifier, bureauFileName);
    			masterCatalog.addFromOtherCatalog(bureauCatalog);
    			//bureauCatalog.toProjectOpenDataJSON(bureauFileName);
    		}
    		else
    		{
    			String organisationJSONString = "";
    			try
    			{	
    				organisationJSONString = new String(Files.readAllBytes(Paths.get(bureauFileName)));
    			}
    			catch (IOException ex)
    			{
    				System.out.println(ex.toString());
    			}
    			masterCatalog.loadCatalogFromJSONString(organisationJSONString);
    		}
    	}
    	return masterCatalog;
	}
	
	public void createDataset(Dataset ds)
	{
		NetworkRequest nr = new NetworkRequest();
		String newDatasetJSONString = "";
		try
		{
			newDatasetJSONString = nr.createDataset(ds.toCKAN_JSON());
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	public void createDirectory(String filePath)
	{
		Path ckanOrganizationDirectory = Paths.get(filePath);
		if (Files.notExists(ckanOrganizationDirectory))
		{
			try
			{
				Files.createDirectories(ckanOrganizationDirectory);
			}
			catch (IOException ex)
			{
				System.out.print(ex.toString());
			}
		}
	}

}
