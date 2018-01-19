/**
 * The purpose of this project is to ingest CKAN and Project Open Data compliant JSON files
 *  and to output the same. It will create, update, read and delete on CKAN.
 * <p>
 * This is useful for automating content management systems with CKAN as well as generating reports.
 *
 * It is written in Java because it is prevalent in many web development operations. 
 * The original author is not a java engineer so there are many areas for optimization. 
 * This project did not use GSON or Jackson for marshalling because of the use of 
 * 3-4 different mappings into Dataset class made it difficult for testing. 
 * This may change this in the future.
 */
package gov.usda.DataCatalogClient;
