/** \file
 * 
 * Jul 25, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.Mapping;
import booksearch_es.model.BookInfo;
import booksearch_es.service.BookSearchService;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

public class HasBookEntryTest {
    private final static String INDEX_NAME = "has_book_entry_test";
    private final static String type = null;
    private final static String suffix = null;
    private final static ArrayList<BookInfo> bookList = BookInfoUtil.gibsonBooks();
    
    @Before
    public void setUp() throws Exception {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);

        String rslt = HttpService.putDocument(INDEX_NAME, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(this.getClass().getName() + ": " + INDEX_NAME + " index created");
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
    public void testHasBookEntryStringBookInfo() {
        BookInfo bookEntry = null;
        for (BookInfo book : bookList) {
            if (book.getTitle().equals("Mona Lisa Overdrive")) {
                bookEntry = book;
                break;
            }
        }
        if (bookEntry != null) {
            BookSearchService bookService = new BookSearchService(); 
            assertTrue("The book \"" + bookEntry.getTitle() + "\" was not found", bookService.hasBookEntry(INDEX_NAME, bookEntry));
        } else {
            fail("Unit test code is wrong!");
        }
    }

}
