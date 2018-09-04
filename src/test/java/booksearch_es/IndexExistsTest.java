/** \file
 * 
 * Jul 9, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.Mapping;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

/**
 * <h4>
 * IndexExistsTest
 * </h4>
 * <p>
 * Test the index exists operation. This is needed in the start-up code to determine whether an index needs to be created.
 * </p>
 * <p>
 * Jul 9, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class IndexExistsTest {
    private final static String INDEX_NAME = "index_exists_test";
    private final String type = null;
    private final String suffix = null;
    
    /**
     * <p>
     * Create a temporary index/type for testing.
     * </p>
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);

        String rslt = HttpService.putDocument(INDEX_NAME, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(this.getClass().getName() + ": " + INDEX_NAME + " index created");
            } else {
                fail(this.getClass().getName() + ": " + "Failed to create index");
            }
        }
    }

    /**
     * <p>
     * Remove the temporary index used for testing.
     * </p>
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        String deleteIndexRslt = HttpService.deleteDocument(INDEX_NAME, type, suffix);
        if (deleteIndexRslt != null) {
            if (JSONUtils.isAcknowledged(deleteIndexRslt)) {
                System.out.println(this.getClass().getName() + ": " +  "Test index " + INDEX_NAME + " successfully removed");
            } else {
                fail(this.getClass().getName() + ": " + "Failed to remove the index " + INDEX_NAME );
            }
        }
    }

    /**
     * Test the index exists operation.
     */
    @Test
    public void test() {
        ElasticsearchService service = new ElasticsearchService();
        if (service.indexExists(INDEX_NAME)) {
            System.out.println(this.getClass().getName() + " test passed");
        } else {
            fail("Did not find the index " + INDEX_NAME);
        }
    }

}
