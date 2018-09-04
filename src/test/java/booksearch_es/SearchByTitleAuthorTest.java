/** \file
 * 
 * Jul 16, 2018
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
 * SearchByTitleAuthorTest
 * </h4>
 * <p>
 * A unit test for search by title word and author word.
 * </p>
 * <p>
 * Jul 25, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class SearchByTitleAuthorTest {
    private final static String INDEX_NAME = "search_by_title_author_test";
    private final static String type = null;
    private final static String suffix = null;

    @Before
    public void setUp() throws Exception {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);

        String rslt = HttpService.putDocument(INDEX_NAME, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(this.getClass().getName() + ": " + INDEX_NAME + " index created");
                // Add some BookInfo data to the index/type
                ArrayList<BookInfo> bookList = BookInfoUtil.buildBookList();
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
    public void testFindByTitleAuthor() {
        final String title_word = "neuromancer";
        final String author_name = "gibson";
        BookSearchService service = new BookSearchService();
        List<BookInfo> book = service.findBookByTitleAuthor(INDEX_NAME, title_word, author_name);
        assertTrue("The book in the title, author search was not found", 
                book.size() == 1 && book.get(0).getTitle().toLowerCase().equals(title_word));
    }

}
