package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	
	/**
	 * Change legacy Project Open Data frequency to ISO 8661
	 * @param periodicity
	 * @return
	 */
	static public String toISO8661(String periodicity)
	{
		if (periodicity == null)
		{
			throw new NullPointerException ("Periodicity cannot be null");
		}
	
		Map<String, String> isoFrequencyMapping = new HashMap<String, String>();
		isoFrequencyMapping.put("Annual", "R/P1Y");
		isoFrequencyMapping.put("Bimonthly", "R/P2M");
		isoFrequencyMapping.put("Semiweekly", "R/P3.5D");
		isoFrequencyMapping.put("Daily", "R/P1D");
		isoFrequencyMapping.put("Biweekly", "R/P2W");
		isoFrequencyMapping.put("Semiannual", "R/P6M");
		isoFrequencyMapping.put("Biennial", "R/P2Y");
		isoFrequencyMapping.put("Triennial", "R/P3Y");
		isoFrequencyMapping.put("Three times a week", "R/P0.33W");
		isoFrequencyMapping.put("Three times a month", "R/P0.33M");
		isoFrequencyMapping.put("Continuously updated","R/PT1S");
		isoFrequencyMapping.put("Monthly", "R/P1M");
		isoFrequencyMapping.put("Quarterly", "R/P3M");
		isoFrequencyMapping.put("Semimonthly", "R/P0.5M");
		isoFrequencyMapping.put("Three times a year", "R/P4M");
		isoFrequencyMapping.put("Weekly", "R/P1W");
		isoFrequencyMapping.put("Completely irregular", "irregular");
		
		for (Entry<String, String> isoEntry: isoFrequencyMapping.entrySet())
		{
			if (periodicity.equals(isoEntry.getKey()))
			{
				return isoEntry.getValue();
			}
			else if (periodicity.equals(isoEntry.getValue()))
			{
				//This case is when a valid ISO frequency was sent in as a parameter.
				return periodicity;
			}
		}
		//value not valid legacy Project Open Data or ISO 8661
		throw new IllegalArgumentException("Accrual Periodicity is invalid");
		
	}
	
}
