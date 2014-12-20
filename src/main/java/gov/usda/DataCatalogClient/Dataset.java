package gov.usda.DataCatalogClient;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Dataset {
	
	//documentation is at http://www.w3.org/TR/vocab-dcat/
	private String title;
	private String description;
	private Date issued;
	private Date modified;
	private String identifier;
	//These three are lists for Project Open Data compliance
	private List<String> keywordList;
	private List<String> languageList;
	private List<String> themeList;
	private String contactPoint;
	private String temporal;
	private String spatial;
	private String accrualPeriodicity;
	private URL landingPage;
	
	//federal government project open data extension documentation here: https://project-open-data.cio.gov/v1.1/schema/
	private String bureauCode;
	private String programCode;
	private String primaryITInvestmentUII;
	private String contactEmail;  //Project Open Data = hasEmail
	private String accessLevel;
	private String rights;
	private String systemOfRecords;
	private Boolean dataQuality;
	private List<String> referenceList;
	
	private List<Dataset> dataSetList;
	
	private DatasetException dsEx;
	
	public Dataset()
	{
		dsEx = new DatasetException("Dataset Error");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public List<String> getThemeList() {
		return themeList;
	}

	public void setThemeList(List<String> themeList) {
		this.themeList = themeList;
	}

	public String getContactPoint() {
		return contactPoint;
	}

	public void setContactPoint(String contactPoint) {
		this.contactPoint = contactPoint;
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

	public String getBureauCode() {
		return bureauCode;
	}

	public void setBureauCode(String bureauCode) throws DatasetException{
		if (Pattern.matches("\\d{3}:\\d{2}", bureauCode)){
			this.bureauCode = bureauCode;
		}
		else{
			dsEx.addError("Bureau Code must be \\d{3}:\\d{2}: " + bureauCode);
			throw dsEx;
		}
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getPrimaryITInvestmentUII() {
		return primaryITInvestmentUII;
	}

	public void setPrimaryITInvestmentUII(String primaryITInvestmentUII) {
		this.primaryITInvestmentUII = primaryITInvestmentUII;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
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

	public List<String> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(List<String> referenceList) {
		this.referenceList = referenceList;
	}

	public List<Dataset> getDataSetList() {
		return dataSetList;
	}

	public void setDataSetList(List<Dataset> dataSetList) {
		this.dataSetList = dataSetList;
	}
	

}
