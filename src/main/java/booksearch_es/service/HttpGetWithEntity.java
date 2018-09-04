/** \file
 * 
 * Jul 11, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;


import java.net.URI;
import java.net.URL;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * <h4>
 * HttpGetWithEntity
 * </h4>
 * <p>
 * A GET object with an associated entity.
 * </p>
 * <p>
 * HTTP Put operations have an associated entity. HTTP GET operations do not have an associated
 * entity because it is not defined in the standard. However, an entity is allowed in a GET operation but
 * it has no meaning as far as the standard is concerned. The REST application, apparently, can process the
 * GET entity. Elasticsearch does process get entities in "_search" operations.
 * </p>
 * <p>
 * Elasticsearch search operations include a query as a GET entity. For example:
 * </p>
 * <pre>
 * GET index/type/_search
 *'{
 *     "query" : {
 *        "match" : {
 *           "title" : "neuromancer"
 *        }
 *     }
 * }'
 * </pre>
 * <p>
 * Jul 11, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity(final String url) throws Exception {
        URL urlObj = new URL( url );
        URI uri = urlObj.toURI();
        setURI(uri);
    }
    
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
