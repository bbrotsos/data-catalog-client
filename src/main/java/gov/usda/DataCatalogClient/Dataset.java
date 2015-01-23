package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Dataset class is based on Project Open Data metadata specification 1.1. More
 * details can be found here: https://project-open-data.cio.gov/v1.1/schema/#dataset
 * 
 * There are three ways to load the dataset object:
 * <p>
 * 1.  Load Dataset from a CKAN JSON Object.    @see loadDatasetFromCKAN_JSON(JSONObject):void
 * 2.  Load Dataset from a Project Open Data compliant JSON Object.  @see loadDatasetFromProjectOpenDataJSON(JSONObject):void
 * 3.  Load dataset from Project Open Data file @see loadDatasetFromFile(String)
 * <p>
 * These methods will throw a Dataset Exception if the JSON is not in Project
 * Open Data compliance.
 * <p>
 * Also, the variables are exposed so another way to populate Dataset is to set variables like 
 * <code>
 * Dataset ds = new Dataset();
 * ds.setTitle("My new Title");
 * ...
 * ds.validate();  //validates to ensure Project Open Data Compliance.
 * </code>
 * No builders were implemented because the most normal use-case is loading from a JSON file.
 * <p>
 * The Dataset class also has methods for outputting the dataset in multiple formats.
 * <p>
 * 1.  CKAN formated JSON Object: @see toCKAN_JSON():JSONObject
 * 2.  Project Open Data compliant Map.  Linked HashMap is used to preserve order against JSON specification.  This is used
 * for testing.  @see toProjectOpenDataJSON(): Map
 * 3.  Text delimited format for opening in Excel, etc.  This is tab delimited format  @see toCSV()String
 * 
 * @author bbrotsos
 *
 */
public class Dataset implements Comparable<Dataset> {

	//Project Open Data 1.1 JSON fields  https://project-open-data.cio.gov/v1.1/schema/
	public final static String PROJECT_OPEN_DATA_DATASET = "dataset";
	public final static String PROJECT_OPEN_DATA_DATASET_ACCESS_LEVEL = "accessLevel";
	public final static String PROJECT_OPEN_DATA_DATASET_ACCRUAL_PERIODICITY = "accrualPeriodicity";
	public final static String PROJECT_OPEN_DATA_DATASET_BUREAU_CODE = "bureauCode";
	public final static String PROJECT_OPEN_DATA_DATASET_CONFORMS_TO = "conformsTo";
	public final static String PROJECT_OPEN_DATA_DATASET_DATA_QUALITY = "dataQuality";
	public final static String PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY = "describedBy";
	public final static String PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY_TYPE = "describedByType";
	public final static String PROJECT_OPEN_DATA_DATASET_DESCRIPTION = "description";
	public final static String PROJECT_OPEN_DATA_DATASET_UNIQUE_IDENTIFIER = "identifier";
	public final static String PROJECT_OPEN_DATA_DATASET_IS_PART_OF = "isPartOf";
	public final static String PROJECT_OPEN_DATA_DATASET_ISSUED = "issued";
	public final static String PROJECT_OPEN_DATA_DATASET_LICENSE = "license";
	public final static String PROJECT_OPEN_DATA_DATASET_KEYWORD = "keyword";
	public final static String PROJECT_OPEN_DATA_DATASET_LANDING_PAGE = "landingPage";
	public final static String PROJECT_OPEN_DATA_DATASET_LANGUAGE = "language";
	public final static String PROJECT_OPEN_DATA_DATASET_MODIFIED = "modified";
	public final static String PROJECT_OPEN_DATA_DATASET_PRIMARY_IT_INVESTMENT_UII = "primaryITInvestmentUII";
	public final static String PROJECT_OPEN_DATA_DATASET_PROGRAM_CODE = "programCode";
	public final static String PROJECT_OPEN_DATA_DATASET_REFERENCES = "references";
	public final static String PROJECT_OPEN_DATA_DATASET_RIGHTS = "rights";
	public final static String PROJECT_OPEN_DATA_DATASET_SPATIAL = "spatial";
	public final static String PROJECT_OPEN_DATA_DATASET_SYSTEM_OF_RECORDS = "systemOfRecords";
	public final static String PROJECT_OPEN_DATA_DATASET_TEMPORAL = "temporal";
	public final static String PROJECT_OPEN_DATA_DATASET_TITLE = "title";
	public final static String PROJECT_OPEN_DATA_DATASET_THEME = "theme";

