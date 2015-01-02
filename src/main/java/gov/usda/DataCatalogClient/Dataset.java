package gov.usda.DataCatalogClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The Dataset class is based on Project Open Data metadata specification 1.1. More
 * details can be found here: https://project-open-data.cio.gov/v1.1/schema/#dataset
 * 
 * There are two ways to load the dataset object:
 * 
 * 1.  Load Dataset from a CKAN JSON Object.    @see loadDatasetFromCKAN_JSON(JSONObject):void
 * 2.  Load Dataset from a Project Open Data compliant JSON Object.  @see loadDatasetFromProjectOpenDataJSON(JSONObject):void
 * 
 * Both of these methods will throw a Dataset Exception if the JSON is not in Project
 * Open Data compliance.
 * 
 * Also, the variables are exposed so another way to populate Dataset is to set variables like 
 * Dataset ds = new Dataset();
 * ds.setTitle("My new Title");
 * ...
 * ds.validate();  //validates to ensure Project Open Data Compliance.
 * 
 * No builders were implemented because the most normal use-case is loading from a JSON file.
 * 
 * The Dataset class also has methods for outputting the dataset in multiple formats.
 * 
 * 1.  CKAN formated JSON Object: @see toCKAN_JSON():JSONObject
 * 2.  Project Open Data compliant Map.  Linked HashMap is used to preserve order against JSON specification.  This is used
 * for testing.  @see toProjectOpenDataJSON(): Map
 * 3.  Text delimited format for opening in Excel, etc.  This is tab delimited format  @see toCSV()String
 * 
 * @author bbrotsos
 *
 */
public class Dataset {
	
	//metadata documentation is at http://www.w3.org/TR/vocab-dcat/
	private String title;
	private String description;
	private Date issued;
	private Date modified;
	//These three are lists for Project Open Data compliance
	private List<String> keywordList;
	private List<String> languageList;
	private List<String> themeList;
	private Contact contactPoint;
	private Publisher publisher;
	private String temporal;
	private String spatial;
	private String accrualPeriodicity;
	private URL landingPage;
	private List<Distribution> distributionList;
	
	//federal government project open data extension documentation here: https://project-open-data.cio.gov/v1.1/schema/
	private String uniqueIdentifier;
	private List<String> bureauCodeList;
	private List<String> programCodeList;
	private String primaryITInvestmentUII;
	private String accessLevel;
	private String rights;
	private String systemOfRecords;
	private Boolean dataQuality;
	private List<String> referenceList;
	private String describedBy;
	private String describedByType;
	private String license;
	private String conformsTo;
	private String isPartOf;

	//Agency specific (legacy)
	private String comments;
	private String webService;
	private String ownerOrganization;
	
	private DatasetException dsEx;
	private static final Logger log = Logger.getLogger(Dataset.class.getName());

	
	public Dataset()
	{
		dsEx = new DatasetException();
		bureauCodeList = new ArrayList<String>();
		programCodeList = new ArrayList<String>();
		keywordList = new ArrayList<String>();
		languageList = new ArrayList<String>();
		themeList = new ArrayList<String>();
		referenceList = new ArrayList<String>();
		
		distributionList = new ArrayList<Distribution>();
		
		publisher = new Publisher();
		contactPoint = new Contact();
	}
	
