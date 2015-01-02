package gov.usda.DataCatalogClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONObject;

/**
 * The Distribution class is a based on a combination of Project Open Data metadata specification
 * 1.1.  More details here: https://project-open-data.cio.gov/v1.1/schema/#distribution
 * 
 * @author bbrotsos
 *
 */
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
	
	private static final Logger log = Logger.getLogger(Distribution.class.getName());
	private DistributionException distributionException;
	
	public Distribution()
	{
		distributionException = new DistributionException();
	}
	
	/**
	 * This method takes in a CKAN Formatted JSONObject and marshals it. In CKAN, resource is equivalent
	 * to project open data distribution.
	 * 
	 * If type is not specified it is set to dcat:Distribution
	 * @param resourceCKAN_JSON
	 * @throws DistributionException
	 */
	public void loadDistributionFromCKAN_JSON(JSONObject resourceCKAN_JSON) throws DistributionException
	{
		if (resourceCKAN_JSON == null)
		{
			throw new NullPointerException("resourceCKAN_JSON cannot be null");
		}
		setTitle((String) resourceCKAN_JSON.get("name"));
		setDescription ((String) resourceCKAN_JSON.get("description"));
		setConformsTo ((String) resourceCKAN_JSON.get("conformsTo"));
		setDescribedByType((String) resourceCKAN_JSON.get("describedByType"));
		setDescribedBy ((String) resourceCKAN_JSON.get("describedBy"));
		if ((String) resourceCKAN_JSON.get("type") != null)
		{
			setType((String)resourceCKAN_JSON.get("describedBy"));
		}
		else
		{
			setType("dcat:Distribution");
		}
    	
    	//resourceType is the check button when adding resource "link to download, link to api, link to file, link to accessurl
    	//accessurl = AccessURL; file = DownloadURL; api = API
    	resourceType = ((String) resourceCKAN_JSON.get("resource_type"));
    	if (resourceType == null)
    	{
    		//default to download_url
    		setDownloadURL((String) resourceCKAN_JSON.get("url"));
    	}
    	else if (resourceType.equals("accessurl"))
    	{
    		setAccessURL((String) resourceCKAN_JSON.get("url"));
    	}
    	else if (resourceType.equals("file"))
    	{
    		setDownloadURL((String) resourceCKAN_JSON.get("url"));
    	}
    	else if (resourceType.toLowerCase().equals("api"))
    	{
    		setAccessURL((String) resourceCKAN_JSON.get("url"));
    	}
    	else
    	{
    		//new resource type in CKAN
    		log.log(Level.SEVERE, "New resource type in CKAN. " + (String) resourceCKAN_JSON.get("resource_type") + " for URL: "  + (String) resourceCKAN_JSON.get("url") );
    	}
    	
    	//looks weird: mediaType = format and format = formatReadable
    	//ok, this keeps backward compatiablity with POD 1.0
    	setMediaType((String) resourceCKAN_JSON.get("format"));
    	setFormat ((String) resourceCKAN_JSON.get("formatReadable"));	
    	if (distributionException.exceptionSize()>0)
    	{
    		throw (distributionException);
    	}
	}
	
	/**
	 * This loads JSON Object that is formated in Project Open Data specification and marshals it to this 
	 * class.
	 * @param pod_JSONObject
	 * @throws DistributionException
	 */
	//TODO: Set resourceType based on same algorithm it's set in CKAN (File, API, null)
	public void loadFromProjectOpenDataJSON(JSONObject pod_JSONObject) throws DistributionException
	{
		if (pod_JSONObject == null)
		{
			throw new NullPointerException("Distribution needs to be populated");
		}
		setTitle ((String) pod_JSONObject.get("title"));
		setDescription ((String) pod_JSONObject.get("description"));
		
		
		
		//catch here so we get all errors.
		try{
			//TODO: the return of JSONObject with URL and List<> is making code difficult to read
			//refactor
			if ( pod_JSONObject.get("accessURL") instanceof String)
			{
				setAccessURL((String) pod_JSONObject.get("accessURL"));
			}
			else
			{
				setAccessURL((URL) pod_JSONObject.get("accessURL"));
			}
			if (pod_JSONObject.get("downloadURL") instanceof String)
			{
				setDownloadURL ((String) pod_JSONObject.get("downloadURL"));
			}
			else
			{
				setDownloadURL((URL) pod_JSONObject.get("downloadURL"));
			}
		}
		catch(DistributionException	e){
			distributionException.addError(e.toString());
		}
		setMediaType ((String) pod_JSONObject.get("mediaType"));
		setFormat ((String) pod_JSONObject.get("format"));
		
		//new 1.1
		setDescribedBy ((String) pod_JSONObject.get("describedBy"));
		setDescribedByType ((String) pod_JSONObject.get("describedByType"));
		setConformsTo ((String) pod_JSONObject.get("conformsTo"));
		setType ((String) pod_JSONObject.get("@type"));
		
		if (!validateDistribution() || distributionException.exceptionSize()>0)
    	{
    		throw (distributionException);
    	}	 
	}
	
	/**
	 * Fail on these business rules:
	 * mediaType is required unless format is equal to api.
	 * 
	
	 * @return
	 */
	private boolean validateDistribution()
	{
		boolean validIndicator = true;
		if (mediaType == null && !format.toLowerCase().equals("api"))
		{
			distributionException.addError("Media Type is required.");
			validIndicator = false;
		}	
		
		return validIndicator;
	}
	
	/**
	 * This validates business rules for public datasets
	 * 
	 * if Format == API and AccessURL == null
	 * if downloadURL = "something" and accessURL == something
	 * if downloadURL = null and access URL == null
	 * 
	 * @return
	 */
	public boolean validatePublicDistribution() throws DistributionException
	{
		boolean validIndicator = true;
		if (accessURL == null & downloadURL == null)
		{
			distributionException.addError("Access URL or Download URL cannot both be blank");
			validIndicator = false;
		}
		else if (format.toLowerCase().equals("api") && accessURL == null)
		{
			distributionException.addError("If format field equals api, access URL cannot be blank.");
			validIndicator = false;
		}
		else if (accessURL != null && downloadURL != null)
		{
			distributionException.addError("Access URL and Download URL cannot both have values.");
			validIndicator = false;
		}
		
		if (!validIndicator)
		{
			throw (distributionException);
		}
		
		return validIndicator;
	}
	
	/*
	 * This outputs the object in CKAN Formatted JSON Object.  This is called a Resource in CKAN.
	 */
	@SuppressWarnings("unchecked")
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
	
	/**
	 * Returns a Map object of the distribution in Project Open Data specification.
	 * @return
	 */
	//TODO: Change Map to JSONObject.  No need to preserve order anymore
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject distributionJSON = new JSONObject();
		distributionJSON.put("@type", type);
		distributionJSON.put("downloadURL", downloadURL);
		distributionJSON.put("mediaType", mediaType);
		distributionJSON.put("title", title);
		distributionJSON.put("description", description);
		distributionJSON.put("accessURL", accessURL);
		distributionJSON.put("format", format);
		distributionJSON.put("describedBy", describedBy);
		distributionJSON.put("describedByType", describedByType);
		distributionJSON.put("conformsTo", conformsTo);
	
		return distributionJSON ;
	}
	
	public String getTitle() {
		return title;
	}
	private void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	private void setDescription(String description) {
		this.description = description;
	}
	public URL getAccessURL() {
		return accessURL;
	}
	
	private void setAccessURL(String accessURL_String)  throws DistributionException
	{
		if (accessURL_String != null)
		{
			try
			{
				this.accessURL =  new URL(accessURL_String);
			}
			catch(MalformedURLException e)
			{
				throw new DistributionException("Invalid accessUrl" + e.toString());

			}
		}
	}
	public void setAccessURL(URL accessURL) {
		this.accessURL = accessURL;
	}

	public URL getDownloadURL() {
		return downloadURL;
	}
	
	public void setDownloadURL(URL downloadURL) {
		this.downloadURL = downloadURL;
	}

	private void setDownloadURL(String downloadURL_String) throws DistributionException
	{
		if (downloadURL_String != null)
		{
			try
			{
				this.downloadURL = new URL(downloadURL_String);
			}
			catch (MalformedURLException e)
			{
				throw new DistributionException("Invalid downloadUrl:" + e.toString());
			}
		}
	}
	
	public String getMediaType() {
		return mediaType;
	}
	
	//TODO: load IANA mimetypes and validate
	private void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getFormat() {
		return format;
	}
	private void setFormat(String format) {
		this.format = format;
	}
	public Integer getByteSize() {
		return byteSize;
	}
	
	public Date getIssued() {
		return issued;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public String getLicense() {
		return license;
	}
	
	public String getRights() {
		return rights;
	}
	
	public String getDescribedBy() {
		return describedBy;
	}
	private void setDescribedBy(String describedBy) {
		this.describedBy = describedBy;
	}
	public String getDescribedByType() {
		return describedByType;
	}
	private void setDescribedByType(String describedByType) {
		this.describedByType = describedByType;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	private void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getType() {
		return type;
	}
	private void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Does not include CKAN specific field resource type.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Distribution))
		{
			return false;
		}
		Distribution distribution_other = (Distribution)o;
		
		return new EqualsBuilder()
         .append(accessURL, distribution_other.accessURL)
         .append(byteSize, distribution_other.byteSize)
         .append(conformsTo, distribution_other.conformsTo)
         .append(describedBy, distribution_other.describedBy)
         .append(describedByType, distribution_other.describedByType)
         .append(description, distribution_other.description)
         .append(downloadURL, distribution_other.downloadURL)
         .append(format, distribution_other.format)
         .append(issued, distribution_other.issued)
         .append(license, distribution_other.license)
         .append(mediaType, distribution_other.mediaType)
         .append(modified, distribution_other.modified)
         .append(rights, distribution_other.rights)
         .append(title, distribution_other.title)
         .append(type, distribution_other.type)
         .isEquals();
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(13, 57).
				append(accessURL).
				append(byteSize).
				append(conformsTo).
				append(describedBy). 
				append(describedByType). 
				append(description). 
				append(downloadURL). 
				append(format). 
				append(issued). 
				append(license). 
				append(mediaType). 
				append(modified). 
				append(rights). 
				append(title). 
				append(type).
				toHashCode();
	}
	
	@Override
	public String toString() {
		return "Distribution [title=" + title + ", description=" + description
				+ ", accessURL=" + accessURL + ", downloadURL=" + downloadURL
				+ ", mediaType=" + mediaType + ", format=" + format
				+ ", byteSize=" + byteSize + ", issued=" + issued
				+ ", modified=" + modified + ", license=" + license
				+ ", rights=" + rights + ", describedBy=" + describedBy
				+ ", describedByType=" + describedByType + ", conformsTo="
				+ conformsTo + ", type=" + type + ", resourceType="
				+ resourceType + "]";
	}
	
}
