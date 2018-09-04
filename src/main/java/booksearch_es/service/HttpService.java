/** \file
 * 
 * Jun 27, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;

public class HttpService extends AmazonServiceBase implements IElasticsearch {
    
    private static Logger logger = LoggerFactory.getLogger( HttpService.class.getName() );

    /**
     * <p>
     * Build a an Apache HTTP ClosableHttpClient. This code is based on the sample code that can be found
     * here:
     * </p>
     * <pre>
     * https://github.com/awslabs/aws-request-signing-apache-interceptor/blob/master/examples/Sample.java
     * </pre>
     * 
     * @return a ClosableHttpClient object
     */
    protected static CloseableHttpClient signedClient() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName( SERVICE_NAME );
        signer.setRegionName( region.getName() );
        AWSCredentials credentials = getCredentials(ES_ID, ES_KEY);
        AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider( credentials );
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(SERVICE_NAME, signer, credProvider);
        return HttpClients.custom()
                .addInterceptorLast(interceptor)
                .build();
    }
    
    
    protected static String sendHTTPTransaction( HttpUriRequest request ) {
        String httpResult = null;
        CloseableHttpClient httpClient = signedClient();
        try {
            HttpResponse response = httpClient.execute(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            httpResult = IOUtils.toString(bufferedReader);
        } catch (IOException e) {
            logger.error("HTTP Result error: " + e.getLocalizedMessage());
        }  
        return httpResult;
    }
    
    private static String buildURL(final String index, final String type, final String suffix) {
        String url = IElasticsearch.ES_URL;
        if (index != null && index.length() > 0) {
            url = url + "/" + index;
        }
        if (type != null && type.length() > 0) {
            url = url + "/" + type;
        }
        if (suffix != null && suffix.length() > 0) {
            url = url + "/" + suffix;
        }
        return url;
    }
    
    
    /**
     * <p>
     * An HTTP HEAD operation
     * </p>
     *  
     * @param index
     * @return
     */
    public static int head(final String index) {
        int statusCode = -1;
        String url = IElasticsearch.ES_URL;
        if (index != null && index.length() > 0) {
            url = url + "/" + index;
        }
        CloseableHttpClient httpClient = signedClient();
        HttpHead head = new HttpHead( url );
        HttpResponse response;

        try {
            response = httpClient.execute(head);
            StatusLine statusLine = response.getStatusLine();
            statusCode = statusLine.getStatusCode();
        } catch (IOException e) {
            logger.error("Error in HEAD transaction: " + e.getLocalizedMessage());
        }
        return statusCode;
    }
    
    /**
     * 
     * @param index
     * @param type
     * @param suffix
     * @param jsonPayload
     * @return
     */
    public static String getDocument(final String index, final String type, final String suffix, final String jsonPayload) {
        String responseString = "";
        String url = buildURL(index, type, suffix);
        try {
            HttpGetWithEntity get = new HttpGetWithEntity( url );
            get.setHeader("Content-type", "application/json");
            StringEntity stringEntity = new StringEntity( jsonPayload, StandardCharsets.UTF_8);
            get.setEntity(stringEntity);
            responseString = sendHTTPTransaction( get );
        } catch (Exception e) {
            logger.error("HttpGet with entity failed: " + e.getLocalizedMessage());
        }
        return responseString;
    }
    
    /**
     * 
     * @param index
     * @param type
     * @param suffix
     * @return
     */
    public static String getDocument(final String index, final String type, final String suffix) {
        String url = buildURL(index, type, suffix);
        HttpGet get = new HttpGet( url );
        String responseString = sendHTTPTransaction( get );
        return responseString;
    }
    
    /**
     * 
     * @param index
     * @param type
     * @param suffix
     * @return
     */
    public static String deleteDocument(final String index, final String type, final String suffix) {
        String url = buildURL(index, type, suffix);
        HttpDelete delete = new HttpDelete( url );
        String responseString = sendHTTPTransaction( delete );
        return responseString;
    }
    
    /**
     * An HTTP PUT operation
     * 
     * @param index The index for the document
     * @param type The Elasticsearch type for the document
     * @param suffix A unique ID for the document or the URL suffix (for example, an id on a document PUT to add a doc to the database)
     * @param jsonPayload The JSON string to be added to the Elasticsearch index
     * @return a JSON result object
     */
    public static String putDocument(final String index, final String type, final String suffix, final String jsonPayload) {
        String url = buildURL(index, type, suffix);
        HttpPut put = new HttpPut( url );
        put.setHeader("Content-type", "application/json");
        StringEntity stringEntity = new StringEntity( jsonPayload, StandardCharsets.UTF_8);
        put.setEntity(stringEntity);
        String responseStr = sendHTTPTransaction( put );
        return responseStr;
    }
    
    /**
     * <p>
     * An HTTP POST where it is assumed that the caller builds the URL
     * </p>
     * @param url
     * @param jsonPayload
     * @return
     */
    public static String postDocument(final String url, String jsonPayload) {
        HttpPost post = new HttpPost( url );
        post.setHeader("Content-type", "application/json");
        StringEntity stringEntity = new StringEntity( jsonPayload, StandardCharsets.UTF_8);
        post.setEntity(stringEntity);
        String responseStr = sendHTTPTransaction( post );
        return responseStr;
    }
    
    /**
     * <p>
     * An HTTP Post operation
     * </p>
     * 
     * @param index
     * @param type
     * @param suffix
     * @param jsonPayload
     * @return
     */
    public static String postDocument(final String index, final String type, final String suffix, final String jsonPayload) {
        String url = buildURL(index, type, suffix);
        String responseStr = postDocument(url, jsonPayload ); 
        return responseStr;
    }
}