	//only using where CKAN differs from Project Open Data
	//TODO: Description can be placed at package or extra level
	//TODO: Title can be placed at package or extra level
	public final static String CKAN_DATASET = "packages";
	public final static String CKAN_DATASET_DISTRIBUTION = "resources";
	public final static String CKAN_DATASET_DESCRIPTION_NOTES = "notes";
	public final static String CKAN_DATASET_DESCRIPTION = "description";
	public final static String CKAN_DATASET_EXTRAS = "extras";
	public final static String CKAN_DATASET_ACCESS_LEVEL = "public_access_level";
	public final static String CKAN_DATASET_ACCRUAL_PERIODICITY = "accrual_periodicity";
	public final static String CKAN_DATASET_BUREAU_CODE_LIST = "bureau_code";
	public final static String CKAN_DATASET_CONFORMS_TO = "conforms_to";
	public final static String CKAN_DATASET_DATA_QUALITY = "data_quality";
	public final static String CKAN_DATASET_DATA_QUALITY_LEGACY ="dataQuality";
	public final static String CKAN_DATASET_DESCRIBED_BY = "data_dictionary";
	public final static String CKAN_DATASET_DESCRIBED_BY_LEGACY = "data_dict";
	public final static String CKAN_DATASET_DESCRIBED_BY_TYPE = "data_dictionary_type";
	public final static String CKAN_DATASET_IS_PART_OF = "is_parent";
	/**
	 * CKAN has extra 'release_date' as a reserved word.  When creating a dataset this software
	 * outputs CKAN_DATASET_ISSUED, when loading it looks for both CKAN_DATASET_ISSUED
	 * and CKAN_DATASET_RELEASE_DATE
	 */
	public final static String CKAN_DATASET_ISSUED = "issued";
	public final static String CKAN_DATASET_RELEASE_DATE = "release_date";
	public final static String CKAN_DATASET_LANDING_PAGE = "homepage_url";
	public final static String CKAN_DATASET_LANGUAGE = "language";
	public final static String CKAN_DATASET_LICENSE = "license_new";
	public final static String CKAN_DATASET_MODIFIED = "modified";
	public final static String CKAN_DATASET_PRIMARY_IT_INVESTMENT_UII = "primary_it_investment_uii";
	public final static String CKAN_DATASET_PROGRAM_CODE = "program_code";
	public final static String CKAN_DATASET_PROGRAM_CODE_LEGACY = "program_cdoe";
	public final static String CKAN_DATASET_REFERENCES = "related_documents";
	public final static String CKAN_DATASET_RIGHTS = "access_level_comment";
	/**
	 * CKAN spatial for extras is a keyword.  This puts SPATIAL_TEXT when creating a dataset
	 * but looks for both SPATIAL AND SPATIAL_TEXT when loading.  More here:
	 * http://docs.ckan.org/en/ckan-1.7/geospatial.html
	 */
	public final static String CKAN_DATASET_SPATIAL = "spatial";
	public final static String CKAN_DATASET_SPATIAL_TEXT = "spatial-text";
	public final static String CKAN_DATASET_SYSTEM_OF_RECORDS = "system_of_records";
	public final static String CKAN_DATASET_TEMPORAL = "temporal";
	public final static String CKAN_DATASET_THEME = "category";
	public final static String CKAN_DATASET_TITLE = "title";
	public final static String CKAN_DATASET_UNIQUE_IDENTIFIER = "unique_id";
	
	//additional field for CKAN to make private
	public final static String CKAN_DATASET_PRIVATE = "private";
	public final static Boolean CKAN_DATASET_PRIVATE_VALUE = true;
	
	/**
	 * There are 3 attributes in CKAN at Dataset level.
	 */
	public final static String CKAN_DATASET_REVISION_TIMESTAMP = "revision_timestamp";
	public final static String CKAN_DATASET_METADATA_CREATED = "metadata_created";
	public final static String CKAN_DATASET_METADATA_MODIFIED = "metadata_modified";
	
	//Enumeration for handling access levels.  More documentation at Project Open Data.
	public enum AccessLevel
	{
		PUBLIC("public"), 
		RESTRICTED("restricted public"), 
		PRIVATE ("non-public");
		
		private final String accessLevel;
		
		private AccessLevel(String accessLevel)
		{
			this.accessLevel = accessLevel;
		}
		@Override
		public String toString()
		{
			return this.accessLevel;
		}
	};

	//metadata documentation is at http://www.w3.org/TR/vocab-dcat/
	private String accrualPeriodicity;
	private Contact contactPoint;
	private String description;
	private List<Distribution> distributionList;
	private Date issued;
	private List<String> keywordList;
	private URL landingPage;
	private List<String> languageList;
	private Date modified;
	private Publisher publisher;
	private String spatial;
	private List<String> themeList;
	private String temporal;
	private String title;
	
	//federal government project open data extension documentation here: https://project-open-data.cio.gov/v1.1/schema/
	private String accessLevel;
	private List<String> bureauCodeList;
	private String conformsTo;
	private Boolean dataQuality;
	private String describedBy;
	private String describedByType;
	private String isPartOf;
	private String license;
	private List<String> programCodeList;
	private String primaryITInvestmentUII;
	private List<String> referenceList;
	private String rights;
	private String systemOfRecords;
	private String uniqueIdentifier;
	
	//CKAN Specific
	private Date metadataModifiedDate;
	private Date metadataCreatedDate;
	private Date revisionTimeStamp;
	
	//This needs to be populated in order run create
	private String ownerOrganization;

	//Agency specific (legacy)
	private String comments;
	private String webService;
	
	//Bureau specific (might move to struct or class)
	private String bureauName;
	private String bureauAbbreviation;
	
	private DatasetException dsEx;
	private static final Logger log = Logger.getLogger(Dataset.class.getName());


	public Dataset()
	{
		contactPoint = new Contact();
		bureauCodeList = new ArrayList<String>();
		dsEx = new DatasetException();
		distributionList = new ArrayList<Distribution>();
		keywordList = new ArrayList<String>();
		languageList = new ArrayList<String>();
		programCodeList = new ArrayList<String>();
		publisher = new Publisher();
		referenceList = new ArrayList<String>();
		themeList = new ArrayList<String>();
	}
	
	//TODO: Add documentation to methods
	private void loadDistributionListFromCKAN(JSONArray resourcesArray)
	{
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
	}
	
