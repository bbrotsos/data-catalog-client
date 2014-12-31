package gov.usda.DataCatalogClient;

import java.util.ArrayList;

public class DistributionException extends Exception {
	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444157;
	
	public DistributionException() {
    }
	
	public DistributionException(String message) {
        messages.add( message );
    }
	
	public DistributionException(String message, Throwable throwable) {
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
    
    public int exceptionSize()
    {
    	return messages.size();
    }
}
