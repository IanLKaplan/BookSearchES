/** \file
 * 
 * Aug 1, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.Mapping;
import booksearch_es.model.BookInfo;
import booksearch_es.service.BookSearchService;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

/**
 * <h4>
 * BulkLoadTest
 * </h4>
 * <p>
 * Test Elasticsearch bulk load.
 * </p>
 * Aug 1, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class BulkLoadTest {
    private final static String INDEX_NAME = "bulk_load_test";
    private final static String type = null;
    private final static String suffix = null;

    /**
     * <p>
     * Create an index, but don't add data, since this will be done by the bulk load operation.
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
    public void testBulkLoad() {
        final String type = Mapping.TYPE_NAME;
        ArrayList<BookInfo> bookList = BookInfoUtil.gibsonBooks();
        // you can't cast an ArrayList<BookInfo> to ArrayList<Object>, so convert by copying
        ArrayList<Object> objList = new ArrayList<Object>();
        objList.addAll(bookList);
        ElasticsearchService service = new ElasticsearchService();
        assertTrue("Bulk load test failed", service.bulkLoad(INDEX_NAME, type, objList) );
        // The load succeeded. Now make sure that the data is correct.
        BookSearchService bookSearch = new BookSearchService();
        List<BookInfo> bookListRslt = bookSearch.getBooks(INDEX_NAME);
        if (bookListRslt.size() == bookList.size()) {
            assertTrue("Bulk loaded data doesn't match test data", bookList.containsAll(bookListRslt) );
        } else {
            fail("The number of BookInfo objects retreived does not match the test data");
        }
    }

}
