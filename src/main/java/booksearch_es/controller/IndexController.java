/** \file
 * 
 * Mar 16, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import booksearch_es.json.JSONUtils;
import booksearch_es.service.BookSearchService;

/**
 * <h4>
 * IndexController
 * </h4>
 * <p>
 * The controller for the main application index page.
 * </p>
 * <p>
 * This controller displays aggregates for genre and publisher. This is a demo application and the primary
 * motivation for handling these aggregates in this way this demonstrates "returning" data structures to
 * the view via the model, instead of as a redirect flash attribute.
 * </p>
 * <p>
 * May 22, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
@Controller
public class IndexController extends BookControllerBase {
    private BookSearchService mBookService = new BookSearchService();
    private final static String GENRE_AGG ="genreAgg";
    private final static String PUBLISHER_AGG = "publisherAgg";

    @GetMapping("/")
    public ModelAndView index(ModelMap model) {
        List<JSONUtils.BucketAggregation> genreAgg = mBookService.bucketAggregation("GenreAgg", "genre");
        if (genreAgg != null && genreAgg.size() > 0) {
            // Pass the genreAgg to the view via the model map.
            model.addAttribute(GENRE_AGG, genreAgg);
        }
        List<JSONUtils.BucketAggregation> publisherAgg = mBookService.bucketAggregation("PublisherAgg", "publisher.keyword");
        if (publisherAgg != null && publisherAgg.size() > 0) {
            model.addAttribute(PUBLISHER_AGG, publisherAgg);
        }
        return new ModelAndView("index", model);
    }
}
