/** \file
 * 
 * May 28, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import java.util.Comparator;

import booksearch_es.model.BookInfo;

public class CompareByPublisher implements Comparator<BookInfo> {

    /**
     * <p>
     * Compare:
     * <p>
     * <ol>
     *   <li>Genre</li>
     *   <li>Author</li>
     *   <li>Title</li>
     * </ol>
     * <p>
     * Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or 
     * greater than the second.
     * </p>
     */
    @Override
    public int compare(BookInfo o1, BookInfo o2) {
        String publisher1 = o1.getPublisher();
        String publisher2 = o2.getPublisher();
        int compareRslt = publisher1.compareToIgnoreCase(publisher2);
        if (compareRslt == 0) {
            compareRslt = o1.getAuthor().compareToIgnoreCase( o2.getAuthor());
            if (compareRslt == 0) {
                compareRslt = o1.getTitle().compareToIgnoreCase( o2.getTitle());
            }
        }
        return compareRslt;
    }

}
