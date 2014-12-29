package gov.usda.DataCatalogClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Distribution {

	//Common DCAT & POD metadata fields
	private String title;
	private String description;
	private URL accessURL;
	private URL downloadURL;
	private String mediaType;
	private String format;
	
	//Additional DCAT fields http://www.w3.org/TR/vocab-dcat/
	private Integer byteSize;
	private Date issued;
	private Date modified;
	private String license;
	private String rights;
	
	//Additional POD Fields: https://project-open-data.cio.gov/v1.1/schema/#accessLevel
	private String describedBy;
	private String describedByType;
	private String conformsTo;
	private String type;
	
	//This is for checkbox "link to api, etc in CKAN
	private String resourceType;
	
	private DistributionException distribtionEx;
	
	public Distribution()
	{
		distribtionEx = new DistributionException("Distribution Exception:");
	}
	
	public void loadDistributionFromCKAN_JSON(JSONObject resourceCKAN_JSON) throws MalformedURLException
	{
		setTitle((String) resourceCKAN_JSON.get("name"));
		setDescription ((String) resourceCKAN_JSON.get("description"));
		setConformsTo ((String) resourceCKAN_JSON.get("conformsTo"));
		setDescribedByType((String) resourceCKAN_JSON.get("describedByType"));
		setDescribedBy ((String) resourceCKAN_JSON.get("describedBy"));
    	
    	//resourceType is the check button when adding resource "link to download, link to api, link to file, link to accessurl
    	//accessurl = AccessURL; file = DownloadURL; api = API
    	resourceType = ((String) resourceCKAN_JSON.get("resource_type"));
    	if (resourceType == null)
    	{
    		//default to download_url
    		try{
    			setDownloadURL((String) resourceCKAN_JSON.get("url"));
    		}
    		catch (MalformedURLException e)
    		{
    			throw (e);
    		}
    	}
    	else if (resourceType.equals("accessurl"))
    	{
    		setAccessURL((String) resourceCKAN_JSON.get("url"));
    	}
    	else if (resourceType.equals("file"))
    	{
    		setDownloadURL((String) resourceCKAN_JSON.get("url"));
    	}
    	
    	//looks weird: mediaType = format and format = formatReadable
    	//ok, keeps backward compatiablity with POD 1.0
    	setMediaType((String) resourceCKAN_JSON.get("format"));
    	setFormat ((String) resourceCKAN_JSON.get("formatReadable"));	
	}
	public void loadFromProjectOpenDataJSON(JSONObject pod_JSONObject) throws MalformedURLException, DistributionException
	{
		if (pod_JSONObject == null)
		{
			throw new DistributionException("Distribution needs to be populated");
		}
		setTitle ((String) pod_JSONObject.get("title"));
		setDescription ((String) pod_JSONObject.get("description"));
		try{
			setAccessURL((String) pod_JSONObject.get("accessURL"));
			setDownloadURL ((String) pod_JSONObject.get("downloadURL"));
		}
		catch (MalformedURLException e){
			throw (e);
		}
		setMediaType ((String) pod_JSONObject.get("mediaType"));
		setFormat ((String) pod_JSONObject.get("format"));
		
		//new 1.1
		setDescribedBy ((String) pod_JSONObject.get("describedBy"));
		setDescribedByType ((String) pod_JSONObject.get("describedByType"));
		setConformsTo ((String) pod_JSONObject.get("conformsTo"));
		setType ((String) pod_JSONObject.get("@type"));
	}
	
	public JSONObject toCKAN_JSON()
	{
		JSONObject distributionCKAN_JSON = new JSONObject();
		
		distributionCKAN_JSON.put("name", title);
		distributionCKAN_JSON.put("description", description);
		distributionCKAN_JSON.put("formatReadable", format);
		distributionCKAN_JSON.put("format", mediaType);
		distributionCKAN_JSON.put("resource_type", resourceType);
		if (resourceType == null)
    	{
    		//default to download_url
			if (downloadURL != null)
			{
				distributionCKAN_JSON.put("url", downloadURL.toString());
			}
			if (accessURL != null)
			{
				distributionCKAN_JSON.put("url", accessURL.toString());
			}
    	}
    	else if (resourceType.equals("accessurl"))
    	{
			distributionCKAN_JSON.put("url", accessURL.toString());
    	}
    	else if (resourceType.equals("file"))
    	{
			distributionCKAN_JSON.put("url", downloadURL.toString());
    	}
    	
		distributionCKAN_JSON.put("describedBy", describedBy);
		distributionCKAN_JSON.put("describedByType", describedByType);
		distributionCKAN_JSON.put("conformsTo", conformsTo);
		distributionCKAN_JSON.put("@type", type);
		
		return distributionCKAN_JSON;
	}
	
	public Map toProjectOpenDataJSON()
	{
		Map distributionJSON = new LinkedHashMap();
		//JSONObject dataSetJSON = new JSONObject();
		distributionJSON.put("@type", type);
		distributionJSON.put("downloadURL", accessURL);
		distributionJSON.put("mediaType", mediaType);
		distributionJSON.put("title", title);
		distributionJSON.put("description", description);
		distributionJSON.put("access_url", accessURL);
		distributionJSON.put("distributionURL", downloadURL);
		distributionJSON.put("format", format);
		distributionJSON.put("describedBy", describedBy);
		distributionJSON.put("describedByType", describedByType);
		distributionJSON.put("conformsTo", conformsTo);

		
		return distributionJSON ;
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
	public URL getAccessURL() {
		return accessURL;
	}
	public void setAccessURL(URL accessURL) {
		this.accessURL = accessURL;
	}
	private void setAccessURL(String accessURL_String)  throws MalformedURLException
	{
		if (accessURL_String != null)
		{
			try
			{
				this.accessURL =  new URL(accessURL_String);
			}
			catch(MalformedURLException e)
			{
				throw (e);
			}
		}
	}
	public URL getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(URL downloadURL) {
		this.downloadURL = downloadURL;
	}
	private void setDownloadURL(String downloadURL_String) throws MalformedURLException
	{
		if (downloadURL_String != null)
		{
			try
			{
				this.downloadURL = new URL(downloadURL_String);
			}
			catch (MalformedURLException e)
			{
				throw (e);
			}
		}
	}
	
	public String getMediaType() {
		return mediaType;
	}
	
	//TODO: load IANA mimetypes and validate
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Integer getByteSize() {
		return byteSize;
	}
	public void setByteSize(Integer byteSize) {
		this.byteSize = byteSize;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
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
	public String getDescribedBy() {
		return describedBy;
	}
	public void setDescribedBy(String describedBy) {
		this.describedBy = describedBy;
	}
	public String getDescribedByType() {
		return describedByType;
	}
	public void setDescribedByType(String describedByType) {
		this.describedByType = describedByType;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
