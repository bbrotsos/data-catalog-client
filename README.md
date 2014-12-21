data-catalog-client
===================
WIP

The purpose of this project is to ingest CKAN and Project Open Data compliant JSON files and to output the same.  It will create, update, read and delete on CKAN.  

This is useful for automating content management systems with CKAN as well as generating reports.

It is written in Java because it is prevalent in many web development operations.

Example usage:

    	Catalog catalog = new Catalog();
    	Client odpClient = new Client();
 
    	catalog = odpClient.loadOrganizationsIntoCatalog();
    	catalog.toProjectOpenDataJSON("data.json");
    	
    	catalog.produceQuarterReport("quarter_report.doc");
    	catalog.produceBureauMetrics("bureau_metrics.csv");