	/**
	 * This method takes a key and value using the CKAN Extra format and converts it to 
	 * DCAT/Project Open Data.
	 * @param key String The key in the CKAN extra field.
	 * @param value String The value in the CKAN extra fields
	 * @throws ParseException  This will be thrown if the dates: issued or modified is not valid iso dates
	 */
	private void loadExtraFromCKAN(String key, String value) throws ParseException
	{
		Publisher subOrganization = null;
		value.trim();
		switch (key)
		{
			case CKAN_DATASET_ACCESS_LEVEL: setAccessLevel(value); break;
			case CKAN_DATASET_ACCRUAL_PERIODICITY: setAccrualPeriodicity(value); break;
			case CKAN_DATASET_BUREAU_CODE_LIST: setBureauCodeList(value); break;
			case CKAN_DATASET_CONFORMS_TO: setConformsTo(value); break;
			case CKAN_DATASET_DATA_QUALITY:
			case CKAN_DATASET_DATA_QUALITY_LEGACY: setDataQuality(value); break;
			case CKAN_DATASET_DESCRIBED_BY:
			case CKAN_DATASET_DESCRIBED_BY_LEGACY:setDescribedBy(value); break;
			case CKAN_DATASET_DESCRIBED_BY_TYPE: setDescribedByType(value); break;
			case CKAN_DATASET_DESCRIPTION: setDescription(value); break;
			case CKAN_DATASET_IS_PART_OF: setIsPartOf(value); break;
			case CKAN_DATASET_ISSUED:
			case CKAN_DATASET_RELEASE_DATE: setIssued(value); break;
			case CKAN_DATASET_LANDING_PAGE: setLandingPage(value); break;
			case CKAN_DATASET_LANGUAGE: setLanguageList(value); break;
			case CKAN_DATASET_LICENSE: setLicense(value); break;
			case CKAN_DATASET_MODIFIED: setModified(value); break;
			case CKAN_DATASET_PRIMARY_IT_INVESTMENT_UII: setPrimaryITInvestmentUII(value); break;
			case CKAN_DATASET_PROGRAM_CODE: 
			case CKAN_DATASET_PROGRAM_CODE_LEGACY: setProgramCodeList(value); break;
			case CKAN_DATASET_REFERENCES: setReferenceList(value); break;
			case CKAN_DATASET_RIGHTS: setRights(value); break;
			case CKAN_DATASET_SPATIAL:
			case CKAN_DATASET_SPATIAL_TEXT: setSpatial(value); break;
			case CKAN_DATASET_SYSTEM_OF_RECORDS: setSystemOfRecords(value); break;
			case CKAN_DATASET_TEMPORAL: setTemporal(value); break;
			case CKAN_DATASET_THEME: setThemeList(value); break;
			case CKAN_DATASET_TITLE: setTitle(value); break;
			case CKAN_DATASET_UNIQUE_IDENTIFIER: setUniqueIdentifier(value); break;
			case Contact.CKAN_CONTACT_EMAIL_ADDRESS: contactPoint.setEmailAddress(value); break;
			case Contact.CKAN_CONTACT_FULL_NAME: contactPoint.setFullName(value); break;
			case Publisher.CKAN_PUBLISHER_NAME: publisher.setName(value); break;
			case Publisher.CKAN_PUBLISHER_SUBORGANIZATION_NAME : 
				subOrganization = new Publisher();
				subOrganization.setName(value); break;
		}
		
		//move these lines elsewhere
		contactPoint.setType("vcard:Contact");
		publisher.setType("org:Organization");

		if (subOrganization != null)
		{
			subOrganization.setType("org:Organization");
			publisher.setSubOrganization(subOrganization);
		}
	}
	
	/**
	 * For each CKAN extra loads into this dataset object.
	 * @param extraList
	 * @throws DatasetException
	 */
	private void loadExtraListFromCKAN(JSONArray extraList) throws DatasetException
	{
	    for (int i = 0; i < extraList.size(); i++)
		{			
			JSONObject extraObject = (JSONObject) extraList.get(i);
			String key = (String) extraObject.get("key");
			String value = (String) extraObject.get("value");
			
			try{
				loadExtraFromCKAN(key, value);
			}
			catch(ParseException e)
			{
				dsEx.addError(e.toString());
			}
		}
	}
	
	/**
	 * This method takes a valid data.json file that conforms to Dataset in Project open data
	 * and loads it into Dataset object.
	 * @param podFileName String The name of the file to import.
	 * @throws DatasetException This is thrown when the data.json file is not valid Project Open Data 1.1 json format.
	 * This can include business validation rules. 
	 * @throws IOException This is thrown if there is issue reading the file, most likely file does not exist
	 */
	public void loadDatasetFromFile(String podFileName) throws DatasetException, IOException
	{
		JSONObject datasetObject;
		try{
		datasetObject = Utils.loadJsonObjectFile(podFileName);
		}
		catch (org.json.simple.parser.ParseException e)
		{
			throw new IOException ("Invalid JSON file:" + podFileName + " " + e.toString());
		}
		loadFromProjectOpenDataJSON(datasetObject);
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
				
		//issue, title is in two places. To solve this set it initially, and let extra tag overwrite if it exists in extra.
		setTitle((String) datasetCKAN_JSON.get("title"));
		setDescription((String) datasetCKAN_JSON.get(CKAN_DATASET_DESCRIPTION_NOTES));
	    
		//TODO:Both of these are set to anaylize when CKAN metadata modified date is different from
		//extra where key equals "modified"
		setModified ((String) datasetCKAN_JSON.get(CKAN_DATASET_METADATA_MODIFIED));
	    setMetadataModifiedDate ((String) datasetCKAN_JSON.get(CKAN_DATASET_METADATA_MODIFIED));
	    setMetadataCreatedDate((String) datasetCKAN_JSON.get(CKAN_DATASET_METADATA_CREATED));
	    setRevisionTimeStamp((String) datasetCKAN_JSON.get(CKAN_DATASET_REVISION_TIMESTAMP));
	    
	    loadDistributionListFromCKAN((JSONArray) datasetCKAN_JSON.get(CKAN_DATASET_DISTRIBUTION));
	    
	    final JSONArray extraList = (JSONArray) datasetCKAN_JSON.get(CKAN_DATASET_EXTRAS);
	    if (extraList == null)
	    {
	    	throw new IllegalArgumentException("JSON is invalid.  extras array is required.");
	    }
	    else
	    {
	    	loadExtraListFromCKAN(extraList);
	    }
	    
		loadKeywordsFromCKAN((JSONArray)datasetCKAN_JSON.get("tags"));
		
		if (!validateDataset() || dsEx.exceptionSize() > 0)
		{
			throw (dsEx);
		}
	}
	
