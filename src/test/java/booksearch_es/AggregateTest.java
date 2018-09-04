/** \file
 * 
 * Jul 30, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.JSONUtils.BucketAggregation;
import booksearch_es.json.Mapping;
import booksearch_es.model.BookInfo;
import booksearch_es.service.BookSearchService;
import booksearch_es.service.ElasticsearchService;
import booksearch_es.service.HttpService;

public class AggregateTest {
    private final static String INDEX_NAME = "aggregate_test";
    private final static String type = null;
    private final static String suffix = null;
    
    private static ArrayList<BookInfo> bookList = BookInfoUtil.buildBookList();

    @Before
    public void setUp() throws Exception {
        final String bookInfoMapping = Mapping.bookInfoMapping(false);

        String rslt = HttpService.putDocument(INDEX_NAME, type, suffix, bookInfoMapping);
        if (rslt != null) {
            if (JSONUtils.isAcknowledged(rslt)) {
                System.out.println(this.getClass().getName() + ": " + INDEX_NAME + " index created");
                // Add some BookInfo data to the index/type
                ElasticsearchService elasticService = new ElasticsearchService();
                for (BookInfo book : bookList) {
                    elasticService.addDocument(INDEX_NAME, Mapping.TYPE_NAME, book);
                }
            } else {
                fail(this.getClass().getName() + ": " + "Failed to create index");
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        String deleteIndexRslt = HttpService.deleteDocument(INDEX_NAME, type, suffix);
        if (deleteIndexRslt != null) {
            if (JSONUtils.isAcknowledged(deleteIndexRslt)) {
                System.out.println(this.getClass().getName() + ": " +  "Test index " + INDEX_NAME + " successfully removed");
            } else {
                fail(this.getClass().getName() + ": " + "Failed to remove the index " + INDEX_NAME );
            }
        }
    }
    
    /**
     * <p>
     * Count the publishers in a BookInfo array that is sorted by publisher.
     * </p>
     * @param publisherArray an array of BookInfo objects that is sorted by publisher.
     * @return a list of BucketAggregation objects with a publisher count.
     */
    private ArrayList<BucketAggregation> publisherCount(BookInfo[] publisherArray ) {
        ArrayList<BucketAggregation> publisherCount = new ArrayList<BucketAggregation>();
        BucketAggregation current = new BucketAggregation();
        current.key = "";
        current.doc_count = 0;
        for (BookInfo book : publisherArray) {
            if (! book.getPublisher().equalsIgnoreCase(current.key)) {
                if (current.doc_count > 0) {
                    publisherCount.add(current);
                }
                current = new BucketAggregation();
                current.key = book.getPublisher();
                current.doc_count = 1;
            } else {
                current.doc_count++;
            }
        }
        if (current.doc_count > 0) {
            publisherCount.add( current );
        }
        return publisherCount;
    } // publisherCount
    

    @Test
    public void testBucketAggregation() {
        BookInfo[] publisherArray = bookList.toArray( new BookInfo[1] );
        Arrays.sort(publisherArray, new CompareByPublisher() );
        ArrayList<BucketAggregation> compareAggregation = publisherCount( publisherArray );
        BookSearchService service = new BookSearchService();
        String aggregateName = "publishers";
        // Aggregations can only be performed on keyword fields, not text fields.
        String fieldName = "publisher.keyword";
        List<BucketAggregation> aggregateList = service.bucketAggregation(INDEX_NAME, aggregateName, fieldName);
        boolean testPassed = false;
        // The two arrays should be the same length
        if (compareAggregation.size() == aggregateList.size()) {
            // The comparison result is already sorted. The aggregation result is sorted by the query. The two
            // collections should have the same values.
            testPassed = true;
            for (int i = 0; i < compareAggregation.size(); i++) {
                if ((!compareAggregation.get(i).key.equals( aggregateList.get(i).key)) || 
                        compareAggregation.get(i).doc_count != aggregateList.get(i).doc_count) {
                    testPassed = false;
                    break;
                }
            }
        }
        assertTrue("The aggregate lists do not match", testPassed);
    }

}
