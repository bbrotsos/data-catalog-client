package gov.usda.DataCatalogClient;

import java.util.ArrayList;

/**
 * Catalog exception will give back a list of datasets that are in invalid and why.  It will also 
 * return JSON Parse well-formedness errors.
 * 
 * Catalog is passive, so it will continue to load all datasets even if some in the file have errors.
 * @author bbrotsos
 *
 */
public class CatalogException extends Exception {

	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444156;
	
	//These are title and identifier from Dataset class
	private String title;
	private String uniqueIdentifier;

	public CatalogException(String message) {
        messages.add( message );
    }
	
	public CatalogException() {
    }
	
	public CatalogException(String message, Throwable throwable) {
		 super(message, throwable);
	}

    public void addError(String error) {
        messages.add(error);
    }

    public ArrayList<String> getErrorMessages() {
        return messages;
    }

    public String toString() {
    	return "Catalog errors " + Utils.listToCSV(messages);
    }
    
    public int exceptionSize()
    {
    	return messages.size();
    }
}
