package gov.usda.DataCatalogClient;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONObject;
/**
 * The Contact class is based on Project Open Data Metadata specification 1.1.
 * More details can be found here: https://project-open-data.cio.gov/v1.1/schema/#contactPoint
 * 
 * @author bbrotsos
 *
 */
public class Contact {

	private String type;
	private String fullName;
	private String emailAddress;
	
	private ContactException contactException;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public Map toProjectOpenDataJSON()
	{
		Map contactPointMap = new LinkedHashMap();
		contactPointMap.put("fn", getFullName());
		contactPointMap.put("hasEmail", getEmailAddress());
		return contactPointMap;
	}
	
	public void loadDatasetFromPOD_JSON(JSONObject contactProjectOpenDataJSON) throws ContactException
	{
		if (contactProjectOpenDataJSON== null)
		{
			throw new ContactException("contact cannot be empty");
		}
		type = (String) contactProjectOpenDataJSON.get("@type");
		fullName = (String) contactProjectOpenDataJSON.get("fn");
		emailAddress = (String) contactProjectOpenDataJSON.get("hasEmail");
	}
	
	public Boolean validateContact() throws ContactException
	{
		Boolean validIndicator = true;
		contactException = new ContactException();
		if (fullName == null)
		{
			contactException.addError("Full Name is required");
			validIndicator = false;
		}
		if (emailAddress == null)
		{
			contactException.addError("Email Address is required");
			validIndicator = false;
		}
		if (contactException.exceptionSize() > 0)
		{
			throw (contactException);
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
		if (!(o instanceof Contact))
		{
			return false;
		}
		Contact contact_other = (Contact)o;
		
		return new EqualsBuilder()
         .append(emailAddress, contact_other.emailAddress)
         .append(fullName, contact_other.fullName)
         .append(type,contact_other.type)
         .isEquals();
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(21, 27).
				append(emailAddress).
				append(fullName).
				append(type).
				toHashCode();
	}
	
}
