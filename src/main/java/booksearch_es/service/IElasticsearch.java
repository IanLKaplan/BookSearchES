/** \file
 * 
 * Jun 7, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;

import com.amazonaws.regions.Regions;

/**
 * <h4>
 * IElasticsearch
 * </h4>
 * <p>
 * Elasticsearch Interface containing various constants, including the search domain URL and the 
 * Elasticsearch ID and secret key.
 * </p>
 * Aug 1, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public interface IElasticsearch {
    public final static String DOMAIN_NAME = "booksearch"; // the elastic search domain
    // The AWS region for the Elasticsearch instance.
    // Frankfurt Germany
    public final static Regions region = Regions.EU_CENTRAL_1;
    // AWS Elasticsearch Service "service name"
    public final static String SERVICE_NAME = "es";
    // The book search application URL end-point
    public final static String ES_URL = "https://search-booksearch-s25q4g7czuusnjrjqlmwr35vxm.eu-central-1.es.amazonaws.com";
        // Elastic search full access (read, write and delete) IAM keys
    public final static String ES_ID = "Your Elasticsearch Service full access ID goes here";
    public final static String ES_KEY = "Your Elasticsearch Service full access secret key goes here";
}
