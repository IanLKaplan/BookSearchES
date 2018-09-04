/** \file
 * 
 * Aug 1, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import booksearch_es.model.BookInfo;
import booksearch_es.model.BookInfoComparator;
import booksearch_es.service.BookSearchService;

/**
 * <h4>
 * CopyFromDynamoDB
 * </h4>
 * <p>
 * Copy data from a DynamoDB database into an Elasticsearch database.
 * </p>
 * <p>
 * An earlier version of the BookSearch application used the Amazon Web Services DynamoDB database to store the book
 * information. This earlier application was written to explore Spring boot and Spring MVC.
 * </p>
 * <p>
 * This version of the BookSearch application has been written to explore Elasticsearch. This class copies the data entered
 * in DynamoDB into the Elasticsearch database.
 * </p>
 * <p>
 * The Elasticsearch Service runs on an Amazon EC2 instance. This instance runs 24/7 as long as it's active. As a result, this 
 * service will incur AWS charges (which may be within the AWS free tier). 
 * </p>
 * <p>
 * Storing a relatively small amount of data in DynamoDB is cost free, since Amazon has a never expiring free tier for DynamoDB.
 * </p>
 * <p>
 * When the Elasticsearch Service is shut down, the data will be lost. For a demonstration application it makes sense to enter
 * data using the DynamoDB applicaiton and then load it into Elasticsearch. This allows the data to be persisted without cost in
 * DynamoDB. The Elasticsearch demo application can then be created as desired and loaded from DyanmoDB.
 * </p>
 * <p>
 * Aug 1, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class CopyFromDynamoDB {
    private final static String DYNAMODB_TABLE_NAME = "book_table";
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Read the entire book database. This function does a scan, which can be expensive on DynamoDB. However,
     * the assumption here is that the size of the book database is in the thousands, not millions (e.g., it's
     * not the Library of Congress).
     * 
     * @return
     */
    public List<BookInfo> getBooksFromDynamoDB(DynamoDBService dynamoDBService, final String dynamoDBTableName ) {
        AmazonDynamoDB client = dynamoDBService.getClient();
        ScanRequest scanRequest = new ScanRequest().withTableName( dynamoDBTableName );
        ScanResult result = client.scan(scanRequest);
        List<BookInfo> bookList = new ArrayList<BookInfo>();
        if (result.getCount() > 0) {
            // The itemList is a set of one or more DynamoDB row values stored in a attribute name/value map.
            List<Map<String, AttributeValue>> itemList = result.getItems();
            try {
                for (Map<String, AttributeValue> item : itemList) {
                    BookInfo info = new BookInfo();
                    DynamoDBUtil.attributesToObject(info, item);
                    bookList.add(info);
                }
                if (bookList.size() > 1) {
                    BookInfo[] infoArray = bookList.toArray( new BookInfo[1] );
                    Arrays.sort(infoArray, new BookInfoComparator() );
                    bookList.clear();
                    bookList.addAll(Arrays.asList( infoArray ) );
                }
            }
            catch (ReflectiveOperationException e) {
                logger.error("getBooks: " + e.getLocalizedMessage());
            }
        }
        return bookList;
    }
    
    void copyFromDynamoDB(final String dynamoDBTableName ) {
        DynamoDBService dynamoDBService = new DynamoDBService( IDynamoDB.region, 
                                                               IDynamoDB.dynamoDBReadOnlyID, 
                                                               IDynamoDB.dynamoDBReadOnlyKey);
        List<BookInfo> bookList = getBooksFromDynamoDB(dynamoDBService, dynamoDBTableName );
        BookSearchService bookSearchService = new BookSearchService();
        bookSearchService.loadBookList(bookList);
    }
    
    public static void main(String[] args) {
        CopyFromDynamoDB application = new CopyFromDynamoDB();
        application.copyFromDynamoDB(DYNAMODB_TABLE_NAME);
    }

}
