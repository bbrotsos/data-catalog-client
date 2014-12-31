package gov.usda.DataCatalogClient;

import java.util.ArrayList;

public class PublisherException extends Exception {
	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444157;
	
	public PublisherException(String message) {
        messages.add( message );
    }
	public PublisherException() {
        
    }
	
	public PublisherException(String message, Throwable throwable) {
		 super(message, throwable);
	}

    public void addError(String error) {
        messages.add(error);
    }

    public ArrayList<String> getErrorMessages() {
        return messages;
    }

    public String toString() {
        return messages.toString();
    }
}
