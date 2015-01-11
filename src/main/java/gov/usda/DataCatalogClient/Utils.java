package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
		//only want date for output.
		DateTime dateTime = new DateTime(date).hourOfDay().roundFloorCopy();
		DateTimeFormatter formatter =  DateTimeFormat.forPattern("yyyy-MM-dd");
		return formatter.print(dateTime);
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
	
	static public String toISO8661(String periodicity)
	{
		if (periodicity == null)
		{
			throw new NullPointerException ("Periodicity cannot be null");
		}
		if (periodicity.equals("Annual"))
		{
			return "R/P1Y";
		}
		else if (periodicity.equals("Bimonthly"))
		{
			return "R/P2M";
		}
		else if (periodicity.equals("Semiweekly"))
		{
			return "R/P3.5D";
		}
		
		else if (periodicity.equals("Daily"))
		{
			return "R/P1D";
		}
		else if (periodicity.equals("Biweekly"))
		{
			return "R/P2W";
		}
		else if (periodicity.equals("Semiannual"))
		{
			return "R/P6M";
		}
		else if (periodicity.equals("Biennial"))
		{
			return "R/P2Y";
		}
		else if (periodicity.equals("Triennial"))
		{
			return "R/P3Y";
		}
		else if (periodicity.equals("Three times a week"))
		{
			return "R/P0.33W";
		}
		else if (periodicity.equals("Three times a month"))
		{
			return "R/P0.33M";
		}
		else if (periodicity.equals("Continuously updated"))
		{
			return "R/PT1S";
		}
		else if (periodicity.equals("Monthly"))
		{
			return "R/P1M";
		}
		else if (periodicity.equals("Quarterly"))
		{
			return "R/P3M";
		}
		else if (periodicity.equals("Semimonthly"))
		{
			return "R/P0.5M";
		}
		else if (periodicity.equals("Three times a year"))
		{
			return "R/P4M";
		}
		else if (periodicity.equals("Weekly"))
		{
			return "R/P1W";
		}
		else if (periodicity.equals("Completely irregular"))
		{
			return "irregular";
		}
		else
		{
			//TODO: Test if it's already valid ISO
			throw new IllegalArgumentException("Accrual Periodicity is invalid");
		}
	}
	
}
