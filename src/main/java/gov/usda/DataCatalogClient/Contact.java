package gov.usda.DataCatalogClient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * The Contact class is based on Project Open Data Metadata specification 1.1.
 * More details can be found here: https://project-open-data.cio.gov/v1.1/schema/#contactPoint
 * 
 * If type is null it will be set to card:Contact
 * @author bbrotsos
 *
 */
public class Contact {

	public final static String PROJECT_OPEN_DATA_CONTACT_POINT = "contactPoint";
	public final static String PROJECT_OPEN_DATA_CONTACT_POINT_FULL_NAME = "fn";
	public final static String PROJECT_OPEN_DATA_CONTACT_POINT_EMAIL_ADDRESS = "hasEmail";
	public final static String PROJECT_OPEN_DATA_CONTACT_POINT_TYPE = "@type";

	public final static String CKAN_CONTACT_EMAIL_ADDRESS = "contact_email";
	public final static String CKAN_CONTACT_FULL_NAME = "contact_name";

	private String emailAddress;
	private String fullName;
	private String type;

	private ContactException contactException = new ContactException();
	
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
	
	/**
	 * Sometimes email address will come in as mailto:  This is removed
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		if (emailAddress != null)
		{
			//remove the 'mailto' in mailto:jane.doe@us.gov if it exists
			emailAddress = removeMailto(emailAddress);
			EmailValidator emailValidator = EmailValidator.getInstance();
			if (!emailValidator.isValid(emailAddress))
			{
				contactException.addError("Email Address: " + emailAddress + " is not a valid address.");
			}
		}
		this.emailAddress = emailAddress;
	}
	
	/**
	 * Sometimes email address will come in as mailto:  This is removed
	 * @param emailAddress
	 */
	private String removeMailto(String emailAddress)
	{
		//remove the 'mailto' in mailto:jane.doe@us.gov if it exists
		if (emailAddress.contains(":"))
		{
			final String[] parseEmailAddress = emailAddress.split(":");
			emailAddress = parseEmailAddress[1];
		}
		return emailAddress;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject contactPointMap = new JSONObject();
		contactPointMap.put(PROJECT_OPEN_DATA_CONTACT_POINT_EMAIL_ADDRESS, "mailto:" + getEmailAddress());
		contactPointMap.put(PROJECT_OPEN_DATA_CONTACT_POINT_FULL_NAME , getFullName());
		contactPointMap.put(PROJECT_OPEN_DATA_CONTACT_POINT_TYPE, type);

		return contactPointMap;
	}
	
	/**
	 * This method takes in a Project Open Data contactPoint json file.
	 * 
	 * { "@type": "vcard:Contact", "fn": "Jane Doe", "hasEmail":
	 * "mailto:jane.doe@us.gov" }
	 * 
	 * @param contactProjectOpenDataJSON
	 * @throws ContactException
	 */
	public void loadDatasetFromPOD_JSON(JSONObject contactProjectOpenDataJSON)
			throws ContactException {
		if (contactProjectOpenDataJSON == null) {
			throw new ContactException("contact cannot be empty");
		}
		setEmailAddress((String) contactProjectOpenDataJSON
				.get(PROJECT_OPEN_DATA_CONTACT_POINT_EMAIL_ADDRESS));
		setFullName((String) contactProjectOpenDataJSON
				.get(PROJECT_OPEN_DATA_CONTACT_POINT_FULL_NAME));
		setType((String) contactProjectOpenDataJSON
				.get(PROJECT_OPEN_DATA_CONTACT_POINT_TYPE));
		validateContact();
	}
	
	/**
	 * Business rules for valid Contact.  fullName and email address are required.
	 * @return
	 * @throws ContactException
	 */
	public Boolean validateContact() throws ContactException
	{
		Boolean validIndicator = true;
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

	public Element toLegacyXML(Document doc)
	{
			
		Element contactElement = doc.createElement("contactPoint");
		if (emailAddress != null)
		{
			contactElement.appendChild(fieldToLegacyXML("emailAddress", emailAddress, doc));
		}
		if (fullName != null)
		{
			contactElement.appendChild(fieldToLegacyXML("fn", fullName, doc));
		}
		if (type != null)
		{
			contactElement.appendChild(fieldToLegacyXML("type", type, doc));
		}
		
		return contactElement;
	}
	
	//TODO: Consolidate this method with dataset level
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
	@Override
	public String toString() {
		return "Contact [type=" + type + ", fullName=" + fullName
				+ ", emailAddress=" + emailAddress + "]";
	}
	
	
	
}
