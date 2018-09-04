/** \file
 * 
 * Jul 10, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.Mapping;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

/**
 * <h4>
 * BookIndex
 * </h4>
 * <p>
 * This is a singleton class that creates an Elasticsearch index and type for the BookIndex (class) information.
 * </p>
 * <p>
 * This class makes sure that the Elasticsearch index will always exist.
 * </p>
 * <p>
 * Note that after Elasticsearch 6.0 only a single type is allowed per index, so the index and the type are, in effect,
 * one.
 * </p>
 * <p>
 * This class is designed to reduce the HTTP traffic to the Elasticsearch server. The indexExists() code is only called
 * if the singleton value is null. So this function should only be called once per instance execution.
 * </p>
 * <p>
 * Jul 10, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public final class BookIndex {
    public final static String BOOK_INDEX_NAME = "bookindex";
    private Logger logger = LoggerFactory.getLogger( BookIndex.class.getName() );
    @SuppressWarnings("unused")
    private static BookIndex singleton = null;
    
    public BookIndex() {
        if (singleton == null) {
            synchronized (BookIndex.class) { // make the singleton thread safe
                if (singleton == null) {
                    singleton = new BookIndex( BOOK_INDEX_NAME );
                }
            }
        }
    }
    
    private BookIndex( final String indexName ) {
        ElasticsearchService service = new ElasticsearchService();
        if (! service.indexExists(indexName)) {
            final String bookInfoMapping = Mapping.bookInfoMapping(false);
            final String type = null;
            final String suffix = null;
            String rslt = HttpService.putDocument(indexName, type, suffix, bookInfoMapping);
            if (! JSONUtils.isAcknowledged(rslt)) {
                logger.error("Critical error: could not create the Elasticsearch index " + BOOK_INDEX_NAME );
            }
        }
    }
}
