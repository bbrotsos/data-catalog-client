package gov.usda.DataCatalogClient;

import java.util.ArrayList;

public class ContactException extends Exception {
	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444157;
	
	public ContactException(String message) {
        messages.add( message );
    }
	
	public ContactException()
	{
		messages = new ArrayList<String>();
	}
	
	public ContactException(String message, Throwable throwable) {
		 super(message, throwable);
	}

    public void addError(String error) {
        messages.add(error);
    }

    public ArrayList<String> getErrorMessages() {
        return messages;
    }

    public String toString() {
        return "Contact Invalid: " + messages.toString();
    }
    
    public int exceptionSize()
    {
    	return messages.size();
    }
}