	/**
	 * Loads CKAN tags into this object.  
	 * @param tagsArray An array from CKAN
	 */
	private void loadKeywordsFromCKAN(JSONArray tagsArray)
	{
		if (tagsArray == null)
		{
			throw new IllegalArgumentException("JSON is invalid for Project Open Data.  Expecting 'tags' array.");
		}
		
		for(int k=0; k<tagsArray.size(); k++)
		{
			final JSONObject tagObject = (JSONObject)tagsArray.get(k);
			//TODO: use static final
			keywordList.add((String)tagObject.get("display_name"));
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
		
		//TODO: make this a variable.  Always set to Private for testing
		//datasetCKAN_JSON.put(CKAN_DATASET_PRIVATE, CKAN_DATASET_PRIVATE_VALUE);
		
		//TODO: take out hardcoded "name, notes, etc"
		datasetCKAN_JSON.put("name", getName());
		datasetCKAN_JSON.put("notes", description);
		datasetCKAN_JSON.put("title", this.title);
		datasetCKAN_JSON.put("owner_org", ownerOrganization);
		
		datasetCKAN_JSON.put(CKAN_DATASET_EXTRAS, addCkanExtras());
		
		JSONArray tagsArray = new JSONArray();
		for (int i = 0; i < keywordList.size(); i++)
		{
			JSONObject tagObject = new JSONObject();
			tagObject.put("name", keywordList.get(i));
			tagObject.put("display_name", keywordList.get(i));
			tagsArray.add(tagObject);
		}
		datasetCKAN_JSON.put("tags", tagsArray);
		
		//add distribution
		JSONArray distributionArray = new JSONArray();
		for (int i=0; i < distributionList.size(); i++)
		{
			JSONObject distributionObject = new JSONObject();
			distributionObject = distributionList.get(i).toCKAN_JSON();
			distributionArray.add(distributionObject);
		}
		datasetCKAN_JSON.put(CKAN_DATASET_DISTRIBUTION, distributionArray);
		
		return datasetCKAN_JSON;
	}
	
	/**
	 * Complies the extra list in special CKAN format that supports Project Open Data.
	 * @return
	 */
	//Suppressing Warnings, there is no way to turn fix this with JSON Object unless I change
	//the package
	@SuppressWarnings("unchecked")
	private JSONArray addCkanExtras()
	{
		JSONArray extrasArray = new JSONArray();
		
		//The fields title and description are added at both the Dataset and Extra
		extrasArray.add(createExtraObject(PROJECT_OPEN_DATA_DATASET_TITLE, title));
		extrasArray.add(createExtraObject("notes", description));
		extrasArray.add(createExtraObject(CKAN_DATASET_ACCESS_LEVEL, accessLevel));
		extrasArray.add(createExtraObject(CKAN_DATASET_ACCRUAL_PERIODICITY, accrualPeriodicity));
		if (contactPoint != null)
		{
			extrasArray.add(createExtraObject(Contact.CKAN_CONTACT_EMAIL_ADDRESS, contactPoint.getEmailAddress()));
			extrasArray.add(createExtraObject(Contact.CKAN_CONTACT_FULL_NAME, contactPoint.getFullName()));
		}
		if (conformsTo != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_CONFORMS_TO, conformsTo));
		}
		if (dataQuality != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_DATA_QUALITY, dataQuality.toString()));
		}
		if (describedBy != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_DESCRIBED_BY, describedBy));
		}
		if (describedByType != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_DESCRIBED_BY_TYPE, describedByType));
		}
		if (isPartOf != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_IS_PART_OF, isPartOf));
		}
		if (landingPage != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_LANDING_PAGE, landingPage.toString()));
		}
		if (license != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_LICENSE, license));
		}
		if (modified != null)
		{
			//TODO: uncomment this line, testing create on ckan
			//extrasArray.add(createExtraObject(CKAN_DATASET_MODIFIED, Utils.convertDateToISOString(modified)));
		}
		extrasArray.add(createExtraObject(CKAN_DATASET_PRIMARY_IT_INVESTMENT_UII, primaryITInvestmentUII));
		if (publisher != null)
		{
			extrasArray.add(createExtraObject(Publisher.CKAN_PUBLISHER, publisher.getName()));
		}
		
		if (issued != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_ISSUED, Utils.convertDateToISOString(issued)));
		}
		
		if (rights != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_RIGHTS, rights));
		}
		//spatial might break create, this was commented out
		//This should really be spatial-text.  spatial is a reserverd word, more here: http://docs.ckan.org/en/ckan-1.7/geospatial.html
		if (spatial != null)
		{
			extrasArray.add(createExtraObject (CKAN_DATASET_SPATIAL_TEXT, spatial));
		}
		if (systemOfRecords != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_SYSTEM_OF_RECORDS, systemOfRecords));
		}
		if (temporal != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_TEMPORAL, temporal));
		}
		if (uniqueIdentifier != null)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_UNIQUE_IDENTIFIER, uniqueIdentifier));
		}
		//add lists
		extrasArray.add(createExtraObject(CKAN_DATASET_BUREAU_CODE_LIST, Utils.listToCSV(bureauCodeList)));
		extrasArray.add(createExtraObject(CKAN_DATASET_LANGUAGE, Utils.listToCSV(languageList)));
		extrasArray.add(createExtraObject(CKAN_DATASET_PROGRAM_CODE, Utils.listToCSV(programCodeList)));
		extrasArray.add(createExtraObject(CKAN_DATASET_REFERENCES,Utils.listToCSV(referenceList)));
		if (themeList.size() > 0)
		{
			extrasArray.add(createExtraObject(CKAN_DATASET_THEME, Utils.listToCSV(themeList)));
		}
		//get rid of nulls, ckan will give errors
		//TODO: Optimize where null values are added, too many if!null is cluttering code.
		for (int i=0; i< extrasArray.size(); i++)
		{
			JSONObject extraObject = (JSONObject) extrasArray.get(i);
			if (extraObject.get("value") == null)
			{
				extrasArray.remove(i);
			}
		}
		return extrasArray;
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
		if (value != null)
		{
			extraObject.put("key", key);
			extraObject.put("value", value);
		}
		return extraObject;
	}
	
	/**
	 * This is to use Apache CSVParser
	 * @return
	 */
	public List<String> datasetToListString()
	{
		List<String> datasetString = new ArrayList<String>();
		datasetString.add(bureauName);
		datasetString.add(title);
		datasetString.add(description);
		
		//TODO: move to distribution class
    	if (distributionList.size() > 0)
		{
    		String mediaTypeString = "";
			String downloadURLString = "";
			for (int i=0; i< distributionList.size(); i++)
    		{
    			if (i > 0)
    			{
    				downloadURLString = downloadURLString  + ", ";
    				mediaTypeString = mediaTypeString + ", ";
    			}
				Distribution outputDistribution = distributionList.get(i);
				mediaTypeString = mediaTypeString + outputDistribution.getMediaType();
				
				downloadURLString = downloadURLString + outputDistribution.getDownloadURL();
    		}
			datasetString.add(mediaTypeString);
			datasetString.add(downloadURLString);
		}

    	datasetString.add(accrualPeriodicity);
    	datasetString.add(bureauCodeList.get(0));
    	datasetString.add(contactPoint.getEmailAddress());
    	datasetString.add(contactPoint.getFullName());
    	if (landingPage != null)
    	{
    		datasetString.add(landingPage.toString());
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	String programCodeString = "";
    	for (int i=0; i < programCodeList.size(); i++)
		{
    		programCodeString = programCodeString + programCodeList.get(i) + ";";
		}
    	datasetString.add(programCodeString);
    	datasetString.add(publisher.getName());
    	datasetString.add(accessLevel);
    	datasetString.add(rights );
    	String keywordString = "";
    	for (int i=0; i < keywordList.size(); i++)
		{
    		keywordString = keywordString + keywordList.get(i) + ";";
		}
    	datasetString.add(keywordString);
    	datasetString.add(Utils.convertDateToISOString(modified));
    	if (issued != null)
    	{
    		datasetString.add(Utils.convertDateToISOString(issued));
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	datasetString.add(uniqueIdentifier);
    	datasetString.add(describedBy);
    	datasetString.add(license);
    	datasetString.add(spatial);
    	datasetString.add(temporal);
    	datasetString.add(systemOfRecords);
    	if (dataQuality != null)
    	{
    		datasetString.add(dataQuality.toString());
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	String languageListString = "";
    	for (int i=0; i < languageList.size(); i++)
    	{
    		languageListString = languageListString + languageList.get(i) + ";";
		}
    	datasetString.add(languageListString);
    	String themeListString = "";
    	for (int i=0; i < themeList.size(); i++)
		{
    		themeListString = themeListString + themeList.get(i) + ";";
		}
    	datasetString.add(themeListString);
    	String referenceListString = "";
    	for (int i=0; i < referenceList.size(); i++)
		{
    		referenceListString = referenceListString + referenceList.get(i) + ";";
		}
    	datasetString.add(referenceListString);
    	
    	if (metadataCreatedDate != null)
    	{
    		datasetString.add(Utils.convertDateToISOString(metadataCreatedDate));
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	if (metadataModifiedDate != null)
    	{
    		datasetString.add(Utils.convertDateToISOString(metadataModifiedDate));
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	if (revisionTimeStamp != null)
    	{
    		datasetString.add(Utils.convertDateToISOString(revisionTimeStamp));
    	}
    	else
    	{
    		datasetString.add(null);
    	}
    	if (!(isPartOf == null) && !isPartOf.equals("false"))
    	{
    		datasetString.add(isPartOf);
    	}
    	
    	return datasetString;
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
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_TITLE, title);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_DESCRIPTION, description);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_KEYWORD, keywordList);
		if (modified != null)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_MODIFIED, Utils.convertDateToISOString(modified));
		}
		
		dataSetJSON.put(Publisher.PROJECT_OPEN_DATA_PUBLISHER, publisher.toProjectOpenDataJSON());	
		dataSetJSON.put (Contact.PROJECT_OPEN_DATA_CONTACT_POINT, contactPoint.toProjectOpenDataJSON());
		
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_UNIQUE_IDENTIFIER, uniqueIdentifier);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_ACCESS_LEVEL, accessLevel);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_CONFORMS_TO, conformsTo);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_RIGHTS, rights);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY, describedBy);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY_TYPE, describedByType);
    	if (!(isPartOf == null) && !isPartOf.equals("false"))
    	{
    		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_IS_PART_OF, isPartOf);
    	}
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_LICENSE, license);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_SPATIAL, spatial);
		if (temporal != null && temporal.isEmpty() && !temporal.equals(""))
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_TEMPORAL, temporal);
		}
		if (issued != null)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_ISSUED, Utils.convertDateToISOString(issued));
		}		
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_ACCRUAL_PERIODICITY , accrualPeriodicity);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_SYSTEM_OF_RECORDS, systemOfRecords);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_PRIMARY_IT_INVESTMENT_UII, primaryITInvestmentUII);
		
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_DATA_QUALITY, dataQuality);
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_LANDING_PAGE, landingPage);

		JSONArray distributionListJSONArray = new JSONArray();
		for (Distribution distribution: distributionList)
		{
			distributionListJSONArray.add(distribution.toProjectOpenDataJSON());
		}
		dataSetJSON.put(Distribution.PROJECT_OPEN_DATA_DISTRIBUTION, distributionListJSONArray);
		
		if (programCodeList.size() > 0)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_PROGRAM_CODE, programCodeList);
		}
		dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_BUREAU_CODE, bureauCodeList);
		if (themeList.size() > 0)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_THEME, themeList);
		}
		if (referenceList.size() > 0)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_REFERENCES, referenceList);
		}
		if (languageList.size() > 0)
		{
			dataSetJSON.put(PROJECT_OPEN_DATA_DATASET_LANGUAGE, languageList);
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
		
		setAccessLevel((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_ACCESS_LEVEL));
		setAccrualPeriodicity((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_ACCRUAL_PERIODICITY));
		setConformsTo((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_CONFORMS_TO));
		setDataQuality (dataSetObject.get(PROJECT_OPEN_DATA_DATASET_DATA_QUALITY));
		setDescribedBy((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY));
		setDescribedByType ((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_DESCRIBED_BY_TYPE));
		setDescription ((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_DESCRIPTION));
		setIsPartOf((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_IS_PART_OF));
		setIssued ((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_ISSUED));	
		setLandingPage(dataSetObject.get(PROJECT_OPEN_DATA_DATASET_LANDING_PAGE));
		setLicense((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_LICENSE));
		setModified ((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_MODIFIED));
		setPrimaryITInvestmentUII((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_PRIMARY_IT_INVESTMENT_UII));
		setRights((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_RIGHTS));
		setSpatial((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_SPATIAL));
		setSystemOfRecords((String)dataSetObject.get(PROJECT_OPEN_DATA_DATASET_SYSTEM_OF_RECORDS));
		setTitle((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_TITLE));
		setTemporal((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_TEMPORAL));
		setUniqueIdentifier ((String) dataSetObject.get(PROJECT_OPEN_DATA_DATASET_UNIQUE_IDENTIFIER));

		//These object returned for bureauCode and programCode could either be ArrayList or JSONArray.
		setBureauCodeList(dataSetObject.get(PROJECT_OPEN_DATA_DATASET_BUREAU_CODE));
		setProgramCodeList(dataSetObject.get(PROJECT_OPEN_DATA_DATASET_PROGRAM_CODE));
		
		//Common method for loading simple string arrays with no parsing exceptions
		keywordList = loadArray(PROJECT_OPEN_DATA_DATASET_KEYWORD, dataSetObject);
		languageList = loadArray(PROJECT_OPEN_DATA_DATASET_LANGUAGE, dataSetObject);
		referenceList = loadArray(PROJECT_OPEN_DATA_DATASET_REFERENCES, dataSetObject);
		themeList = loadArray(PROJECT_OPEN_DATA_DATASET_THEME, dataSetObject);	
		
		//load objects Publisher, Contact and Distributions
		loadDistributionList(dataSetObject.get(Distribution.PROJECT_OPEN_DATA_DISTRIBUTION));
		
		try{
			publisher.loadDatasetFromPOD_JSON((JSONObject)dataSetObject.get(Publisher.PROJECT_OPEN_DATA_PUBLISHER));
		}
		catch (PublisherException e)
		{
			dsEx.addError(e.toString());
		}
		try{
			contactPoint.loadDatasetFromPOD_JSON((JSONObject)dataSetObject.get(Contact.PROJECT_OPEN_DATA_CONTACT_POINT));
		}
		catch (ContactException e)
		{
			dsEx.addError(e.toString());
		}
		
		if (!validateDataset() || dsEx.exceptionSize() > 0)
		{
			dsEx.setTitle(title);
			dsEx.setUniqueIdentifier(uniqueIdentifier);
			throw (dsEx);
		}
	}
	
	/**
	 * This method will determine if the instance is List<JSONObject> or an Array List
	 * Fromt here it will call the approriate function.
	 * 
	 * This might benefit from using generics or converting one list to the other
	 * @param distributionObject
	 */
	private void loadDistributionList(Object distributionObject)
	{
		if (distributionObject == null)
		{
			throw new NullPointerException("distributionObject cannot be null");
		}
		JSONArray distributionArray = null;
		if (distributionObject instanceof ArrayList)
		{
			//convert to JSONArray
			ArrayList<JSONObject> distributionArrayList = (ArrayList<JSONObject>) distributionObject;
			distributionArray = new JSONArray();
			for (int i = 0; i < distributionArrayList.size(); i++)
			{
				distributionArray.add((JSONObject) distributionArrayList.get(i));
			}			
		}
		else if (distributionObject instanceof JSONArray)
		{
			distributionArray = (JSONArray)distributionObject;
		}
		for (int i=0; i< distributionArray.size(); i++)
		{
			loadDistribution ((JSONObject)distributionArray.get(i));
		}
	}

	/**
	 * This method takes in a JSON object and loads a distribution object.  It then
	 * adds the distribution object to this objects distributionList.
	 * @param distributionObject
	 */
	private void loadDistribution(JSONObject distributionObject)
	{
		Distribution distribution = new Distribution();
		try{
			distribution.loadFromProjectOpenDataJSON(distributionObject);
		}
		catch (DistributionException e)
		{
			dsEx.addError(e.toString());
		}
		distributionList.add(distribution);
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
		
		//There are instances when this is not a JSONList, specifically when directly
		//loading from a Dataset object
		if (dataSetObject.get(key) instanceof ArrayList)
		{
			return (ArrayList<String>)dataSetObject.get(key);
		}
		
		JSONArray jsonArray = (JSONArray) dataSetObject.get(key);
		List<String> returnList = new ArrayList<String>();
		if (jsonArray != null)
		{
			for (int i=0; i<jsonArray.size(); i++)
			{
				value = (String) jsonArray.get(i);
				returnList.add(value.trim());
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
	
	private void setIssued(String issued)
	{
		if (issued != null)
		{
			try{
				this.issued = Utils.convertISOStringToDate(issued);
			}
			catch(ParseException e)
			{
				dsEx.addError("Issued field has invalid ISO Date" + e);
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
		for (String s: languageList)
			s.trim();
		this.languageList = languageList;
	}
	
	public void setLanguageList(String languageListString){
		this.languageList.add(languageListString.trim());
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
		if (accrualPeriodicity != null)
		{
			this.accrualPeriodicity = Utils.toISO8661(accrualPeriodicity);
		}
	}

	public URL getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(URL landingPage) {
		this.landingPage = landingPage;
	}
	
	private void setLandingPage(String landingPage){
		if (landingPage != null && !landingPage.isEmpty())
		{
			try 
			{
				this.landingPage = new URL(landingPage);
			}
			catch(MalformedURLException e)
			{
				dsEx.addError("Landing Page is invalid URL." + e.toString());
			}
		}
	}
	
	public void setLandingPage(Object landingPage) {
		if (landingPage instanceof String)
		{
			setLandingPage((String)landingPage);
		}
		else if (landingPage instanceof URL)
		{
			setLandingPage((URL)landingPage);
		}
	}

	public List<String> getBureauCodeList() {
		return bureauCodeList;
	}

	/**
	 * bureau code must be in the following format 000:00 or NNN:NN.  This validates.
	 * This method is used from CKAN string.
	 * @param bureauCode
	 */
	public void setBureauCodeList(String bureauCode) throws ParseException{
		if (!bureauCodeList.contains(bureauCode))
		{
			if (Pattern.matches("\\d{3}:\\d{2}", bureauCode)){
				bureauCodeList.add(bureauCode);
			}
			else
			{
				throw new ParseException("Bureau Code must be \\d{3}:\\d{2}: " + bureauCode, 2);
			}
		}
	}
	
	/**
	 * This method is called from POD 1.1 import.  it calls setBureauCodeList with string
	 * for additional validatins
	 * @param bureauArray
	 */
	public void setBureauCodeList(JSONArray bureauArray) throws ParseException
	{
		if (bureauArray == null)
		{
			throw new NullPointerException("bureau array must have value to set a bureau list");
		}
		
		for (int i = 0; i < bureauArray.size(); i++)
		{
			setBureauCodeList((String) bureauArray.get(i));
		}
	}
	
	public void setBureauCodeList(ArrayList<String> bureauCodeList)
	{
		this.bureauCodeList = bureauCodeList;
	}
	
	public void setBureauCodeList(Object bureauCodeList)
	{
		if (bureauCodeList == null)
		{
			throw new NullPointerException("bureauCodeList cannot be null");
		}
		if( bureauCodeList instanceof ArrayList)
		{
			setBureauCodeList((ArrayList<String>)bureauCodeList);
		}
		else if (bureauCodeList instanceof JSONArray)
		{
			final JSONArray bureauArray = (JSONArray)bureauCodeList;
			for (int i = 0; i < bureauArray.size(); i++)
			{
				try
				{
					setBureauCodeList((String) bureauArray.get(i));
				}
				catch(ParseException e)
				{
					dsEx.addError(e.toString());
				}
			}
		}
	}
	
	public String getBureauName() {
		return bureauName;
	}

	public void setBureauName(String bureauName) {
		this.bureauName = bureauName;
	}

	public String getBureauAbbreviation() {
		return bureauAbbreviation;
	}

	public void setBureauAbbreviation(String bureauAbbreviation) {
		this.bureauAbbreviation = bureauAbbreviation;
	}

	public List<String> getProgramCodeList() {
		return programCodeList;
	}

	/**
	 * This is called from CKAN import
	 * @param programCode
	 * @throws DatasetException
	 */
	public void setProgramCodeList(String programCode) throws ParseException
	{
		if (!programCodeList.contains(programCode))	
		{
			if (Pattern.matches("\\d{3}:\\d{3}", programCode))
			{
				programCodeList.add(programCode);
			}
			else
			{
				//dsEx.addError("Program Code must be \\d{3}:\\d{3}: " + programCode);
				throw new ParseException("Program Code must be \\d{3}:\\d{3}: " + programCode, 3);
			}
		}
	}
	
	public void setProgramCodeList(JSONArray programArray) throws ParseException
	{
		if (programArray == null)
		{
			throw new NullPointerException("bureau array must have value to set a program list");
		}
		
		for (int i = 0; i < programArray.size(); i++)
		{
			setProgramCodeList((String) programArray.get(i));
		}
	}
	
	public void setProgramCodeList(Object programCodeList)
	{
		if (programCodeList == null)
		{
			throw new NullPointerException("programCodeList cannot be null");
		}
		if( programCodeList instanceof ArrayList)
		{
			setProgramCodeList((ArrayList<String>)programCodeList);
		}
		else if (programCodeList instanceof JSONArray)
		{
			final JSONArray programArray = (JSONArray)programCodeList;
			for (int i = 0; i < programArray.size(); i++)
			{
				try
				{
					setProgramCodeList((String) programArray.get(i));
				}
				catch(ParseException e)
				{
					dsEx.addError(e.toString());
				}
			}
		}
	}
	public void setProgramCodeList(ArrayList<String> programCodeList)
	{
		this.programCodeList = programCodeList;
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
			if (accessLevel.equals(AccessLevel.PUBLIC.toString()) || accessLevel.equals(AccessLevel.PRIVATE.toString()) || accessLevel.equals(AccessLevel.RESTRICTED.toString()))
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
	
	//handle any case
	private void setDataQuality(Object dataQuality)
	{
		if (dataQuality instanceof String)
		{
			setDataQuality ((String) dataQuality);
		}
		else if (dataQuality instanceof Boolean)
		{
			setDataQuality ((Boolean) dataQuality);
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
		if (license != null)
		{
			this.license = license.trim();
		}
	}
	
	public String getIsPartOf() {
		return isPartOf;
	}

	public void setIsPartOf(String isPartOf) {
		this.isPartOf = isPartOf;
	}
	
	public Date getMetadataModifiedDate() {
		return metadataModifiedDate;
	}

	public void setMetadataModifiedDate(Date metadataModifiedDate) {
		this.metadataModifiedDate = metadataModifiedDate;
	}
	
	public void setMetadataModifiedDate(String metadataModifiedString){
		if (metadataModifiedString != null)
		{
			try{
				this.metadataModifiedDate= Utils.convertISOStringToDate(metadataModifiedString);
			}
			catch(ParseException e)
			{
				dsEx.addError("Issued field has invalid ISO Date" + e);
			}
		}
	}

	public Date getMetadataCreatedDate() {
		return metadataCreatedDate;
	}

	public void setMetadataCreatedDate(Date metadataCreatedDate) {
		this.metadataCreatedDate = metadataCreatedDate;
	}
	
	public void setMetadataCreatedDate(String metadataCreatedDateString){
		if (metadataCreatedDateString != null)
		{
			try{
				this.metadataCreatedDate = Utils.convertISOStringToDate(metadataCreatedDateString);
			}
			catch(ParseException e)
			{
				dsEx.addError("Issued field has invalid ISO Date" + e);
			}
		}
	}

	public Date getRevisionTimeStamp() {
		return revisionTimeStamp;
	}

	public void setRevisionTimeStamp(Date revisionTimeStamp) {
		this.revisionTimeStamp = revisionTimeStamp;
	}
	
	//TODO: Add generic method for setting date/string
	public void setRevisionTimeStamp(String revisionTimeStampString){
		if (revisionTimeStampString != null)
		{
			try{
				this.revisionTimeStamp = Utils.convertISOStringToDate(revisionTimeStampString);
			}
			catch(ParseException e){
				dsEx.addError("Issued field has invalid ISO Date" + e);
			}
		}
	}
	
	public Element toLegacyXML(Document doc)
	{
		Element datasetElement = null;
		datasetElement = doc.createElement("dataset");
		datasetElement.appendChild(fieldToLegacyXML("title", title, doc));
		datasetElement.appendChild(fieldToLegacyXML("description", description, doc));
		datasetElement.appendChild(fieldToLegacyXML("accessLevel", accessLevel, doc));
		if (accrualPeriodicity != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("accrualPeriodicity", accrualPeriodicity, doc));
		}
		if (conformsTo != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("conformsTo", conformsTo, doc));
		}
		if (dataQuality != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("dataQuality", dataQuality.toString(), doc));
		}
		if (describedBy != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("describedBy", describedBy, doc));
		}
		if (describedByType != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("describedByType", describedByType, doc));
		}
		if (isPartOf != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("isPartOf", isPartOf, doc));
		}
		if (issued != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("issued", Utils.convertDateToISOString(issued), doc));
		}
		if (landingPage != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("landingPage", landingPage.toString(), doc));
		}
		if (license != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("license", license.toString(), doc));
		}
		if (modified != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("modified", Utils.convertDateToISOString(modified), doc));
		}
		if (primaryITInvestmentUII != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("primaryITInvestmentUII", primaryITInvestmentUII , doc));
		}
		if (rights != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("rights", rights, doc));
		}
		if (spatial != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("spatial", spatial, doc));
		}
		if (systemOfRecords != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("systemOfRecords", systemOfRecords , doc));
		}
		if (temporal != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("temporal", temporal, doc));
		}
		if (uniqueIdentifier != null)
		{
			datasetElement.appendChild(fieldToLegacyXML("uniqueIdentifier", uniqueIdentifier, doc));
		}
		
		//TODO: dataQuality, 
		
		datasetElement.appendChild(publisher.toLegacyXML(doc));
		datasetElement.appendChild(contactPoint.toLegacyXML(doc));
		
		if (bureauCodeList.size() > 0)
		{
			datasetElement.appendChild(fieldToLegacyXML("bureauCode", Utils.listToCSV(bureauCodeList), doc));
		}
		if (programCodeList.size() > 0)
		{
			datasetElement.appendChild(fieldToLegacyXML("programCode", Utils.listToCSV(programCodeList), doc));
		}
		if (referenceList.size() > 0)
		{
			datasetElement.appendChild(fieldToLegacyXML("references", Utils.listToCSV(referenceList), doc));
		}
		if (themeList.size() > 0)
		{
			datasetElement.appendChild(fieldToLegacyXML("categories", Utils.listToCSV(themeList), doc));
		}
		
		//TODO: Check size >0
		for (String tag: keywordList)
		{
			datasetElement.appendChild(fieldToLegacyXML("keyword", tag, doc));
		}
		
		//TODO: Check size < 0
		for (Distribution dist: distributionList)
		{
			datasetElement.appendChild(dist.toLegacyXML(doc));
		}
			
		return datasetElement;
	}
	
	
	
	
	private Element fieldToLegacyXML(String elementName, String elementValue, Document doc)
	{
		Element fieldElement = null;
		if (elementValue == null)
		{
			return fieldElement;
		}
	
		fieldElement = doc.createElement(elementName);
		fieldElement.setTextContent(elementValue);
		return fieldElement;
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
		if (keywordList.size() == 0)
		{
			dsEx.addError("At least one tag is required.");
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
		//can't check distribution size unless access is filled so this is a case you 
		//need to run the program twice to find error.
		else if (distributionList.size() == 0 && !accessLevel.equals(AccessLevel.PRIVATE.toString()))
		{
			dsEx.addError("At least one distribution is required when dataset is public or restricted.");
			validIndicator = false;
		}
		for (int i=0; i< distributionList.size(); i++)
		{
			final Distribution d = distributionList.get(i);
			
			try{
				if (accessLevel == null)
				{
					System.out.println(title);
				}
				else if (accessLevel.equals(AccessLevel.PUBLIC.toString()) || accessLevel.equals(AccessLevel.PRIVATE.toString()))
				{
					d.validatePublicDistribution();
				}
			}
			catch(DistributionException e)
			{
				dsEx.addError(e.toString());
			}
		}
		if (bureauCodeList.size() == 0)
		{
			dsEx.addError("Bureau Code is required.");
			validIndicator = false;
		}
		if (programCodeList.size() ==0)
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
	
	/**
	 * Sorts list first by bureauName and then by title.  if bureau name is null compare by bureauCode
	 */
	@Override
	public int compareTo(Dataset other) 
	{
		if (other==null)
		{
			throw new NullPointerException("compareTo other cannot be null in Dataset");
		}
		int agencyCompare = 0;
		int titleCompare = 0;
		if (bureauName != null)
		{
			agencyCompare = this.bureauName.compareTo(other.bureauName);
		}
		else
		{
			agencyCompare = this.bureauCodeList.get(0).compareTo(other.bureauCodeList.get(0));
		}
		titleCompare = agencyCompare == 0 ? this.title.compareTo(other.title) : agencyCompare;
	
		return titleCompare;
    }
}