	/**
	 * Populates this class from a JSON Object at the package level delivered from CKAN. 
	 * <p>
	 * It takes in a CKAN JSON formated dataset and populates the instance variables.  
	 * Most of the additional Project Open Data fields are in the Extras JSONArray.
	 * 
	 * @param datasetCKAN_JSON JSONObject This is most likely directly from CKAN API call.  This
	 * is also considered the Package level for CKAN.
	 */
	public void loadDatasetFromCKAN_JSON(JSONObject datasetCKAN_JSON) throws DatasetException
	{	
		if (datasetCKAN_JSON == null)
		{
			throw new NullPointerException("datasetCKAN_JSON cannot be null");
		}
		
		//This is used to store subOrganization.
		//There is no order in JSON so there is no guarantee that publisher1 comes after publisher.
		Publisher subOrganization = new Publisher();
		
		//probably should use GSON/Jackson
		//optimize in the future
		
		//issue, title is in two places. To solve this set it initially, and let extra tag overwrite if it exists in extra.
		setTitle((String) datasetCKAN_JSON.get("title"));
		setDescription((String) datasetCKAN_JSON.get("notes"));
	    setModified ((String) datasetCKAN_JSON.get("metadata_modified"));
	    
	    final JSONArray resourcesArray = (JSONArray) datasetCKAN_JSON.get("resources");
	    if (resourcesArray == null)
		{
	    	log.log(Level.SEVERE, "There are no resources. This could be the case for datasets marked private.  Passively allowing this but need to validate in validate function");
		}
	    else
	    {
	    	for (int i=0; i < resourcesArray.size(); i++)
	    	{	    	
	    		final JSONObject resource = (JSONObject) resourcesArray.get(i);

	    		Distribution distribution = new Distribution();
	    		try{
	    			distribution.loadDistributionFromCKAN_JSON(resource);
	    			distributionList.add(distribution);
	    		}
	    		catch (DistributionException e)
	    		{
	    			dsEx.addError("Distribution error" + e.toString());
	    		}
	    	}
	    }
		

	    final JSONArray extraList;
		extraList = (JSONArray) datasetCKAN_JSON.get("extras");
	    if (extraList == null)
	    {
	    	throw new IllegalArgumentException("JSON is invalid.  extras array is required.");
	    }
	    for (int i = 0; i < extraList.size(); i++)
		{			
			JSONObject extraObject = (JSONObject) extraList.get(i);
			String key = (String) extraObject.get("key");

			String value = (String) extraObject.get("value");
			if (key.equals("data_quality") || key.equals("dataQuality"))
	    	{
	    		setDataQuality(value);	    		
	    	}
	    	else if (key.equals("accrual_periodicity"))
	    	{
	    		setAccrualPeriodicity(value);
	    	}
	    	else if (key.equals("bureau_code"))
	    	{
	    		try{
	    			setBureauCodeList(value);
	    		}
	    		catch (DatasetException e)
	    		{
	    			dsEx.addError(e.toString());
	    		}
	    	}
	    	else if (key.equals("unique_id"))
	    	{
	    		setUniqueIdentifier(value);
	    	}
	    	else if (key.equals("contact_email"))
	    	{
	    		contactPoint.setEmailAddress(value);
	    	}
	    	else if (key.equals("contact_name"))
	    	{
	    		contactPoint.setFullName(value);
	    	}
	    	else if (key.equals("homepage_url"))
	    	{
	    		setLandingPage(value);
	    	}
	    	//TODO: Fix ckan feed for misspelled program_ocde
	    	else if (key.equals("program_code") || key.equals("program_ocde"))
	    	{
	    		programCodeList.add(value);
	    	}
	    	else if (key.equals("publisher"))
	    	{
	    		publisher.setName(value);
	    		//CKAN does not have way to extract his field, so hardcoded
	    		publisher.setType("org:Organization");
	    	}
	    	else if (key.equals("publisher_1"))
	    	{
	    		subOrganization.setName(value);
	    		subOrganization.setType("org:Organization");
	    	}
	    	else if (key.equals("related_documents"))
	    	{
	    		setReferenceList(value);
	    	}
	    	else if (key.equals("release_date"))
	    	{
	    		setIssued(value);
	    	}
	    	else if (key.equals("spatial"))
	    	{
	    		setSpatial(value);
	    	}
	    	else if (key.equals("temporal"))
	    	{
	    		setTemporal(value);
	    	}
	    	else if (key.equals("public_access_level"))
	    	{
	    		setAccessLevel(value);
	    	}
	    	else if (key.equals("access_level_comment"))
	    	{
	    		setRights(value);
	    	}
	    	else if (key.equals("title"))
	    	{
	    		setTitle(value.trim());	    		
	    	}
	    	else if (key.equals("revision_timestamp"))
	    	{
	    		setModified(value);
	    	}
	    	else if (key.equals("notes"))
	    	{
	    		setComments(value);
	    	}
	    	else if (key.equals("category"))
	    	{
	    		setThemeList(value);	
	    	}
	    	else if (key.equals("modified"))
	    	{
	    		setModified(value);
	    	}
	    	else if (key.equals("system_of_records"))
	    	{
	    		setSystemOfRecords(value);
	    	}
	    	else if (key.equals("data_dictionary") || key.equals("data_dict"))
	    	{
	    		setDescribedBy(value);
	    	}
	    	else if (key.equals("data_dictionary_type"))
	    	{
	    		setDescribedByType(value);
	    	}
	    	else if (key.equals("language"))
	    	{
	    		setLanguageList(value);
	    	}
	    	else if (key.equals("primary_it_investment_uii"))
	    	{
	    		setPrimaryITInvestmentUII(value);
	    	}
	    	else if (key.equals("webservice"))
	    	{
	    		setWebService(value);
	    	}
	    	else if (key.equals("owner_org") || key.equals("ow"))
	    	{
	    		setOwnerOrganization(value);
	    	}
	    	else if (key.equals("license_new"))
	    	{
	    		setLicense(value);
	    	}
	    	else if (key.equals("conforms_to"))
	    	{
	    		setConformsTo(value);
	    	}
	    	else if (key.equals("is_parent"))
	    	{
	    		setIsPartOf(value);
	    	}
	    	else
	    	{
				log.log(Level.SEVERE, "Unaccounted for CKAN Element:" + key + " value: " + value);
	    	}
			
		}
		//TODO: Put this at a better location, or figure out if CKAN is going to output the contact type field.
		contactPoint.setType("vcard:Contact");
		
		//put this at a better point too
		if (subOrganization != null)
		{
			publisher.setSubOrganization(subOrganization);
		}

		
		final JSONArray tagsArray = (JSONArray)datasetCKAN_JSON.get("tags");
		if (tagsArray == null)
		{
			throw new IllegalArgumentException("JSON is invalid for Project Open Data.  Expecting 'tags' array.");
		}
		
		for(int k=0; k<tagsArray.size(); k++)
		{
			final JSONObject tagObject = (JSONObject)tagsArray.get(k);
			keywordList.add((String)tagObject.get("display_name"));
		}
		
		if (!validateDataset() || dsEx.exceptionSize() > 0)
		{
			throw (dsEx);
		}
	}
	
