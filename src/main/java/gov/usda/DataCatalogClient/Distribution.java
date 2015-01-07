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

	//Project Open Data JSON Fields
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION= "distribution";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_ACCESS_URL = "accessURL";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_CONFORMS_TO =  "conformsTo";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIPTION = "description";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_DOWNLOAD_URL = "downloadURL";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY = "describedBy";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY_TYPE = "describedByType";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_FORMAT = "format";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_MEDIA_TYPE = "mediaType";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_TITLE = "title";
	public final static String PROJECT_OPEN_DATA_DISTRIBUTION_TYPE = "@type";
	
	//CKAN Data Fields, media type looks odd to continue backwards compliance with Project Open Data 1.0
	public final static String CKAN_DISTRIBUTION_DESCRIPTION = "description";
	public final static String CKAN_DISTRIBUTION_CONFORMS_TO = "conformsTo";
	public final static String CKAN_DISTRIBUTION_DESCRIBED_BY = "describedBy";
	public final static String CKAN_DISTRIBUTION_DESCRIBED_BY_TYPE = "describedByType";
	public final static String CKAN_DISTRIBUTION_FORMAT = "formatReadable";
	public final static String CKAN_DISTRIBUTION_MEDIA_TYPE = "format";
	public final static String CKAN_DISTRIBUTION_TITLE = "name";
	public final static String CKAN_DISTRIBUTION_URL = "url";


	//Common DCAT & POD metadata fields
	private URL accessURL;
	private String description;
	private URL downloadURL;
	private String format;
	private String mediaType;
	private String title;
	
	//Additional DCAT fields http://www.w3.org/TR/vocab-dcat/
	private Integer byteSize;
	private Date issued;
	private Date modified;
	private String license;
	private String rights;
	
	//Additional POD Fields: https://project-open-data.cio.gov/v1.1/schema/#accessLevel
	private String conformsTo;
	private String describedBy;
	private String describedByType;
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
		setTitle((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_TITLE));
		setDescription ((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_DESCRIPTION));
		setConformsTo ((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_CONFORMS_TO));
		setDescribedByType((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_DESCRIBED_BY_TYPE));
		setDescribedBy ((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_DESCRIBED_BY));
    	setFormat ((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_FORMAT));	
		if ((String) resourceCKAN_JSON.get("type") != null)
		{
			setType((String)resourceCKAN_JSON.get("type"));
		}
		else
		{
			setType("dcat:Distribution");
		}
    	
    	//resourceType is the check button when adding resource "link to download, link to api, link to file, link to accessurl
    	//accessurl = AccessURL; file = DownloadURL; api = API
		//TODO:optimize, make clearer
    	resourceType = ((String) resourceCKAN_JSON.get("resource_type"));
    	if (resourceType == null)
    	{
    		if (format != null && format.equalsIgnoreCase("api"))
    		{
    			setAccessURL((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL));
    		}
    		else	
    		{
    			setDownloadURL((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL));
    		}
    	}
    	else if (resourceType.equals("accessurl"))
    	{
    		setAccessURL((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL));
    	}
    	else if (resourceType.equals("file"))
    	{
    		setDownloadURL((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL));
    	}
    	else if (resourceType.toLowerCase().equals("api") )
    	{
    		setAccessURL((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL));
    	}
    	else
    	{
    		//new resource type in CKAN
    		log.log(Level.SEVERE, "New resource type in CKAN. " + (String) resourceCKAN_JSON.get("resource_type") + " for URL: "  + (String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_URL) );
    	}
    	
    	//looks weird: mediaType = format and format = formatReadable
    	//ok, this keeps backward compatiablity with POD 1.0
    	setMediaType((String) resourceCKAN_JSON.get(CKAN_DISTRIBUTION_MEDIA_TYPE));
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
		setTitle ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_TITLE));
		setDescription ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIPTION ));
	
		//catch here so we get all errors.
		try{
			//TODO: the return of JSONObject with URL and List<> is making code difficult to read
			//refactor
			if ( pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_ACCESS_URL) instanceof String)
			{
				setAccessURL((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_ACCESS_URL));
			}
			else
			{
				setAccessURL((URL) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_ACCESS_URL));
			}
			if (pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DOWNLOAD_URL) instanceof String)
			{
				setDownloadURL ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DOWNLOAD_URL));
			}
			else
			{
				setDownloadURL((URL) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DOWNLOAD_URL));
			}
		}
		catch(DistributionException	e){
			distributionException.addError(e.toString());
		}
		setMediaType ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_MEDIA_TYPE));
		setFormat ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_FORMAT));
		
		//new 1.1
		setDescribedBy ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY));
		setDescribedByType ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY_TYPE));
		setConformsTo ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_CONFORMS_TO));
		setType ((String) pod_JSONObject.get(PROJECT_OPEN_DATA_DISTRIBUTION_TYPE));
		
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
		
		else if (format != null && format.toLowerCase().equals("api") && accessURL == null)
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
	 * This currently suppresses title and description until validation checks.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toCKAN_JSON()
	{
		JSONObject distributionCKAN_JSON = new JSONObject();
		
		//distributionCKAN_JSON.put(CKAN_DISTRIBUTION_TITLE, title);
		if (description != null && !description.isEmpty())
		{
		//	distributionCKAN_JSON.put(CKAN_DISTRIBUTION_DESCRIPTION, description);
		}
		distributionCKAN_JSON.put(CKAN_DISTRIBUTION_FORMAT, format);
		distributionCKAN_JSON.put(CKAN_DISTRIBUTION_MEDIA_TYPE, mediaType);
		distributionCKAN_JSON.put("resource_type", resourceType);
		if (resourceType == null)
    	{
    		//default to download_url
			if (downloadURL != null)
			{
				distributionCKAN_JSON.put(CKAN_DISTRIBUTION_URL, downloadURL.toString());
			}
			if (accessURL != null)
			{
				distributionCKAN_JSON.put(CKAN_DISTRIBUTION_URL, accessURL.toString());
			}
    	}
    	else if (resourceType.equals("accessurl"))
    	{
			distributionCKAN_JSON.put(CKAN_DISTRIBUTION_URL, accessURL.toString());
    	}
    	else if (resourceType.equals("file"))
    	{
			distributionCKAN_JSON.put(CKAN_DISTRIBUTION_URL, downloadURL.toString());
    	}
    	
		distributionCKAN_JSON.put(CKAN_DISTRIBUTION_DESCRIBED_BY, describedBy);
		distributionCKAN_JSON.put(CKAN_DISTRIBUTION_DESCRIBED_BY, describedByType);
		distributionCKAN_JSON.put(CKAN_DISTRIBUTION_CONFORMS_TO, conformsTo);
		distributionCKAN_JSON.put("@type", type);
		
		return distributionCKAN_JSON;
	}
	
	/**
	 * Returns a JSON object of the distribution in Project Open Data specification.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject distributionJSON = new JSONObject();
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_TYPE, type);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_DOWNLOAD_URL, downloadURL);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_MEDIA_TYPE, mediaType);
		if (!(title == null) && !title.isEmpty())
		{
			distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_TITLE, title);
		}
		if (!(description == null) && !description.isEmpty())
		{
			distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIPTION , description);
		}
		
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_ACCESS_URL, accessURL);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_FORMAT, format);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY, describedBy);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_DESCRIBED_BY_TYPE, describedByType);
		distributionJSON.put(PROJECT_OPEN_DATA_DISTRIBUTION_CONFORMS_TO, conformsTo);
	
		return distributionJSON ;
	}
	
	public String getTitle() {
		return title;
	}
	private void setTitle(String title) {
		if (title != null)
		{
			this.title = title.trim();
		}
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
