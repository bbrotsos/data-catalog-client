package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Catalog {

	//Documentation on DCAT here: http://www.w3.org/TR/vocab-dcat/
	private String title;
	private String description;
	private Date issued;
	private String language;
	private String license;
	private String rights;
	private String spatial;
	private String homepage;
	
	//Project Open Data additional fields: https://project-open-data.cio.gov/v1.1/schema/#accessLevel
	private String context;
	private String id;
	private String type;
	private String conformsTo;
	private String describedBy;
	
	private List<Dataset> dataSetList;
	
	public Catalog()
	{
		dataSetList = new ArrayList<Dataset>();
	}
	
	/**
	 * Populates this class' member variables from CKAN JSON Object
	 * <p>
	 * When doing a search on CKAN, the result is an array of packages(in this program datasets).
	 * This method begins the process of marshaling the JSON into Java Objects by looping through
	 * the packages and calling the loadDataset methods at the dataset level.
	 * @param catalogCKAN_JSON JSONObject The results from a CKAN query.
	 */
	public void loadCatalogFromCKAN_JSON(JSONObject catalogCKAN_JSON)
	{
		JSONObject resultObject= (JSONObject) catalogCKAN_JSON.get("result");
		try
		{
			JSONArray packageList = (JSONArray) resultObject.get("packages");				
			for(int i = 0; i < packageList.size(); i++)
			{
				JSONObject packageObject = (JSONObject) packageList.get(i);
				Dataset ds = new Dataset();
				ds.loadDatasetFromCKAN_JSON(packageObject);
				dataSetList.add(ds);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	//This is for mulitple organization catalogs
	public void loadMulitpleCatalogsFromCKAN(String catalogFileName)
	{
		//testing skeleton
	}
	
	public void produceQuarterlyReport (String quarterReportFileName)
	{
		//testing skeleton
	}
	
	public void produceBureauMetrics(String bureauMetricsFileName)
	{
		//testing skeleton
	}
	/**
	 * Adds datasets from another catalog to this catalog.
	 * 
	 * @param otherCatalog Catalog Another catalog who's datasets will be combined with this object.
	 */
	public void addFromOtherCatalog(Catalog otherCatalog)
	{
		List<Dataset> otherDatasetList = new ArrayList<>();
		otherDatasetList = otherCatalog.dataSetList;
		
		for (Dataset ds: otherDatasetList)
		{
			dataSetList.add(ds);
		}
	}
	
	/**
	 * Outputs a Catalog into tab delimited format.
	 * <p>
	 * Begins the process by listing out the header and calling all datasets to create
	 * tab delimitted lines.
	 * @param filePath String The output file for the catalog tab delimitted file.
	 */
	public void outputCSV(String filePath)
	{
		try
		{
			PrintWriter out = new PrintWriter(filePath);

			String headerLine = "Agency Name\tTitle\tDescription\tFormat\tAccess URL\tFrequency\tABureau Code\tContact Email\tContactName\t";
			headerLine = headerLine + "Landing Page\tProgram Code\tPublisher\tPublic Access Level\tAccess Level Comment\tTags\tLast Update\tRelease Date\tUnique Identifier\t";
			headerLine = headerLine + "Data Dictionary\tLicense\tSpatial\tTemporal\tSystem Of Records\tData Quality\tLangauge\t";
			headerLine = headerLine + "Program Code\tTheme\tReference\t";
			
			out.println(headerLine);		
			
			for (int i=0; i < dataSetList.size(); i++)
			{
				if (!dataSetList.get(i).getAccessLevel().equals("non-public"))
				{
					out.println(dataSetList.get(i).toCSV());
				}
			}
			out.close();
		}
		catch(Exception ex)
		{
			System.out.print(ex.toString());
		}
	}
	
	/**
	 * Populates catalog from Project Open Data compliant json object
	 * 
	 * @param catalogObject
	 */
	public void loadFromProjectOpenDataJSON(JSONObject catalogObject)
	{
		setConformsTo((String) catalogObject.get("conformsTo"));
		setDescribedBy((String) catalogObject.get("describedBy"));
		setContext ((String) catalogObject.get("@context"));
		setType ((String) catalogObject.get("@type"));
		
		JSONArray dataSetArray = new JSONArray();
		dataSetArray = (JSONArray) catalogObject.get("dataset");
		for (int i = 0; i < dataSetArray.size(); i++)
		{
			Dataset ds = new Dataset();
			JSONObject dataSetObject = (JSONObject) dataSetArray.get(i);
			ds.loadFromProjectOpenDataJSON(dataSetObject);
			dataSetList.add(ds);
		}
		
	}
	
	/**
	 * Populates catalog object from CKAN compliant results string.
	 * @param catalogJSONString String CKAN search results string
	 */
	public void loadCatalogFromJSONString(String catalogJSONString)
	{
		JSONObject resourceCKAN_JSON = new JSONObject();
		Object obj = new Object();
		try{
			JSONParser parser = new JSONParser();

			obj = parser.parse(catalogJSONString);
			resourceCKAN_JSON = (JSONObject)obj;
		} 
		catch (ParseException pe) 
		{
			System.out.print(pe.toString());
		}
			
		loadCatalogFromCKAN_JSON(resourceCKAN_JSON);
	}
	

	/**
	 * Populates catalog from CKAN compliant json file.
	 * @param catalogFileName
	 */
	public void loadCatalogFromCKAN(String catalogFileName)
	{
		String catalogCKAN_JSON_String = "";
		Object obj = new Object();
		JSONObject resourceCKAN_JSON = new JSONObject();
		try 
		{
			catalogCKAN_JSON_String = new String(Files.readAllBytes(Paths.get(catalogFileName)));
			JSONParser parser = new JSONParser();
			obj = parser.parse(catalogCKAN_JSON_String);
			resourceCKAN_JSON = (JSONObject)obj;
		} 
		catch (IOException | ParseException pe) 
		{
			System.out.print(pe.toString());
		}
				
		loadCatalogFromCKAN_JSON(resourceCKAN_JSON);
	}
	
	/**
	 * Outputs catalog object to Project Open Data v1.1 compliant json file for example data.json.
	 * <p>
	 * 
	 * @param podFilePath
	 * @param privateIndicator
	 */
	public void toProjectOpenDataJSON(String podFilePath, Boolean privateIndicator)
	{	
		Map catalogJSON = new LinkedHashMap();
		Map dataSetMap = new LinkedHashMap();
		JSONArray dataSetArray = new JSONArray();
		
		catalogJSON.put("conformsTo", "https://project-open-data.cio.gov/v1.1/schema");
		catalogJSON.put("describedBy", "https://project-open-data.cio.gov/v1.1/schema/catalog.json");
		catalogJSON.put("@context", "https://project-open-data.cio.gov/v1.1/schema/data.jsonld");
		catalogJSON.put("@type", "dcat:Catalog");
	
		for (int i = 0; i < dataSetList.size(); i++)
		{
			if (privateIndicator)
			{
				dataSetArray.add(dataSetList.get(i).toProjectOpenDataJSON());
			}
			else
			{
				String publicAccessLevel = dataSetList.get(i).getAccessLevel();
				if (publicAccessLevel.equals("public") || publicAccessLevel.equals("restricted"))
				{
					dataSetArray.add(dataSetList.get(i).toProjectOpenDataJSON());
				}
			}
		}
		
		catalogJSON.put("dataset", dataSetArray);
		
		Utils.printJSON(podFilePath, catalogJSON);
		 
	}
	
	public JSONObject toCKAN_JSON()
	{
		JSONObject catalogCKAN_JSON = new JSONObject();
		return catalogCKAN_JSON;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getSpatial() {
		return spatial;
	}
	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getDescribedBy() {
		return describedBy;
	}
	public void setDescribedBy(String describedBy) {
		this.describedBy = describedBy;
	}
	
	public Integer size()
	{
		return dataSetList.size();
	}
	
	/**
	 * Validates catalog is Project Open Data 1.1 compliant
	 * <p>
	 * This method checks business rules that identifiers are unique.  Other business rules
	 * will be added in the future.
	 * @return
	 */
	public Boolean validateCatalog()
	{
		Boolean validIndicator = true;
		validIndicator = validateUniqueIdentifiers();
		
		return validIndicator;
	}
	
	public Boolean validateUniqueIdentifiers()
	{
		Boolean validIndicator = true;
		//optimize this
		for (int i = 0; i < dataSetList.size(); i++)
		{
			String identifier = dataSetList.get(i).getUniqueIdentifier();
			for (int k=i+1; k < dataSetList.size(); k++)
			{
				String otherIdentifier = dataSetList.get(k).getUniqueIdentifier();
				if (identifier.equals(otherIdentifier))
				{
					System.out.println("Invalid catalog: non-unique identifier: " + dataSetList.get(k).getTitle());
					validIndicator=false;
				}
			}
		}
		return validIndicator;
	}
}