	/**
	 * Converts Project Open Data object to CKAN compatible JSON dataset.
	 * <p>
	 * Marshals the object to a format that can be sent to CKAN for creating or updating datasets.
	 * 
	 * @return JSONObject This is CKAN compatible JSON
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toCKAN_JSON()
	{
		JSONObject datasetCKAN_JSON = new JSONObject();
		
		datasetCKAN_JSON.put("private", true);
		
		datasetCKAN_JSON.put("name", getName());
		datasetCKAN_JSON.put("notes", description);
		datasetCKAN_JSON.put("title", this.title);
		//dataio
		//datasetCKAN_JSON.put("owner_org", "f2e28a6c-fafb-4914-a590-91fbcd6ae339");
		
		//demo.ckan
		datasetCKAN_JSON.put("owner_org", "9ca02aa2-5007-4e9c-a407-ff8bdd9f43aa");
		
		JSONArray extrasArray = new JSONArray();
		extrasArray.add(createExtraObject("public_access_level", accessLevel));
		extrasArray.add(createExtraObject("access_level_comment", rights));
		if (contactPoint != null)
		{
			extrasArray.add(createExtraObject("contact_email", contactPoint.getEmailAddress()));
			extrasArray.add(createExtraObject("contact_name", contactPoint.getFullName()));
		}
		extrasArray.add(createExtraObject("accrual_periodicity", accrualPeriodicity));
		extrasArray.add(createExtraObject("conforms_to", conformsTo));
		extrasArray.add(createExtraObject("data_dictionary", describedBy));
		extrasArray.add(createExtraObject("data_dictionary_type", describedByType));
		if (dataQuality != null)
		{
			extrasArray.add(createExtraObject("data_quality", dataQuality.toString()));
		}
		if (landingPage != null)
		{
			extrasArray.add(createExtraObject("homepage_url", landingPage.toString()));
		}
		extrasArray.add(createExtraObject("is_parent", isPartOf));
		extrasArray.add(createExtraObject("license_new", license));
		if (modified != null)
		{
			extrasArray.add(createExtraObject("modified", Utils.convertDateToISOString(modified)));
		}
		extrasArray.add(createExtraObject("primary_it_investment_uii", primaryITInvestmentUII));
		if (publisher != null)
		{
			extrasArray.add(createExtraObject("publisher", publisher.getName()));
		}
		/*
		if (issued != null)
		{
			extrasArray.add(createExtraObject("release_date", Utils.convertDateToISOString(issued)));
		}
		
		extrasArray.add(createExtraObject("spatial", spatial));
		*/
		extrasArray.add(createExtraObject("system_of_records", systemOfRecords));
		extrasArray.add(createExtraObject("temporal", temporal));
		extrasArray.add(createExtraObject("unique_id", uniqueIdentifier));
		extrasArray.add(createExtraObject("program_code", Utils.listToCSV(programCodeList)));
		extrasArray.add(createExtraObject("language", Utils.listToCSV(languageList)));
		extrasArray.add(createExtraObject("bureau_code", Utils.listToCSV(bureauCodeList)));
		extrasArray.add(createExtraObject("category", Utils.listToCSV(themeList)));
		extrasArray.add(createExtraObject("related_documents",Utils.listToCSV(referenceList)));
		
		JSONArray tagsArray = new JSONArray();
		for (int i = 0; i < keywordList.size(); i++)
		{
			JSONObject tagObject = new JSONObject();
			tagObject.put("name", keywordList.get(i));
			tagObject.put("display_name", keywordList.get(i));
			tagsArray.add(tagObject);
		}
		datasetCKAN_JSON.put("tags", tagsArray);
		
		//get rid of nulls, ckan will give errors
		for (int i=0; i< extrasArray.size(); i++)
		{
			JSONObject extraObject = (JSONObject) extrasArray.get(i);
			if (extraObject.get("value") == null)
			{
				extrasArray.remove(i);
			}
		}
		datasetCKAN_JSON.put("extras", extrasArray);
		
