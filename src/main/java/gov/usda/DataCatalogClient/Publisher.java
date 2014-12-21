package gov.usda.DataCatalogClient;

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
	
	
	
	
}
