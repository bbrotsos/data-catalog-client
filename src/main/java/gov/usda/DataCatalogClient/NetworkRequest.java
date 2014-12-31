package gov.usda.DataCatalogClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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
	private HttpURLConnection connection_public;
	private String server;
	private String apiKey;
	
	public NetworkRequest () throws IOException, ParseException
	{
		//Get Server and API Key
		loadServerAndAPI_Key();
	}
	
	private void loadServerAndAPI_Key() throws IOException, ParseException
	{
		String configStringJSON = "";
		String config_path = "sample_data/config.json";
		JSONObject configJSON = new JSONObject();
			
		configStringJSON = new String(Files.readAllBytes(Paths.get(config_path)));
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(configStringJSON);
		configJSON = (JSONObject) obj;
		server = (String)configJSON.get("server");
		apiKey = (String)configJSON.get("api_key");
	}
	
	private void setupConnection()
	{
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Authorization", apiKey);
		connection.setRequestProperty("Cookie", "auth_tkt=hello_world");
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
	}	
	
	//optimize or remove in future, setup in config file: Development, Test, Prod.
	//i hit wall moving from https, to http.  clean up later.
	private void setupPublicConnection()
	{
		connection_public.setRequestProperty("Accept-Charset", "UTF-8");
		connection_public.setRequestProperty("Accept", "application/json");
		connection_public.setRequestProperty("Authorization", apiKey);
		connection_public.setRequestProperty("Cookie", "auth_tkt=hello_world");
		connection_public.setConnectTimeout(20000);
		connection_public.setReadTimeout(20000);
	}
	
	public String getOrganizationCatalog(String organization) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/organization_show?id=" + organization);
		if (dataAPIURL.getProtocol().toLowerCase().equals("https"))
		{
			
		}
		else
		{
			connection = (HttpURLConnection)dataAPIURL.openConnection();
		}
		connection = (HttpsURLConnection)dataAPIURL.openConnection();
		setupConnection();
		connection.setRequestProperty("Content-Type", "application/json");
		return getHttpResponse(connection);
	}
	
	public String getDataset(String name) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/package_show?id=" + name);
		
		connection_public = (HttpURLConnection)dataAPIURL.openConnection();
		setupPublicConnection();
		connection_public.setRequestProperty("Content-Type", "application/json");
		return getHttpResponse(connection_public);
	}
	
	public String createDataset(JSONObject postJSON) throws IOException
	{
		//get rid of connection_public...using it for connecting to non-ssl
		URL dataAPIURL = new URL(server + "/api/3/action/package_create");
		connection_public = (HttpURLConnection)dataAPIURL.openConnection();
		setupPublicConnection();
		connection_public.setRequestProperty("Content-Type", "application/json");
		connection_public.setDoOutput(true);

		postObject(connection_public, postJSON);
		return getHttpResponse(connection_public);
	}
	
	public String updateDataset(String name, JSONObject postJSON) throws IOException
	{
		//get rid of connection_public...using it for connecting to non-ssl
		URL dataAPIURL = new URL(server + "/api/3/action/package_update");
		connection_public = (HttpURLConnection)dataAPIURL.openConnection();
		setupPublicConnection();
		connection_public.setRequestProperty("Content-Type", "application/json");
		connection_public.setDoOutput(true);

		postObject(connection_public, postJSON);
		return getHttpResponse(connection_public);
	}
	
	public String  deleteDataset(String name, JSONObject postJSON) throws IOException
	{
		//get rid of connection_public...using it for connecting to non-ssl
		URL dataAPIURL = new URL(server + "/api/3/action/package_delete");
		connection_public = (HttpURLConnection)dataAPIURL.openConnection();
		setupPublicConnection();
		connection_public.setRequestProperty("Content-Type", "application/json");
		connection_public.setDoOutput(true);

		postObject(connection_public, postJSON);
		return getHttpResponse(connection_public);
	}
	
	private void postObject(HttpURLConnection connection, JSONObject object) throws IOException
	{
		OutputStreamWriter out = null;
		try
		{
		    out = new OutputStreamWriter(connection.getOutputStream());	 
			out.write(object.toJSONString());
		}
		catch(IOException e)
		{
			throw (e);
		}
		finally
		{
			out.close();
		}
	}
	
	String getHttpResponse (HttpsURLConnection connection) throws IOException
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
		catch (IOException e)
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
	
	//This needs to be removed...testing non-https connections.
	String getHttpResponse (HttpURLConnection connection) throws IOException
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
		catch (IOException e)
		{
			throw (e);
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
