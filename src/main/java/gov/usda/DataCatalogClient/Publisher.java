package gov.usda.DataCatalogClient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONObject;
/**
 * The Publisher class is based on Project Open Data metadata specification 1.1.  Details can
 * be found here https://project-open-data.cio.gov/v1.1/schema/#publisher
 * 
 * @author bbrotsos
 *
 */
public class Publisher {
	public final static String PROJECT_OPEN_DATA_PUBLISHER = "publisher";
	public final static String PROJECT_OPEN_DATA_PUBLISHER_NAME = "name";
	public final static String PROJECT_OPEN_DATA_PUBLISHER_SUBORGANIZATION = "subOrganizationOf";
	public final static String PROJECT_OPEN_DATA_PUBLISHER_TYPE = "@type";
	
	public final static String CKAN_PUBLISHER_NAME = "publisher";
	public final static String CKAN_PUBLISHER_SUBORGANIZATION_NAME = "publisher_1";

	private String name;
	private Publisher subOrganization;
	private String type;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Publisher getSubOrganization() {
		return subOrganization;
	}
	public void setSubOrganization(Publisher subOrganization) {
		this.subOrganization = subOrganization;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject publisherMap = new JSONObject();
		publisherMap.put(PROJECT_OPEN_DATA_PUBLISHER_NAME, name);
		if (subOrganization != null)
		{
			publisherMap.put(PROJECT_OPEN_DATA_PUBLISHER_SUBORGANIZATION, subOrganization.toProjectOpenDataJSON());
		}
		publisherMap.put(PROJECT_OPEN_DATA_PUBLISHER_TYPE, type);
		
		return publisherMap;
	}
	
	public void loadDatasetFromPOD_JSON(JSONObject publisherProjectOpenDataJSON) throws PublisherException
	{
		if (publisherProjectOpenDataJSON == null)
		{
			throw new PublisherException("Publisher cannot be null");
		}
		setName((String) publisherProjectOpenDataJSON.get(PROJECT_OPEN_DATA_PUBLISHER_NAME));
		JSONObject subOrganizationJSON = new JSONObject();
		subOrganizationJSON = (JSONObject) publisherProjectOpenDataJSON.get(PROJECT_OPEN_DATA_PUBLISHER_SUBORGANIZATION);
		if (subOrganizationJSON != null)
		{
			subOrganization = new Publisher();
			subOrganization.loadDatasetFromPOD_JSON(subOrganizationJSON);
		}
		setType((String) publisherProjectOpenDataJSON.get(PROJECT_OPEN_DATA_PUBLISHER_TYPE));
		
		validatePublisher();
		
	}
	
	public Boolean validatePublisher() throws PublisherException
	{
		Boolean validIndicator = true;
		if (name == null)
		{
			validIndicator = false;
			throw (new PublisherException("Publisher invalid: Name is required"));
		}
		return validIndicator;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Publisher))
		{
			return false;
		}
		Publisher publisher_other = (Publisher)o;
		
		return new EqualsBuilder()
         .append(name, publisher_other.name)
         .append(subOrganization, publisher_other.subOrganization)
         .append(type, publisher_other.type)
         .isEquals();
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(19, 27).
				append(name).
				append(subOrganization).
				append(type).
				toHashCode();
	}
	@Override
	public String toString() {
		return "Publisher [type=" + type + ", name=" + name
				+ ", subOrganization=" + subOrganization + "]";
	}
	
	
}
