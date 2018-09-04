/** \file
 * 
 * Jul 31, 2018
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

public class GetBooksTest {
    private final static String INDEX_NAME = "match_all_query_test";
    private final static String type = null;
    private final static String suffix = null;
    
    private static ArrayList<BookInfo> bookList = BookInfoUtil.buildBookList();


    @Before
    public void setUp() throws Exception {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);

        String rslt = HttpService.putDocument(INDEX_NAME, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(this.getClass().getName() + ": " + INDEX_NAME + " index created");
                // Add some BookInfo data to the index/type
                ElasticsearchService elasticService = new ElasticsearchService();
                for (BookInfo book : bookList) {
                    elasticService.addDocument(INDEX_NAME, Mapping.TYPE_NAME, book);
                }
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
    public void testGetBooks() {
        BookSearchService service = new BookSearchService();
        List<BookInfo> bookListRslt = service.getBooks(INDEX_NAME);
        if (bookList.size() == bookListRslt.size()) {
            assertTrue("Result does not match the initial data set", bookList.containsAll(bookListRslt) );
        } else {
            fail("The size of the result does not match the size of the initial data set");
        }
    }

}
