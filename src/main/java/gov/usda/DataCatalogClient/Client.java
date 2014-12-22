package gov.usda.DataCatalogClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {
	
	
	public Catalog getOrganizationCatalogCKAN(String organizationIdentifier)
	{
		Path ckanOrganizationDirectory = Paths.get("ckan/");
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
		
		String catalogJSONString = "";
		NetworkRequest nr = new NetworkRequest();		
		try
		{
			catalogJSONString = nr.getOrganizationCatalog(organizationIdentifier);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		Catalog catalog = new Catalog();
		catalog.loadCatalogFromCKAN(catalogJSONString);
		return catalog;
	}
	
	public Catalog loadOrganizationsIntoCatalog()
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
    		String bureau_ckan_identifier = (String)bureau.get("bureau_ckan_identifier");
    		String bureauFileName = "ckan/" + (String)bureau.get("bureau_abbreviation") + "-data.json";
    		File bureauFile = new File(bureauFileName);
    		if (!bureauFile.exists())
    		{
    			Catalog bureauCatalog = new Catalog();
    			bureauCatalog = getOrganizationCatalogCKAN(bureau_ckan_identifier);
    			masterCatalog.addFromOtherCatalog(bureauCatalog);
    			bureauCatalog.toProjectOpenDataJSON(bureauFileName);
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
		
	}

}
