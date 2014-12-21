package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	public void loadCatalogFromCKAN_JSON(JSONObject catalogCKAN_JSON)
	{
		JSONObject resultObject= (JSONObject) catalogCKAN_JSON.get("result");
		try
		{
			JSONArray packageList = (JSONArray) resultObject.get("packages");				
			for(int n = 0; n < packageList.size(); n++)
			{
				JSONObject packageObject = (JSONObject) packageList.get(n);
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
	
	
	
}
