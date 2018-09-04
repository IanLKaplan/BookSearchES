/** \file
 * 
 * Jul 10, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import booksearch_es.model.BookIndex;
import booksearch_es.service.ElasticsearchService;

/**
 * <p>
 * BookIndexSingletonTest
 * </p>
 * <p>
 * Test that the BookIndex singleton properly creates the index for the book search application.
 * </p>
 * <p>
 * Jul 10, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class BookIndexSingletonTest {

    @Test
    public void test() {
        BookIndex index = new BookIndex();
        ElasticsearchService service = new ElasticsearchService();
        assertTrue("Serious error: the " + BookIndex.class.getCanonicalName() + " did not create the index " + BookIndex.BOOK_INDEX_NAME,
                   service.indexExists( BookIndex.BOOK_INDEX_NAME ));
    }

}
