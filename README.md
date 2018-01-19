data-catalog-client
===================
WIP

The purpose of this project is to ingest CKAN and Project Open Data compliant JSON files and to output the same.  It will create, update, read and delete on CKAN.  

This is useful for automating content management systems with CKAN as well as generating reports.



It is written in Java because it is prevalent in many web development operations.  There are many areas for optimization.  This project did not use GSON or Jackson for marshalling because of the use of 3-4 different mappings into Dataset class made it difficult for testing.  This may change this in the future.

To download this type
'''
git clone https://github.com/bbrotsos/data-catalog-client
'''

or click "Clone or download" in github.

Example usage:

    	Catalog catalog = new Catalog();
    	Client odpClient = new Client();
 
    	catalog = odpClient.loadOrganizationsIntoCatalog();
    	
    	//create edi or pdl
    	catalog.toProjectOpenDataJSON("data.json");
    	
    	//Add new dataset to ckan
    	Dataset ds = new Dataset();
    	ds.setTitle("My New Title");
    	ds.setDescription("New dataset for CKAN");
    	
    	odpClient.createDataset(ds);
