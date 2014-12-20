package gov.usda.DataCatalogClient;

import java.util.ArrayList;

public class DatasetException extends Exception {

	private ArrayList<String> messages = new ArrayList<String>();
	private static final long serialVersionUID = 745444156;
	
	public DatasetException(String message) {
        messages.add( message );
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