		//add distribution
		JSONArray distributionArray = new JSONArray();
		for (int i=0; i < distributionList.size(); i++)
		{
			JSONObject distributionObject = new JSONObject();
			distributionObject = distributionList.get(i).toCKAN_JSON();
			distributionArray.add(distributionObject);
		}
		datasetCKAN_JSON.put("resources", distributionArray);
		
		return datasetCKAN_JSON;
		
	}
	
	/**
	 * Method to create CKAN compatible extra object.
	 * <p>
	 * Project Open Data uses the extra object for extensions.  The extra object is just
	 * a key-value for extending CKAN interface.
	 * 
	 * @param key  the key in key-value pair
	 * @param value the value in key-value pair.
	 * @return key-value JSON Object
	 */
	@SuppressWarnings("unchecked")
	private JSONObject createExtraObject(String key, String value)
	{
		JSONObject extraObject = new JSONObject();
		extraObject.put("key", key);
		extraObject.put("value", value);
		return extraObject;
	}
	
	/**
	 * Outputs data set line in tab delimited format
	 * <p>
	 * This method is to convert the object to one line of tab delimited format.
	 * @return String This string is tab delimited format of the Dataset object.
	 */
	//TODO: Optimize by using new String() rather than +
	public String toCSV()
	{
		String response = "";
    	response = response + title + "\t";
    	//TODO: make description CSVable
    	//response = response  + unEscapeString(description) + "\t";
    	
    	if (distributionList.size() > 0)
		{
    		for (int i=0; i< distributionList.size(); i++)
    		{
    			if (i > 0)
    			{
    				response = response + ", ";
    			}
				Distribution outputDistribution = distributionList.get(i);
				response = response + outputDistribution.getFormat();
    		}
    		response = response + "\t";
		}
    	else
    	{
    		response = response + "\t";
    	}
    	if (distributionList.size() > 0)
		{
    		for (int i=0; i< distributionList.size(); i++)
    		{
    			if (i > 0)
    			{
    				response = response + ", ";
    			}
    			Distribution outputDistribution = distributionList.get(i);
				response = response + outputDistribution.getAccessURL();
    		}
    		response = response + "\t";
		}
    	else
    	{
    		response = response + "\t";
    	}
    	
    	response = response +  accrualPeriodicity + "\t";
    	response = response + bureauCodeList.get(0) + "\t";
    	response = response + contactPoint.getEmailAddress() +"\t";
    	response = response + contactPoint.getFullName()+ "\t";
    	response = response + landingPage + "\t";
    	for (int i=0; i < programCodeList.size(); i++)
    	{
    		response = response + programCodeList.get(i) + ";";
    	}
    	response = response + "\t";
    	//response = response + bureauName + ", Department of Agriculture\t";
    	response = response + accessLevel + "\t";
    	response = response + rights+ "\t";
    	for (int i=0; i < keywordList.size(); i++)
    	{
    		response = response + keywordList.get(i) + ";";
    	}
    	response = response + "\t";
    	response = response + modified+ "\t";
    	response = response + issued + "\t";

    	response = response + uniqueIdentifier + "\t";
    	response = response + describedBy + "\t";
    	response = response + license + "\t";
    	response = response + spatial + "\t";
    	response = response + temporal+ "\t";
    	response = response + systemOfRecords + "\t";
    	response = response + dataQuality + "\t"; 	
    	for (int i=0; i < languageList.size(); i++)
    	{
    		response = response + languageList.get(i) + ";";
		}
    	response = response + "\t";
    	for (int i=0; i < programCodeList.size(); i++)
		{
    		response = response + programCodeList.get(i) + ";";
		}
    	response = response + "\t";
    	for (int i=0; i < themeList.size(); i++)
		{
    		response = response + themeList.get(i) + ";";
		}
    	response = response + "\t";
    	
    	for (int i=0; i < referenceList.size(); i++)
		{
    		response = response + referenceList.get(i) + ";";
		}
    	response = response + "\t";
    	
    	return response;
	}
	
	/**
	 * Converts Dataset object to Project Open Data compliant Map.
	 * <p>
	 * This comment is from previous version: Map was used over JSONObject to preserve attribute order.  This is outside the JSON spec
	 * but makes testing efficient (String == String)
	 *  
	 * @return JSONObject Version changed from a linked map (order preserved) of Dataset object in Project Open Data 1.1 compliant metadata.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject dataSetJSON = new JSONObject();
		dataSetJSON.put("title", title);
		dataSetJSON.put("description", description);
		dataSetJSON.put("keyword", keywordList);
		if (modified != null)
		{
			dataSetJSON.put("modified", Utils.convertDateToISOString(modified));
		}
		
		dataSetJSON.put("publisher", publisher.toProjectOpenDataJSON());	
		dataSetJSON.put ("contactPoint", contactPoint.toProjectOpenDataJSON());
		
		dataSetJSON.put("identifier", uniqueIdentifier);
		dataSetJSON.put("accessLevel", accessLevel);
		dataSetJSON.put("rights",rights) ;
		dataSetJSON.put("describedBy", describedBy);
		dataSetJSON.put("license", license);
		dataSetJSON.put("spatial", spatial);
		dataSetJSON.put("temporal", temporal);
		dataSetJSON.put("issued", issued);
		dataSetJSON.put("accrualPeriodicity", accrualPeriodicity);
		dataSetJSON.put("systemOfRecords", systemOfRecords);
		dataSetJSON.put("primaryITInvestmentUII", primaryITInvestmentUII);

		if (issued != null)
		{
			dataSetJSON.put("issued", Utils.convertDateToISOString(issued));
		}

		dataSetJSON.put("dataQuality", dataQuality);
		dataSetJSON.put("landingPage", landingPage);

		
		JSONArray distributionListJSONArray = new JSONArray();
		for (Distribution distribution: distributionList)
		{
			distributionListJSONArray.add(distribution.toProjectOpenDataJSON());
		}
		dataSetJSON.put("distribution", distributionListJSONArray);
		
		if (programCodeList.size() > 0)
		{
			dataSetJSON.put("programCode", programCodeList);
		}
		dataSetJSON.put("bureauCode", bureauCodeList);
		if (themeList.size() > 0)
		{
			dataSetJSON.put("theme", themeList);
		}
		if (referenceList.size() > 0)
		{
			dataSetJSON.put("references", referenceList);
		}
		if (languageList.size() > 0)
		{
			dataSetJSON.put("language", languageList);
		}

		dataSetJSON.put("notes", comments);
		
		//The following attributes are legacy from before Project Open Data
		//dataSetJSON.put("tagString", tagList);
		//dataSetJSON.put("revisionTimestamp", revisionTimeStamp);
		//dataSetJSON.put("dataDict", dataDict);
		//dataSetJSON.put("ownerOrg", ownerOrg);
		
		return dataSetJSON;
	}
	
	/**
	 * Converts Project Open Data compliant JSONObject to class Dataset
	 * <p>
	 * Straight forward parsing of POD compliant data which could probably be done in gson.  This is handparsed
	 * because of problems moving from 1.0 -> 1.1 plus the CKAN imports.  The goal is also compliance with
	 * DCAT.
	 * 
	 * @param dataSetObject JSONObject This is Project Open Data 1.1 compliant json object.
	 */
	public void loadFromProjectOpenDataJSON(JSONObject dataSetObject) throws DatasetException
	{
		if (dataSetObject == null)
		{
			throw new NullPointerException("datasetObject cannot be null");
		}
		
		setTitle((String) dataSetObject.get("title"));
		setDescription ((String) dataSetObject.get("description"));
		try{
			publisher.loadDatasetFromPOD_JSON((JSONObject)dataSetObject.get("publisher"));
		}
		catch (PublisherException e)
		{
			dsEx.addError(e.toString());
		}
		try{
			contactPoint.loadDatasetFromPOD_JSON((JSONObject)dataSetObject.get("contactPoint"));
		}
		catch (ContactException e)
		{
			dsEx.addError(e.toString());
		}
		setAccessLevel((String)dataSetObject.get("accessLevel"));
		setRights((String)dataSetObject.get("rights"));
		setSystemOfRecords((String)dataSetObject.get("systemOfRecords"));
		setLandingPage((String)dataSetObject.get("landingPage"));
		setTemporal((String)dataSetObject.get("temporal"));
		setModified ((String) dataSetObject.get("modified"));
		setUniqueIdentifier ((String) dataSetObject.get("identifier"));
		setSpatial((String)dataSetObject.get("spatial"));
		setConformsTo((String) dataSetObject.get("conformsTo"));
		
		//Data quality can be string in ckan or boolean in 
		final Object dQuality = dataSetObject.get("dataQuality");
		if (dQuality instanceof String)
		{
			setDataQuality ((String)dataSetObject.get("dataQuality"));
		}
		else
		{
			setDataQuality ((Boolean)dataSetObject.get("dataQuality"));
		}
		setIssued ((String) dataSetObject.get("issued"));	
		
		setDescribedBy((String)dataSetObject.get("describedBy"));
		setDescribedByType ((String) dataSetObject.get("describedByType"));
		setAccrualPeriodicity((String)dataSetObject.get("accrualPeriodicity"));
		setLicense((String) dataSetObject.get("license"));
		setPrimaryITInvestmentUII((String) dataSetObject.get("primaryITInvestmentUII"));
		setIsPartOf((String) dataSetObject.get("isPartOf"));

		bureauCodeList = loadArray("bureauCode", dataSetObject);
		keywordList = loadArray("keyword", dataSetObject);
		languageList = loadArray("language", dataSetObject);
		programCodeList = loadArray("programCode", dataSetObject);
		referenceList = loadArray("references", dataSetObject);
		themeList = loadArray("theme", dataSetObject);	
		
		final JSONArray distributionArray = (JSONArray)dataSetObject.get("distribution");
		
		if (distributionArray != null)
		{
			getDistribution(distributionArray);
		}
		
		if (!validateDataset() || dsEx.exceptionSize() > 0)
		{
			dsEx.setTitle(title);
			dsEx.setUniqueIdentifier(uniqueIdentifier);
			throw (dsEx);
		}
	}
	
	private void getDistribution(JSONArray distributionArray)
	{
		for (int i=0; i< distributionArray.size(); i++)
		{
			Distribution distribution = new Distribution();
			final JSONObject distributionObject = (JSONObject) distributionArray.get(i);
			try{
				distribution.loadFromProjectOpenDataJSON(distributionObject);
			}
			catch (DistributionException e)
			{
				dsEx.addError(e.toString());
			}
			distributionList.add(distribution);
		}
	}
	
	/**
	 * Loads List<String> into JSONArray for fields that are strings of lists.
	 * <p>
	 * A common datatype in Project Open Data is a list of strings.  Examples include bureauCode,
	 * Program Code, Theme and Language.  This is a helper.
	 * 
	 * @param key String The key in key-value.  For example bureauCode, category, language.
	 * @param dataSetObject JSONObject The dataset jsonobject in Project Open Data 1.1 compliance
	 * @return List<String> Conversion of the json to java List<String> instance variable.
	 */
	private List<String> loadArray(String key, JSONObject dataSetObject)
	{
		String value = "";
		JSONArray jsonArray = (JSONArray) dataSetObject.get(key);
		List<String> returnList = new ArrayList<String>();
		if (jsonArray != null)
		{
			for (int i=0; i<jsonArray.size(); i++)
			{
				value = (String) jsonArray.get(i);
				returnList.add(value);
			}
		}
		return returnList;
	}

	public String getTitle() {
		return title;
	}
	
	/**
	 * Changes title to CKAN compliant name.
	 * <p>
	 * There are several items that CKAN requires of name.  It can't have spaces, upper case, ".".
	 * This method will convert the title to compliance with CKAN conventions.
	 * @return String CKAN compliant naming identifier.
	 */
	public String getName(){
		String name = title.replace("-", "_");
		name = name.replace(" ", "-");
		name = name.toLowerCase();
		return name;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getConformsTo() {
		return conformsTo;
	}

	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
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
	
	private void setIssued(String issued) throws DatasetException
	{
		if (issued != null)
		{
			try{
				this.issued = Utils.convertISOStringToDate(issued);
			}
			catch(ParseException e)
			{
				throw new DatasetException("Invalid issued date", e);
				//throw e;
			}
		}
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public void setModified(String modified){
		if (modified != null)
		{
			try{
				this.modified = Utils.convertISOStringToDate(modified );
			}
			catch(ParseException e)
			{
				dsEx.addError("Modified field has invalid ISO Date" + e);
			}
		}
	}

	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

	public List<String> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<String> languageList) {
		this.languageList = languageList;
	}
	
	public void setLanguageList(String languageListString){
		this.languageList.add(languageListString);
	}

	public List<String> getThemeList() {
		return themeList;
	}

	public void setThemeList(List<String> themeList) {
		this.themeList = themeList;
	}
	

	/**
	 * Converts CSV to List for themes
	 * <p>
	 * CKAN will sometimes send this back as a string instead of JSONArray.
	 * This method converst that string to List<String>
	 * @param themeListString String The string to be converted to themeList which is List<String>
	 */
	private void setThemeList(String themeListString)
	{
		final String[] categoryArray = themeListString.split(",");
		if (categoryArray.length == 1)
		{
			themeList.add(themeListString);
		}
		else
		{
			for (int r = 0; r < categoryArray.length; r++)
			{
				themeList.add(categoryArray[r].trim());
			}
		}
	}

	public String getTemporal() {
		return temporal;
	}

	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}

	public String getSpatial() {
		return spatial;
	}

	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}

	public String getAccrualPeriodicity() {
		return accrualPeriodicity;
	}

	public void setAccrualPeriodicity(String accrualPeriodicity) {
		this.accrualPeriodicity = accrualPeriodicity;
	}

	public URL getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(URL landingPage) {
		this.landingPage = landingPage;
	}
	
	private void setLandingPage(String landingPage){
		if (landingPage != null)
		{
			try 
			{
				this.landingPage = new URL(landingPage);
			}
			catch(MalformedURLException e)
			{
				dsEx.addError("Landing Page is invalid URL." + e);
			}
		}
	}

	public List<String> getBureauCodeList() {
		return bureauCodeList;
	}

	/**
	 * bureau code must be in the following format 000:00 or NNN:NN.  This validates.
	 * @param bureauCode
	 */
	public void setBureauCodeList(String bureauCode) throws DatasetException{
		if (!bureauCodeList.contains(bureauCode))
		{
			if (Pattern.matches("\\d{3}:\\d{2}", bureauCode)){
				bureauCodeList.add(bureauCode);
			}
			else
			{
				dsEx.addError("Bureau Code must be \\d{3}:\\d{2}: " + bureauCode);
				throw dsEx;
			}
		}
	}

	public List<String> getProgramCodeList() {
		return programCodeList;
	}

	public void setProgramCode(String programCode) {
		if (!programCodeList.contains(programCode))	
		{
			programCodeList.add(programCode);
		}
	}

	public String getPrimaryITInvestmentUII() {
		return primaryITInvestmentUII;
	}

	public void setPrimaryITInvestmentUII(String primaryITInvestmentUII) {
		this.primaryITInvestmentUII = primaryITInvestmentUII;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		if (accessLevel != null)
		{
			if (accessLevel.equals("public") || accessLevel.equals("non-public") || accessLevel.equals("restricted"))
			{
				this.accessLevel = accessLevel;
			}
			else
			{
				dsEx.addError("access level must equal public, non-public or restricted");
			}
		}
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getSystemOfRecords() {
		return systemOfRecords;
	}

	public void setSystemOfRecords(String systemOfRecords) {
		this.systemOfRecords = systemOfRecords;
	}

	public Boolean getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(Boolean dataQuality) {
		this.dataQuality = dataQuality;
	}
	
	//handle string case
	private void setDataQuality(String dataQuality)
	{
		if (dataQuality != null)
		{
			if (dataQuality.equals("true"))
			{
				this.dataQuality = true;
			}
			else
			{
				this.dataQuality = false;
			}
		}
	}

	public List<String> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(List<String> referenceList) {
		this.referenceList = referenceList;
	}
	
	//CKAN can send this as a comma delimited field
	private void setReferenceList(String referenceListString) {
		final String[] referenceArray = referenceListString.split(",");
		
		if (referenceArray.length == 1)
		{
			referenceList.add(referenceListString);
		}
		else
		{
			for (int r = 0; r < referenceArray.length; r++)
			{
				referenceList.add(referenceArray[r].trim());
			}
		}
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getWebService() {
		return webService;
	}

	public void setWebService(String webService) {
		this.webService = webService;
	}

	public String getOwnerOrganization() {
		return ownerOrganization;
	}

	public void setOwnerOrganization(String ownerOrganization) {
		this.ownerOrganization = ownerOrganization;
	}

	public List<Distribution> getDistributionList() {
		return distributionList;
	}

	public void setDistributionList(List<Distribution> distributionList) {
		this.distributionList = distributionList;
	}
	
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
	public String getIsPartOf() {
		return isPartOf;
	}

	public void setIsPartOf(String isPartOf) {
		this.isPartOf = isPartOf;
	}
	
	/**
	 * Checks to make sure dataset business logic for Project Open Data 1.1 is valid
	 * <p>
	 * Required: title, description, keywordlist, modified, publisher, contactPoint, uniqueIdenifier
	 * accesslevel, bureauCode, programCode.  Other business rules will be added in the future.
	 * 
	 * This method also catches Publisher, ContactPoint exceptions.
	 * 
	 * 
	 * @return Boolean True of data set is valid; false if invalid dataset
	 */
	//TODO validate distribution accessurl for all public and restricted datasets
	//if Format == API and AccessURL == null
	//if downloadURL = "something" and accessURL == something
	//if downloadURL = null and access URL == null
	public Boolean validateDataset()
	{
		Boolean validIndicator = true;
		if (title == null)
		{
			dsEx.addError("Title is required.");
			validIndicator = false;
		}
		if (description == null)
		{
			dsEx.addError("Description is required.");
			validIndicator = false;
		}
		if (keywordList == null)
		{
			dsEx.addError(title);
			validIndicator = false;
		}
		if (modified == null)
		{
			dsEx.addError("Modified is required.");
			validIndicator = false;
		}
		try{
			if (!publisher.validatePublisher())
			{
				validIndicator = false;
			}
		}
		catch (PublisherException e){
			dsEx.addError(e.toString());
		}
		try{
			if (!contactPoint.validateContact())
			{
				validIndicator = false;
			}
		}
		catch (ContactException e)
		{
			dsEx.addError(e.toString());
		}
		
		if (uniqueIdentifier == null)
		{
			dsEx.addError("Identifier is required.");
			validIndicator = false;
		}
		if (accessLevel == null)
		{
			dsEx.addError("Access Level is required.");
			validIndicator = false;
		}
		//extra distribution checks for dataset
		if (distributionList.size() == 0 && !accessLevel.equals("non-public"))
		{
			dsEx.addError("At least one distribution is required when dataset is public or restricted.");
			validIndicator = false;
		}
		for (int i=0; i< distributionList.size(); i++)
		{
			final Distribution d = distributionList.get(i);
			try{
				if (accessLevel.equals("public") || accessLevel.equals("restricted"))
				{
					d.validatePublicDistribution();
				}
			}
			catch(DistributionException e)
			{
				dsEx.addError(e.toString());
			}
		}
		if (bureauCodeList == null)
		{
			dsEx.addError("Bureau Code is required.");
			validIndicator = false;
		}
		if (programCodeList == null)
		{
			dsEx.addError("Program Code is required.");
			validIndicator = false;
		}
		
		return validIndicator;			
	}
	
	/**
	 * Does not include legacy or class specific variables: commments, dsEx, ownerOrganization, webService
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Dataset))
		{
			return false;
		}
		Dataset ds_other = (Dataset)o;
		
		return new EqualsBuilder()
         .append(title, ds_other.title)
         .append(description, ds_other.description)
         .append(accessLevel, ds_other.accessLevel)
         .append(accrualPeriodicity, ds_other.accrualPeriodicity)
         .append(bureauCodeList, ds_other.bureauCodeList)
         .append(conformsTo, ds_other.conformsTo)
         .append(dataQuality, ds_other.dataQuality)
         .append(describedBy, ds_other.describedBy)
         .append(describedByType, ds_other.describedByType)
         .append(isPartOf, ds_other.isPartOf)
         .append(issued, ds_other.issued)
         .append(keywordList, ds_other.keywordList)
         .append(landingPage, ds_other.landingPage)
         .append(languageList, ds_other.languageList)
         .append(license, ds_other.license)
         .append(modified, ds_other.modified)
         .append(primaryITInvestmentUII, ds_other.primaryITInvestmentUII)
         .append(programCodeList, ds_other.programCodeList)
         .append(referenceList, ds_other.referenceList)
         .append(rights, ds_other.rights)
         .append(spatial, ds_other.spatial)
         .append(systemOfRecords, ds_other.systemOfRecords)
         .append(temporal, ds_other.temporal)
         .append(themeList, ds_other.themeList)
         .append(uniqueIdentifier, ds_other.uniqueIdentifier)
         .append(contactPoint, ds_other.contactPoint)
         .append(publisher, ds_other.publisher)
         .append(distributionList, ds_other.distributionList)
         .isEquals();
	}
	
	/**
	 * Does not include legacy or class specific variables: commments, dsEx, ownerOrganization, webService
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(19, 37).
				append(title).
				append(description).
				append(accessLevel).
				append(accrualPeriodicity).
				append(bureauCodeList).
				append(conformsTo).
				append(dataQuality).
				append(describedBy).
				append(describedByType).
				append(isPartOf).
				append(issued).
				append(keywordList). 
				append(landingPage). 
				append(languageList). 
				append(license). 
				append(modified). 
				append(primaryITInvestmentUII). 
				append(programCodeList). 
				append(referenceList). 
				append(rights). 
				append(spatial). 
				append(systemOfRecords). 
				append(temporal). 
				append(themeList). 
				append(uniqueIdentifier).
				append(contactPoint). 
				append(publisher). 
				append(distributionList).
				toHashCode();
	}

	@Override
	public String toString() {
		return "Dataset [title=" + title + ", description=" + description
				+ ", issued=" + issued + ", modified=" + modified
				+ ", keywordList=" + keywordList + ", languageList="
				+ languageList + ", themeList=" + themeList + ", contactPoint="
				+ contactPoint + ", publisher=" + publisher + ", temporal="
				+ temporal + ", spatial=" + spatial + ", accrualPeriodicity="
				+ accrualPeriodicity + ", landingPage=" + landingPage
				+ ", distributionList=" + distributionList
				+ ", uniqueIdentifier=" + uniqueIdentifier
				+ ", bureauCodeList=" + bureauCodeList + ", programCodeList="
				+ programCodeList + ", primaryITInvestmentUII="
				+ primaryITInvestmentUII + ", accessLevel=" + accessLevel
				+ ", rights=" + rights + ", systemOfRecords=" + systemOfRecords
				+ ", dataQuality=" + dataQuality + ", referenceList="
				+ referenceList + ", describedBy=" + describedBy
				+ ", describedByType=" + describedByType + ", license="
				+ license + ", conformsTo=" + conformsTo + ", isPartOf="
				+ isPartOf + ", comments=" + comments + ", webService="
				+ webService + ", ownerOrganization=" + ownerOrganization + "]";
	}
	
	
	
}
