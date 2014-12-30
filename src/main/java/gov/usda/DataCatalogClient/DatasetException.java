package gov.usda.DataCatalogClient;

import java.util.ArrayList;

public class DatasetException extends Exception {

	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444156;
	
	//These are title and identifier from Dataset class
	private String title;
	private String uniqueIdentifier;

	public DatasetException(String message) {
        messages.add( message );
    }
	
	public DatasetException() {
    }
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
	public DatasetException(String message, Throwable throwable) {
		 super(message, throwable);
	}

    public void addError(String error) {
        messages.add(error);
    }

    public ArrayList<String> getErrorMessages() {
        return messages;
    }

    public String toString() {
    	return "Dataset error for title: " + title + " " +Utils.listToCSV(messages);
    }
    
    public int exceptionSize()
    {
    	return messages.size();
    }
	
}
