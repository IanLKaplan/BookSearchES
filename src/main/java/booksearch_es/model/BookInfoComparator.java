/** \file
 * 
 * May 28, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.model;

import java.util.Comparator;

public class BookInfoComparator implements Comparator<BookInfo> {

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
        GenreEnum e1 = GenreEnum.stringToEnum( o1.getGenre() );
        GenreEnum e2 = GenreEnum.stringToEnum( o2.getGenre() );
        int compareRslt = e1.compareTo(e2);
        if (compareRslt == 0) {
            compareRslt = o1.getAuthor().compareToIgnoreCase( o2.getAuthor());
            if (compareRslt == 0) {
                compareRslt = o1.getTitle().compareToIgnoreCase( o2.getTitle());
            }
        }
        return compareRslt;
    }

}
