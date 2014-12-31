package gov.usda.DataCatalogClient;

import java.util.LinkedHashMap;
import java.util.Map;

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
	private String type;
	private String name;
	private Publisher subOrganization;
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
	
	public Map toProjectOpenDataJSON()
	{
		Map publisherMap = new LinkedHashMap();
		publisherMap.put("@type", type);
		publisherMap.put("name", name);
		
		if (subOrganization != null)
		{
			publisherMap.put("suborganization", subOrganization.toProjectOpenDataJSON());
		}
		
		return publisherMap;
	}
	
	public void loadDatasetFromPOD_JSON(JSONObject publisherProjectOpenDataJSON) throws PublisherException
	{
		if (publisherProjectOpenDataJSON == null)
		{
			throw new PublisherException("Publisher cannot be null");
		}
		type = (String) publisherProjectOpenDataJSON.get("@type");
		name = (String) publisherProjectOpenDataJSON.get("name");
		JSONObject subOrganizationJSON = new JSONObject();
		subOrganizationJSON = (JSONObject) publisherProjectOpenDataJSON.get("subOrganizationOf");
		if (subOrganizationJSON != null)
		{
			subOrganization = new Publisher();
			subOrganization.loadDatasetFromPOD_JSON(subOrganizationJSON);
		}
		
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
	
}
