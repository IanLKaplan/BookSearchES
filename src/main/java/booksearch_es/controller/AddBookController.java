/** \file
 * 
 * Mar 28, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.controller;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import booksearch_es.model.BookInfo;

/**
 * <h4>
 * AddBookController
 * </h4>
 * <p>
 * Add a book to the database
 * </p>

 * May 22, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
@Controller
public class AddBookController extends BookControllerBase {
    
    private static final Log logger = LogFactory.getLog(AddBookController.class);

    @GetMapping( value="/addbook" )
    public String addbook( Model model ) {
        return "addbook";
    } // addBookForm
    

    @RequestMapping( value="/save-book", method = RequestMethod.POST)
    public String saveBook(@Valid BookInfo bookForm,  Errors errors, RedirectAttributes redirectAttributes) {
        if (! errors.hasErrors()) {
            if (bookForm != null) {
                bookForm.setAuthor_last_name();
                getBookSearchService().writeBookToDB(bookForm);
                redirectAttributes.addFlashAttribute("book_saved", "Saved the information for " + bookForm.getTitle());
            } else {
                logger.info("bookForm argument is null");
            }
        } else {
            redirectAttributes.addFlashAttribute("errors", errors);
        }
        return "redirect:addbook";
    }

}
