/** \file
 * 
 * Jul 10, 2018
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
import booksearch_es.model.BookInfo;
import booksearch_es.model.GenreEnum;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

/**
 * <h4>
 * AddDocumentTest
 * </h4>
 * <p>
 * Test adding a document to an Elasticsearch index.
 * </p>
 * <p>
 * The index used in this test is an index that is temporary constructed and then removed.
 * </p>
 * <p>
 * Jul 10, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class AddDocumentTest {

    private final static String INDEX_NAME = "add_document_test_index";
    private final static String type = null;
    private final static String suffix = null;
    private final static BookInfo testBook = BookInfoUtil.buildBookInfo("Neuromancer",
                                                                        "William Gibson",
                                                                        GenreEnum.SCIENCE_FICTION,
                                                                        "Ace",
                                                                        "1984",
                                                                        "14.77");
    private ElasticsearchService elasticService = new ElasticsearchService();
    
    /**
     * <p>
     * Create a temporary index/type for testing.
     * </p>
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        if (! elasticService.indexExists(INDEX_NAME)) {
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


    @Test
    public void testAddDocument() {
        if (elasticService.addDocument(INDEX_NAME, Mapping.TYPE_NAME, testBook)) {
            System.out.println(this.getClass().getName() + " Test passed: Document successfully added");
        } else {
            fail("Error adding document to the index " + INDEX_NAME );
        }
    }

}
