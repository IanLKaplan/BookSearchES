/** \file
 * 
 * Jul 11, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import java.util.ArrayList;

import booksearch_es.model.BookInfo;
import booksearch_es.model.GenreEnum;

public final class BookInfoUtil {

    public static BookInfo buildBookInfo( String title, 
            String author, 
            GenreEnum genre, 
            String publisher, 
            String year, 
            String price ) {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setGenre(genre.getName());
        bookInfo.setPublisher(publisher);
        bookInfo.setYear(year);
        bookInfo.setPrice(price);
        return bookInfo;
    }
    
    /**
     * @return a List of books by the author William Gibson
     */
    public static ArrayList<BookInfo> gibsonBooks() {
        ArrayList<BookInfo> bookList = new ArrayList<BookInfo>();
        BookInfo testBook = BookInfoUtil.buildBookInfo("Neuromancer",
                                                                 "William Gibson",
                                                                 GenreEnum.SCIENCE_FICTION,
                                                                 "Ace",
                                                                 "1984",
                                                                 "14.77");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Count Zero",
                                                        "William Gibson",
                                                        GenreEnum.SCIENCE_FICTION,
                                                        "HarperCollins Publishers",
                                                        "1986",
                                                        "47.50");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Mona Lisa Overdrive",
                                                        "William Gibson",
                                                        GenreEnum.SCIENCE_FICTION,
                                                        "Bantam Books",
                                                        "1988",
                                                        "14.00");
        bookList.add(testBook);
        return bookList;
    }
    
    public static ArrayList<BookInfo> reynoldsBooks() {
        ArrayList<BookInfo> bookList = new ArrayList<BookInfo>();
        BookInfo testBook = BookInfoUtil.buildBookInfo("Revelation Space",
                                                       "Alastair Reynolds",
                                                       GenreEnum.SCIENCE_FICTION,
                                                       "Ace",
                                                       "2001",
                                                       "6.55");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Redemption Ark",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2003",
                                              "14.96");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Absolution Gap",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2004",
                                              "14.98");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Chasm City",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2002",
                                              "18.00");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("The Prefect",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2009",
                                              "34.95");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Revenger",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Gollancz",
                                              "2016",
                                              "23.29");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Blue Remembered Earth",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Gollancz",
                                              "2012",
                                              "16.95");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("On a Steel Breeze",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Gollancz",
                                              "2014",
                                              "8.91");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Poseidon's Wake",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Gollancz",
                                              "2016",
                                              "27.00");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("House of Suns",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2009",
                                              "59.97");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Century Rain",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2005",
                                              "34.23");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Pushing Ice",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2006",
                                              "24.90");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Terminal World",
                                              "Alastair Reynolds",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Ace",
                                              "2010",
                                              "11.69");
        bookList.add(testBook);
        return bookList;
    }
    
    /**
     * 
     * @return a List of books for the book database
     */
    public static ArrayList<BookInfo> buildBookList() {
        ArrayList<BookInfo> bookList = new ArrayList<BookInfo>();
        bookList.addAll( gibsonBooks() );
        bookList.addAll( reynoldsBooks() );
        BookInfo testBook = BookInfoUtil.buildBookInfo("Luna: New Moon",
                                                        "Iain McDonald",
                                                        GenreEnum.SCIENCE_FICTION,
                                                        "Tor Books",
                                                        "2015",
                                                        "16.23");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("The Hydrogen Sonata",
                                              "Iain M. Banks",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Orbit Books",
                                              "2012",
                                              "17.29");
        bookList.add(testBook);
        testBook = BookInfoUtil.buildBookInfo("Jennifer Government",
                                              "Max Berry",
                                              GenreEnum.SCIENCE_FICTION,
                                              "Doubleday",
                                              "2003",
                                              "19.95");
        bookList.add(testBook);                
        return bookList;
    }
}
