package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	
	static public JSONObject loadJsonObjectFile(String fileName)
	{
		Object obj = new Object();
		JSONObject jsonObject = new JSONObject();
		String jsonString = "";
		try 
		{
			jsonString = new String(Files.readAllBytes(Paths.get(fileName)));
			JSONParser parser = new JSONParser();
			obj = parser.parse(jsonString);
			jsonObject = (JSONObject) obj;
		} 
		catch (IOException | ParseException pe) 
		{
			System.out.println(pe.toString());
		}
		return jsonObject;
		
	}
	
	static public JSONArray loadJsonArrayFile(String fileName)
	{
		Object obj = new Object();
		JSONArray jsonArray = new JSONArray();
		String jsonString = "";
		try 
		{
			jsonString = new String(Files.readAllBytes(Paths.get(fileName)));
			JSONParser parser = new JSONParser();
			obj = parser.parse(jsonString);
			jsonArray = (JSONArray) obj;
		} 
		catch (IOException | ParseException pe) 
		{
			System.out.println(pe.toString());
		}
		
		return jsonArray;
		
	}
	
	static public void printJSON(String fileName, Map jsonMap)
	{
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		try
		{
			PrintWriter out = new PrintWriter(fileName);
			out.print( gson.toJson(jsonMap) );
			out.close();
		}
		catch (Exception ex)	
		{
			System.out.println(ex.toString());
		}
	}
	
	
}
