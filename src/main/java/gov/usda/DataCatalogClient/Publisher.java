package gov.usda.DataCatalogClient;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

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
	
	public void loadDatasetFromPOD_JSON(JSONObject publisherProjectOpenDataJSON)
	{
		type = (String) publisherProjectOpenDataJSON.get("@type");
		name = (String) publisherProjectOpenDataJSON.get("name");
		JSONObject subOrganizationJSON = new JSONObject();
		subOrganizationJSON = (JSONObject) publisherProjectOpenDataJSON.get("subOrganization");
		if (subOrganizationJSON != null)
		{
			subOrganization.loadDatasetFromPOD_JSON(subOrganizationJSON);
		}
	}
	
	public Boolean validatePublisher()
	{
		Boolean validIndicator = true;
		
		if (name == null)
		{
			System.out.println("Publisher invalid: Name is required");
			validIndicator = false;
		}
		
		return validIndicator;
	}
	
}
