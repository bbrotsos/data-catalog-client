package gov.usda.DataCatalogClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NetworkRequest 
{
	private HttpsURLConnection connection;
	private String server;
	private String apiKey;
	
	public NetworkRequest ()
	{
		//Get Server and API Key
		loadServerAndAPI_Key();
	}
	
	private void loadServerAndAPI_Key()
	{
		String configStringJSON = "";
		String config_path = "sample_data/config.json";
		JSONObject configJSON = new JSONObject();
			
		try 
		{
			configStringJSON = new String(Files.readAllBytes(Paths.get(config_path)));
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(configStringJSON);
			configJSON = (JSONObject) obj;
			server = (String)configJSON.get("server");
			apiKey = (String)configJSON.get("webAPI_Key");
		} 
		catch (IOException | ParseException pe) 
		{
			System.out.println (pe.toString());
		}
	}
	
	private void setupConnection()
	{
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Authorization", apiKey);
		connection.setRequestProperty("Cookie", "");
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
	}	
	
	public String getOrganizationCatalog(String organization) throws Exception
	{
		URL dataAPIURL = new URL(server + "/api/3/action/organization_show?id=" + organization);
		connection = (HttpsURLConnection)dataAPIURL.openConnection();
		setupConnection();
		connection.setRequestProperty("Content-Type", "application/json");
		return getHttpResponse(connection);
	}
	
	String getHttpResponse (HttpsURLConnection connection) throws Exception
	{
		BufferedReader in = null;
		String response="";
		int responseCode = connection.getResponseCode();
		
		try
		{
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			response = "";
			while ((inputLine = in.readLine()) != null) 
			{
				response = response + inputLine;
			}	
			System.out.println();
			System.out.println(responseCode);
		}
		catch (Exception e)
		{
			if (responseCode == 422)
			{
				in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String inputLine;
				response = "";
				while ((inputLine = in.readLine()) != null) 
				{
					response = response + inputLine;
				}	
				//throw new NetworkProcessingException(response);
			}
			else
			{
				throw (e);
			}
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			connection.disconnect();
		}
		
		return response;
	}
	
}
