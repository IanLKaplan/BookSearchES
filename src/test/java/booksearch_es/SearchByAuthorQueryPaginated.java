/** \file
 * 
 * Jul 11, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

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
 * SearchByAuthorQueryTest
 * </h4>
 * <p>
 * Test the "search by author" query with paginated results.
 * </p>
 * Jul 13, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class SearchByAuthorQueryPaginated {
    private final static String INDEX_NAME = "search_by_author_paginated";
    private final static String type = null;
    private final static String suffix = null;
    
    
    
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
    public void testFindBookByAuthor() {
        final String authorName = "reynolds";
        BookSearchService bookService = new BookSearchService(); 
        List<BookInfo> resultList = bookService.findBookByAuthor(INDEX_NAME, authorName);
        List<BookInfo> reynoldsBooks = BookInfoUtil.reynoldsBooks();
        if (resultList.size() == reynoldsBooks.size()) {
            if (! reynoldsBooks.containsAll(resultList)) {
                fail("The book lists do not match");
            }
        } else {
            fail("The number of results should have been " + reynoldsBooks.size() + ". It was " + resultList.size());
        }
    }

}
