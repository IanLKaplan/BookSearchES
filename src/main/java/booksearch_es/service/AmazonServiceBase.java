/** \file
 * 
 * Jun 7, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public abstract class AmazonServiceBase {
    
    protected static AWSCredentials getCredentials(String AWS_ID, String AWS_KEY) {
        AWSCredentials credentials = new BasicAWSCredentials( AWS_ID, AWS_KEY );            
        return credentials;
    }
}
