/** \file
 * 
 * Jun 7, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import booksearch_es.json.JSONUtils;
import booksearch_es.service.HttpService;
import booksearch_es.service.IElasticsearch;

/**
 * <h4>
 * ElasticsearchConnectTest
 * </h4>
 * <p>
 * Test that we can execute an HTTP GET on the AWS Elasticsearch Service URL.
 * </p>
 * <p>
 * Successfully accessing the Elasticsearch Service URL will return a JSON object. An example of a JSON return 
 * value is shown below.
 * </p>
 * <pre>
 * {
  "name" : "_8thz7y",
  "cluster_name" : "392059453262:booksearch",
  "cluster_uuid" : "8HJfSSl6Qf6CCeYC0XOFbg",
  "version" : {
    "number" : "6.2.2",
    "build_hash" : "10b1edd",
    "build_date" : "2018-02-28T15:42:08.616107Z",
    "build_snapshot" : false,
    "lucene_version" : "7.2.1",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
 * </pre>
 * Many of the values in this object are changeable. However, the domain name for the Elasticsearch instance (in this case booksearch)
 * should remain constant.
 * </p>
 * <p>
 * The test code searches for the domain name in the cluster name field. If the domain name is found (as expected)
 * the test passes.
 * </p>
 * <p>
 * Jun 13, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ElasticsearchConnectTest {
    private final static String CLUSTER_NAME_FIELD = "cluster_name";
    Logger log = Logger.getLogger( this.getClass().getName() );
    @Test
    public void test() {
        // Try an HTTP GET
        final String index = null;
        final String type = null;
        final String suffix = null;
        String responseStr = HttpService.getDocument(index, type, suffix);
        if (responseStr != null && responseStr.length() > 0) {
            try {
                System.out.println(responseStr);
                JsonNode jsonNode = JSONUtils.stringToJsonNode(responseStr);
                JsonNode clusterName = jsonNode.get( CLUSTER_NAME_FIELD);
                if (clusterName != null) {
                    String clusterNameStr = clusterName.textValue();
                    if (! clusterNameStr.endsWith(IElasticsearch.DOMAIN_NAME)) {
                        fail("Did not find the expected cluster name. Clustername returned is: " + clusterNameStr );
                    } else {
                        log.info("Test passed. Cluster name is: " + clusterNameStr );
                    }
                } else {
                    fail("Did not find " + CLUSTER_NAME_FIELD + " field");
                }
            } catch (IOException e) {
                fail("Error parsing JSON result from HTTP response: " + e.getLocalizedMessage() );
            }
        } else {
            fail("There was no response from the HTTP request");
        }
    }

}
