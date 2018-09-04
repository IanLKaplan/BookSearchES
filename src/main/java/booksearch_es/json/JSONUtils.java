/** \file
 * 
 * Jun 13, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import booksearch_es.model.BookInfo;

public final class JSONUtils {

    /**
     * <h4>
     * BucketAggregation
     * </h4>
     * <p>
     * The BucketAggregation class is a container for the aggregation information returned by a bucket
     * term aggregation query. This information is returned as an array. A single entry is shown below:
     * </p>
     * <pre>
     * {
          "key": "Tor Books",
          "doc_count": 1
        }
     * </pre>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    public static class BucketAggregation {
        public String key;
        public int doc_count;
        public String getKey() { return key; }
        public String getCount() { return Integer.toString(doc_count); }
    }
    
    /**
     * <h4>
     * AggregateCompare
     * </h4>
     * <p>
     * Compare two aggregates, by their keys. The comparision ignores the aggregate doc_count.
     * </p>
     * Jul 30, 2018
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    public static class AggregateCompare implements Comparator<BucketAggregation> {

        @Override
        public int compare(BucketAggregation o1, BucketAggregation o2) {
            int compareRslt = o1.key.compareTo( o2.key );
            return compareRslt;
        }
        
    }

    private static Logger logger = LoggerFactory.getLogger( JSONUtils.class.getCanonicalName());
    
    /**
     * Convert a JSON string to a Jackson JsonNode object. The JsonNode is a hierarchical object that
     * allows the JSON components to be referenced.
     * 
     * @param jsonStr
     * @throws IOException 
     */
    public static JsonNode stringToJsonNode( final String jsonStr ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonStr);
        return jsonNode;
    }
    
    /**
     * <p>
     * A number of Elasticsearch operations return JSON containing:
     * </p>
     * <pre>
     * {"acknowledged":true, ... }
     * </pre>
     * <p>
     * This function returns true if the JSON contains "acknowledged" : true. False otherwise.
     * </p>
     * @param json a JSON string
     */
    public static boolean isAcknowledged( String json ) {
        boolean acknowledge = false;
        try {
            JsonNode node = JSONUtils.stringToJsonNode( json );
            if (node != null) {
                node = node.get("acknowledged");
                if (node != null) {
                    acknowledge = node.asBoolean();
                }
            }
        } catch (IOException e) { }
        return acknowledge;
    }
    
    /**
     * <p>
     * Extract the BookInfo objects from the Elasticsearch query result. This is done
     * by traversing the JSON tree.
     * </p>
     * <p>
     * Query result (note score):
     * </p>
     * <pre>
     * {"took":6,
     *  "timed_out":false,
     *  "_shards": {
     *     "total":5,
     *     "successful":5,
     *     "skipped":0,
     *     "failed":0
     *     },
     *     "hits":{
     *        "total":2,
     *        "max_score":0.2876821,
     *        "hits":[{
     *             "_index":"search_by_author_test",
     *             "_type":"bookinfo",
     *             "_id":"4e7f7c3f639a9e5d09de075594496cce",
     *             "_score":0.2876821,
     *             "_source":{
     *                 "title":"Count Zero",
     *                 "author":"William Gibson",
     *                 "genre":"Science Fiction",
     *                 "publisher":"HarperCollins Publishers",
     *                 "year":"1986",
     *                 "price":"47.50"
     *               }
     *             },{
     *                "_index":"search_by_author_test",
     *                "_type":"bookinfo",
     *                "_id":"748d62eb46d498720a337fc16a9be24c",
     *                "_score":0.2876821,
     *                "_source":{
     *                    "title":"Neuromancer",
     *                    "author":"William Gibson",
     *                    "genre":"Science Fiction",
     *                    "publisher":"Ace",
     *                    "year":"1984",
     *                    "price":"14.77"
     *                 }
     *              }
     *           ]
     *       }
     *    }
     * </pre>
     * <p>
     * Filter query result (score is always zero)
     * </p>
     * <pre>
     * {
      "took": 5,
      "timed_out": false,
      "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
      },
      "hits": {
        "total": 2,
        "max_score": 0,
        "hits": [
          {
            "_index": "search_by_author_test",
            "_type": "bookinfo",
            "_id": "4e7f7c3f639a9e5d09de075594496cce",
            "_score": 0,
            "_source": {
              "title": "Count Zero",
              "author": "William Gibson",
              "genre": "Science Fiction",
              "publisher": "HarperCollins Publishers",
              "year": "1986",
              "price": "47.50"
            }
          },
          {
            "_index": "search_by_author_test",
            "_type": "bookinfo",
            "_id": "748d62eb46d498720a337fc16a9be24c",
            "_score": 0,
            "_source": {
              "title": "Neuromancer",
              "author": "William Gibson",
              "genre": "Science Fiction",
              "publisher": "Ace",
              "year": "1984",
              "price": "14.77"
            }
          }
        ]
      }
    }
     * </pre>
     * @param jsonQueryRslt
     * @return
     */
    public static int extractFromQueryResult( final String jsonQueryRslt, List<BookInfo> bookList ) {
        int total = 0;
        if (jsonQueryRslt != null && jsonQueryRslt.length() > 0 && bookList != null) {
            try {
                JsonNode node = JSONUtils.stringToJsonNode( jsonQueryRslt );
                node = node.get("hits");
                if (node != null) {
                    JsonNode totalNode = node.get("total");
                    if (totalNode != null) {
                        total = totalNode.asInt();
                    }
                    JsonNode objArray = node.get("hits");  // fetch the array associated with the second instance of "hits"
                    if (objArray.isArray()) {
                        ObjectMapper mapper = new ObjectMapper();
                        for (JsonNode bookInfoContainer : objArray) {
                            JsonNode bookInfoJson = bookInfoContainer.get("_source");
                            BookInfo info = mapper.convertValue(bookInfoJson, BookInfo.class);
                            if (info != null) {
                                bookList.add(info);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error processing JSON: " + e.getLocalizedMessage());
            }
        }
        return total;
    }
    
    
    /**
     * <p>
     * Extract JSON objects from an Elasticsearch query result. This is used to dump the Elasticsearch database
     * to JSON.  An example of a matchall query result is shown below. Note that each query only returns
     * ten results.
     * </p>
     * <p>
     * Note that the JSON below has been edited. It contains ten JSON object elements.
     * </p>
     * <pre>
 {
  "took": 0,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 120,
    "max_score": 1,
    "hits": [
      {
        "_index": "bookindex",
        "_type": "bookinfo",
        "_id": "c03bf87cadf1990c4793dc05da6816a5",
        "_score": 1,
        "_source": {
          "title": "Inversions",
          "author": "Iain M. Banks",
          "genre": "Science Fiction",
          "publisher": "Atria",
          "year": "2000",
          "price": "14.13"
        }
      },
      ...
      {
        "_index": "bookindex",
        "_type": "bookinfo",
        "_id": "b13a7037ac93b6fbae8b8aded7ee05e6",
        "_score": 1,
        "_source": {
          "title": "Imperium",
          "author": "Robert Harris",
          "genre": "Fiction",
          "publisher": "Simon Schuster",
          "year": "2006",
          "price": "26.00"
        }
      }
    ]
  }
}
     * </pre>
     * @param jsonQueryRslt
     * @param jsonArrayNode
     * @return
     */
    public static int extractJSONObjFromQueryResult(final String jsonQueryRslt, ArrayNode jsonArrayNode ) {
        int total = 0;
        if (jsonQueryRslt != null && jsonQueryRslt.length() > 0) {
            try {
                JsonNode node = JSONUtils.stringToJsonNode( jsonQueryRslt );
                node = node.get("hits");
                if (node != null) {
                    JsonNode totalNode = node.get("total");
                    if (totalNode != null) {
                        total = totalNode.asInt();
                    }
                    JsonNode jsonObjArray = node.get("hits");  // fetch the array associated with the second instance of "hits"
                    if (jsonObjArray.isArray()) {
                        for (JsonNode jsonObjElem : jsonObjArray) {
                            JsonNode jsonObj = jsonObjElem.get("_source");
                            jsonArrayNode.add( jsonObj );
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error processing JSON: " + e.getLocalizedMessage());
            }
        }
        return total;
    }
    
    /**
     * <p>
     * Extract the result from an Elasticsearch bucket term aggregation query.  An example of an aggregation query
     * on book publishers is shown below.
     * </p>
     *  <pre>
      {
    "took": 22,
    "timed_out": false,
    "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
    },
    "hits": {
    "total": 19,
    "max_score": 0,
    "hits": []
    },
    "aggregations": {
    "publishers": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "Ace",
          "doc_count": 10
        },
        {
          "key": "Gollancz",
          "doc_count": 4
        },
        {
          "key": "Bantam Books",
          "doc_count": 1
        },
        {
          "key": "Doubleday",
          "doc_count": 1
        },
        {
          "key": "HarperCollins Publishers",
          "doc_count": 1
        },
        {
          "key": "Orbit Books",
          "doc_count": 1
        },
        {
          "key": "Tor Books",
          "doc_count": 1
        }
      ]
    }
    }
    }
     * </pre>
     * @param jsonResult
     * @return
     */
    public static List<BucketAggregation> extractBucketTermAggregationResult(final String aggregateName, final String jsonResult ) {
        ArrayList<BucketAggregation> bucketInfoList = new ArrayList<BucketAggregation>();
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                JsonNode node = JSONUtils.stringToJsonNode( jsonResult );
                if (node != null) {
                    JsonNode aggTree = node.get("aggregations");
                    if (aggTree != null) {
                        JsonNode aggRsltTree = aggTree.get(aggregateName);
                        if (aggRsltTree != null) {
                            JsonNode buckets = aggRsltTree.get("buckets");
                            if (buckets.isArray()) {
                                ObjectMapper mapper = new ObjectMapper();
                                for (JsonNode bucketInfoJson : buckets) {
                                    BucketAggregation info = mapper.convertValue(bucketInfoJson, BucketAggregation.class);
                                    if (info != null) {
                                        bucketInfoList.add( info );
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error processing bucket term aggregation result: " + e.getLocalizedMessage());
            }
        }
        return bucketInfoList;
    }
    
    /**
     * <p>
     * Check that a JSON string is valid JSON
     * </p>
     * <p>
     * This code is from a Stackoverflow post: https://stackoverflow.com/q/22295422/2341077
     * </p>
     * 
     * @param jsonInString
     * @return returns true if the string is valid JSON (e.g., it parses). False if the JSON is not
     * valid (e.g., it fails parsing with an exception).
     * 
     */
    public static boolean isJSONValid(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
}
