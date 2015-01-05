package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	//Could probably combine loadJSONObject and loadJSONArray
	//It would put more work on the clients.
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
			if (!(obj instanceof JSONObject))
			{
				throw new IllegalArgumentException(fileName + " is invalid JSON file for this request.  Expecting JSONObject.");
			}
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
		DateTime dateTime = new DateTime(date);
		return dateTime.toString();
	}
	
	//TODO: might be issues with SimpleDateFormat in static method.
	//qualify ParseException because this is also thrown by json.simple.
	/**
	 * This goes for simple cases so there is not the need to add yet 
	 * another library.  Might want to go for full library in the future.
	 * 
	 * UPDATE: Switched to joda time, may look to optimize in the future.
	 * @param isoDateString
	 * @return
	 * @throws java.text.ParseException
	 */
	static public Date convertISOStringToDate(String isoDateString) throws java.text.ParseException 
	{
		DateTime isoDateTime = DateTime.parse(isoDateString);
		
		return isoDateTime.toDate();
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
	
	static public JSONArray getBureauList() throws ParseException, IOException
	{
		return Utils.loadJsonArrayFile("sample_data/bureau_reference_data.json");
	}
	
}
