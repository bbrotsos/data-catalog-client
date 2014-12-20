package gov.usda.DataCatalogClient;

import java.util.Date;
import java.util.List;

public class Dataset {
	
	//documentation is at http://www.w3.org/TR/vocab-dcat/
	private String title;
	private String description;
	private Date issued;
	private Date modified;
	private String identifier;
	//These two are lists for Project Open Data compliance
	private List<String> keywordList;
	private List<String> languageList;
	private String contactPoint;
	private String temporal;
	private String spatial;
	private String accrualPeriodicity;
	private String landingPage;
	
	//federal government project open data extension documentation here: https://project-open-data.cio.gov/v1.1/schema/
	private String bureauCode;
	private String programCode;
	private String primaryITInvestmentUII;
	private String contactEmail;  //Project Open Data = hasEmail
	private String accessLevel;


	
	

}
