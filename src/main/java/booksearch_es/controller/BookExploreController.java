/** \file
 * 
 * Aug 27, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import booksearch_es.model.BookInfo;

/**
 * <h4>
 * BookExploreController
 * </h4>
 * <p>
 * Handle queries from the index page which supports exploration of the books via
 * aggregates.
 * </p>
 * <p>
 * Aug 28, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
@Controller
public class BookExploreController extends BookControllerBase {

    @RequestMapping( value="/returnGenreBooks", method = RequestMethod.POST)
    public String returnBooksByGenre(@RequestParam("genre") String genre,
                                     RedirectAttributes redirect) {
        if (genre != null && genre.length() > 0) {
            List<BookInfo> bookList = getBookSearchService().findBooksByGenre(genre); 
            if (bookList != null && bookList.size() > 0) {
                redirect.addFlashAttribute(BOOK_LIST, bookList);
                redirect.addFlashAttribute("search", "genre");
                redirect.addFlashAttribute("name", genre);
            }
        }
       return "redirect:/";
    }
    
    @RequestMapping( value="/returnPublisherBooks", method = RequestMethod.POST)
    public String returnBooksByPublisher(@RequestParam("publisher") String publisher,
                                     RedirectAttributes redirect) {
        if (publisher != null && publisher.length() > 0) {
            List<BookInfo> bookList = getBookSearchService().findBooksByPublisherKeyword( publisher ); 
            if (bookList != null && bookList.size() > 0) {
                redirect.addFlashAttribute(BOOK_LIST, bookList);
                redirect.addFlashAttribute("search", "publisher");
                redirect.addFlashAttribute("name", publisher);
            }
        }
       return "redirect:/";
    }
                                     
}
