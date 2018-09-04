/** \file
 * 
 * May 11, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.controller;

import booksearch_es.service.BookSearchService;

/**
 * <h4>
 * BookControllerBase
 * </h4>
 * <p>
 * This base class provides the BookTableService object to its subclasses. This allows a single BookTableService object
 * to be shared by all subclasses (which makes them all singleton classes).
 * </p>
 * <p>
 * May 22, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
abstract class BookControllerBase {
    private static BookSearchService bookSearchService = null; 
    protected static final String BOOK_LIST = "bookList";
    
    public BookControllerBase() {
        if (bookSearchService == null) {
            synchronized (BookControllerBase.class) {
                if (bookSearchService == null) {
                    bookSearchService = new BookSearchService();
                }
            }
        }
    }
    
    protected BookSearchService getBookSearchService() {
        return bookSearchService;
    }
    
}
