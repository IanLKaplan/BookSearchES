/** \file
 * 
 * Jun 23, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.Mapping;
import booksearch_es.service.HttpService;

public class TestIndexCreation {
    private final static String indexName = "test_index";
    private final static String type = null;
    private final static String suffix = null;
    
    /**
     * <p>
     * Remove the test index. Deleting an index results in:
     * </p>
     * <pre>
     * {
          "acknowledged": true
       }
     * </pre>
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        String deleteIndexRslt = HttpService.deleteDocument(indexName, type, suffix);
        if (deleteIndexRslt != null) {
            if (JSONUtils.isAcknowledged(deleteIndexRslt)) {
                System.out.println("Test index " + indexName + " successfully removed");
            } else {
                fail("Failed to remove the index " + indexName );
            }
        }
    }
    
    /**
     * <p>
     * Successfully creating the index results in the JSON
     * </p>
     * <pre>
     * {"acknowledged":true,"shards_acknowledged":true,"index":"booksearch"}
     * </pre>
     */
    @Test
    public void test() {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);
        String rslt = HttpService.putDocument(indexName, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(indexName + " index created");
                System.out.println("Test passed");
            } else {
                fail("Failed to create index");
            }
        } else {
            fail("Result for the PUT operation was null");
        }
    }

}
