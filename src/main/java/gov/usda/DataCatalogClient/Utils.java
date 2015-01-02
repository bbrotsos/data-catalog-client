package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	
	static public JSONObject loadJsonObjectFile(String fileName) throws ParseException, IOException
	{
		if (fileName == null)
		{
			throw new NullPointerException ("fileName cannot be Null");
		}
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
		catch (IOException | ParseException e) 
		{
			throw (e);
		}
		return jsonObject;
		
	}
	
	static public JSONArray loadJsonArrayFile(String fileName) throws ParseException, IOException
	{
		if (fileName == null)
		{
			throw new NullPointerException ("fileName cannot be Null");
		}
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
		catch (IOException | ParseException e) 
		{
			throw (e);
		}
		
		return jsonArray;
		
	}
	
	static public JSONObject loadJsonObjectFromString(String jsonString) throws ParseException
	{
		if (jsonString == null)
		{
			throw new NullPointerException ("jsonString cannot be Null");
		}
		Object obj = new Object();
		JSONObject jsonObject = new JSONObject();
		try 
		{
			JSONParser parser = new JSONParser();
			obj = parser.parse(jsonString);
			jsonObject = (JSONObject) obj;
		} 
		catch (ParseException e) 
		{
			throw (e);
		}
		return jsonObject;
	}
	
	static public void printJSON(String fileName, JSONObject jsonMap) throws IOException
	{
		if (fileName == null || jsonMap == null)
		{
			throw new NullPointerException("fileName or jsonMap cannot be null");
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(fileName);
			out.print( gson.toJson(jsonMap) );
			out.close();
		}
		catch (IOException e)	
		{
			throw (e);
		}
		finally{
			out.close();
		}
	}
	
	static public String convertDateToISOString(Date date)
	{
		if (date == null)
		{
			throw new NullPointerException("date cannot be null");
		}
		DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String isoDateString = isoDateTimeFormat.format(date);
		
		return isoDateString;
	}
	
	//TODO: might be issues with SimpleDateFormat in static method.
	//qualify ParseException because this is also thrown by json.simple.
	static public Date convertISOStringToDate(String isoDateString) throws java.text.ParseException 
	{
		if (isoDateString == null)
		{
			throw new NullPointerException("isoDateString cannot be null");
		}
		Date isoFormattedDate = new Date();
		DateFormat isoDateTimeFormat = null;
		if (isoDateString.contains("T") && isoDateString.contains("Z"))
		{
			isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		else if (isoDateString.contains("T"))
		{
			isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		}
		else 
		{
			isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
		}
		isoDateTimeFormat.parse(isoDateString);
		
		return isoFormattedDate;
	}

	static public String listToCSV(List<String> list)
	{
		if (list.size() == 0)
		{
			return null;
		}
		String csvString = "";
		for (String s: list)
		{
			if (csvString.equals(""))
			{
				csvString = s;
			}
			else
			{
				csvString = csvString + ", " + s;
			}
		}
		return csvString;
	}
	
}
