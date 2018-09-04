/** \file
 * 
 * Jul 11, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import booksearch_es.json.JSONUtils;
import booksearch_es.json.JSONUtils.BucketAggregation;
import booksearch_es.json.Mapping;
import booksearch_es.model.BookIndex;
import booksearch_es.model.BookInfo;

/**
 * <h4>BookSearchService</h4>
 * <p>
 * Support for book search application level services (e.g., queries) . These are built on top of
 * generic Elasticsearch operations.
 * </p>
 * <p>
 * Jul 11, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class BookSearchService {
    /**
     * <h4>Author</h4>
     * <p>
     * This serializes to the JSON { "author" : "author name here" }
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Author {
        @SuppressWarnings("unused")
        final public String author;

        public Author(final String author) {
            this.author = author;
        }
    }

    /**
     * <h4>BoolExp</h4>
     * <p>
     * This serializes to
     * </p>
     * 
     * <pre>
     * bool : {
     *    JSON
     * }
     * </pre>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class BoolExp {
        @SuppressWarnings("unused")
        final public Object bool;

        public BoolExp(Object obj) {
            this.bool = obj;
        }
    }

    private static class FilterArrayExp {
        @SuppressWarnings("unused")
        final public Object[] filter;

        public FilterArrayExp(Object[] filterArray) {
            this.filter = filterArray;
        }
    }

    private static class FilterExp {
        @SuppressWarnings("unused")
        final public Object filter;

        public FilterExp(Object obj) {
            this.filter = obj;
        }
    }

    private static class Genre {
        @SuppressWarnings("unused")
        final public String genre;

        public Genre(final String genre) {
            this.genre = genre;
        }
    }

    /**
     * <h4>MatchExp</h4>
     * 
     * <p>
     * For generating the JSON:
     * </p>
     * 
     * <pre>
     * "match": {
               Match expression
             }
     * </pre>
     * <p>
     * Jun 15, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class MatchExp {
        @SuppressWarnings("unused")
        final public Object match;

        public MatchExp(Object obj) {
            match = obj;
        }
    }

    /**
     * <h4>Price</h4>
     * <p>
     * This serializes to { "price" : "floating point price" }
     * </p>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Price {
        @SuppressWarnings("unused")
        final public String price;

        public Price(final String price) {
            this.price = price;
        }
    }

    /**
     * <h4>Publisher</h4>
     * <p>
     * This serializes to { "publisher" : "publisher name here" }
     * </p>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Publisher {
        @SuppressWarnings("unused")
        final public String publisher;

        public Publisher(final String publisher) {
            this.publisher = publisher;
        }
    }

    /**
     * <h4>Query</h4>
     * <p>
     * For generating the JSON
     * </p>
     * 
     * <pre>
     * "query": { 
             Query expression
          }
     * </pre>
     * 
     * Jun 15, 2018
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Query {
        @SuppressWarnings("unused")
        final public Object query;

        public Query(final Object query) {
            this.query = query;
        }
    }

    /**
     * <h4>Title</h4>
     * <p>
     * This serializes to the JSON { "title" : "book title here" }
     * </p>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Title {
        @SuppressWarnings("unused")
        final public String title;

        public Title(final String title) {
            this.title = title;
        }
    }

    /**
     * <h4>Year</h4>
     * <p>
     * This serializes to { "year" : "year data value" }
     * </p>
     * <p>
     * Jul 26, 2018
     * </p>
     * 
     * @author Ian Kaplan, iank@bearcave.com
     */
    private static class Year {
        @SuppressWarnings("unused")
        final public String year;

        public Year(final String year) {
            this.year = year;
        }
    }

    private final static int GET_MAX = 10000;

    private final static String SEARCH_SUFFIX = "_search";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ElasticsearchService elasticService = new ElasticsearchService();
    
    // Allocate the BookIndex singleton which will create the Elasticsearch mapping (index) for the BookInfo data if it 
    // doesn't already exist.
    @SuppressWarnings("unused")
    private final BookIndex bookIndex = new BookIndex();

    /**
     * <p>
     * Build a bucket terms aggregate query.
     * </p>
     * <p>
     * The documentation on the term aggregate query mentions that the query returns the top aggregates.
     * But it doesn't mention how to return all of the aggregates. One way to return all of the aggregates
     * is to set the size field to a large number (10000 is the maximum number of aggregates that can be
     * returned). This is done by this query.
     * </p>
     * <pre>
      {
        "size" : 0,
        "aggs" : {
           "aggregate name goes here" : {
               "terms" : { "field" : "field name goes here",
                            "size" : 10000,
                           "order" : {"_key" : "asc" } }
            }
         }
       }
     * </pre>
     * <p>
     * For example:
     * </p>
     * <pre>
        POST bookindex/bookinfo/_search
        {"size":0,
         "aggs":{
             "PublisherAgg":{
                 "terms":{
                    "field":"publisher.keyword",
                    "size" : 10000,
                    "order":{"_key":"asc"}
                 }
             }
         }
       }
     * </pre>
     * <p>
     * This query orders the aggregation result by lexically sorting on the basis of the aggregation result key.
     * The sort is in ascending order.
     * </p>
     * @param fieldName
     * @return
     */
    protected String buildBucketTermsAggregate(final String aggregateName, final String fieldName) {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                    // {
        generator.write("size", 0);                      //  "size" : 0,
        generator.writeStartObject("aggs");              //  "aggs" : {
        generator.writeStartObject(aggregateName);       //     "aggregateName" : {
        generator.writeStartObject("terms");             //        "terms" : {
        generator.write("field", fieldName);             //            "field" : "fieldName",
        generator.write("size", 10000);                  //            "size" : 10000,
        generator.writeStartObject("order");             //            "order : {
        generator.write("_key", "asc");                  //                "_key" : "asc"
        generator.writeEnd();                            //             }
        generator.writeEnd();                            //        }
        generator.writeEnd();                            //     }
        generator.writeEnd();                            //   }
        generator.writeEnd();                            // }
        generator.close();
        return writer.toString();
    }

    /**
     * <pre>
     * <pre>
     * {
         "query": {
         "bool" : {
            "filter" : [
               { "match" : { "author" : bookInfo.getAuthor() }},
               { "match" : { "title" : bookInfo.getTitle()}},
               { "match" : { "publisher" : bookInfo.getPublisher() }},
               { "match" : { "year" : bookInfo.getDate() }}
           ]
         }
       }
     }
     * </pre>
     * </pre>
     * 
     * @param info
     * @return
     * @throws JsonProcessingException
     */
    protected String buildBookInfoQuery(final BookInfo info) throws JsonProcessingException {
        Object[] matchArray = { new MatchExp(new Title(info.getTitle())), new MatchExp(new Author(info.getAuthor())),
                new MatchExp(new Publisher(info.getPublisher())), new MatchExp(new Genre(info.getGenre())),
                new MatchExp(new Year(info.getYear())), new MatchExp(new Price(info.getPrice())) };
        final String jsonString = buildFilterArrayQuery(matchArray);
        return jsonString;
    }

    protected String buildFilterArrayQuery(Object[] matchArray) throws JsonProcessingException {
        Query query = new Query(new BoolExp(new FilterArrayExp(matchArray)));
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(query);
        return jsonString;
    }

    /**
     * <p>
     * Build a filter query. Filter queries do not calculate a match score. The
     * query is boolean in the sense that there is either a match or not. Filter
     * queries are supposed to be faster, since score calculation is not required.
     * The query can also be cached.
     * </p>
     * <p>
     * Filter queries work well for the BookSearch application since the search term
     * either matches or not.
     * </p>
     * <p>
     * An example of a filter query to search for an author name is shown below:
     * </p>
     * 
     * <pre>
      {
        "query": {
          "bool": {
            "filter": {
              "match": {
                "author": "gibson"
              }
            }
          }
        }
      }
     * </pre>
     * <p>
     * The Elasticsearch documentation is not terribly clear and the book
     * Elasticsearch in Action is out of date. As far as I can tell, all parts of
     * the query above are needed. For example, removing the "book" or "match"
     * clauses results in a syntax error.
     * </p>
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    protected String buildFilterQuery(Object obj) throws JsonProcessingException {
        Query query = new Query(new BoolExp(new FilterExp(new MatchExp(obj))));
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(query);
        return jsonString;
    }

    /**
     * <p>
     * Add "from" and "size" fields to the query that is passed in.
     * </p>
     * 
     * <pre>
     * {
         "from" : 10, "size" : 3,
         "query" : {
            ...
        }
      }
     * </pre>
     * <p>
     * The Jackson class library is used to construct the new tree from the existing
     * JSON.
     * </p>
     * 
     * @param startIx
     *            The first record to fetch (numbered from zero)
     * @param fetchSize
     *            The number of records to fetch
     * @param jsonQuery
     *            a JSON query that starts with { "query" : { ... } }
     * @return The JSON query with "from" and "size" fields added.
     */
    protected String buildFromQuery(int startIx, int fetchSize, String jsonQuery ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonTree = mapper.readTree(jsonQuery);
        ObjectNode objNode = mapper.createObjectNode();
        objNode.put("from", startIx);
        objNode.put("size", fetchSize);
        // iterate over the elements of the JSON tree and add them to the ObjectNode that is being
        // built for the new query.
        Iterator<Map.Entry<String, JsonNode>> fieldItr = jsonTree.fields();
        while (fieldItr.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldItr.next();
            objNode.set(entry.getKey(), entry.getValue());
        }        
        String newTree = mapper.writeValueAsString(objNode);
        return newTree;
    }
    
    
    /**
     * <p>
     * Build the JSON for a match_all query that will return all of the books in the Elasticsearch database.
     * </p>
     * <pre>
     * {
     *    "query" : {
     *        "match_all" : {}
     *    },
     *    "sort" : [
     *        {"genre" : { "order" : "asc" } }
     *       ]
     * }
     * </pre>
     * <p>
     * The query result is sorted by genre.
     * </p>
     * @return the JSON for a match_all query.
     */
    protected String buildMatchAllQuery() {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                    // {
        generator.writeStartObject("query");              //  "query" : {
        generator.writeStartObject("match_all");          //      "match_all" : {
        generator.writeEnd();                            //                     }
        generator.writeEnd();                            //    },
        generator.writeStartArray("sort");               //    "sort" : [
        generator.writeStartObject();                    //        {
        generator.writeStartObject("genre");             //           "genre" : {
        generator.write("order", "asc");                 //               "order" : "asc"
        generator.writeEnd();                            //            }
        generator.writeEnd();                            //        }
        generator.writeEnd();                            //    ]
        generator.writeEnd();                            // }
        generator.close();
        return writer.toString();
    }

    /**
     * <p>
     * Build the JSON for a query expression:
     * </p>
     * 
     * <pre>
      "query" : {
          "match": {
               Match expression
              }
        }
     * </pre>
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    protected String buildMatchQuery(Object obj) throws JsonProcessingException {
        final MatchExp match = new MatchExp(obj);
        final Query query = new Query(match);
        ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(query);
        return jsonString;
    }

    /**
     * <p>
     * Build the JSON
     * </p>
     * 
     * <pre>
     * {
         "query": {
         "bool" : {
            "filter" : [
               { "match" : { "author" : "gibson" }},
               { "match" : { "title" : "neuromancer"}}
           ]
         }
       }
     }
     * </pre>
     * 
     * @param title
     * @param author
     * @return
     * @throws JsonProcessingException
     */
    protected String buildTitleAuthorQuery(final String title, final String author) throws JsonProcessingException {
        Object[] matchArray = { new MatchExp(new Title(title)), new MatchExp(new Author(author)) };
        final String jsonString = buildFilterArrayQuery(matchArray);
        return jsonString;
    }
    
    
    /**
     * <p>
     * Build a genre query. For example:
     * </p>
     * <pre>
      GET bookindex/bookinfo/_search
      {"query":
         {"match":
             {"genre":"Science Fiction"}
         },
         "sort":[
            {"author_last_name": {"order" : "asc"}},
            {"title.keyword":{"order":"asc"}}
         ]
       }
     *  <pre>
     * @param genre
     * @return
     * @throws JsonProcessingException
     */
    protected String buildGenreQuery(final String genre) throws JsonProcessingException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                    // {
        generator.writeStartObject("query");              //  "query" : {
        generator.writeStartObject("match");          //      "match" : {
        generator.write("genre", genre );               //        "genre" : [genre]            
        generator.writeEnd();                            //             }
        generator.writeEnd();                            //    },
        generator.writeStartArray("sort");               //    "sort" : [
        generator.writeStartObject();                    //        {
        generator.writeStartObject("author_last_name");  //          "author_last_name" : {
        generator.write("order", "asc");                 //               "order" : "asc"
        generator.writeEnd();                            //            }
        generator.writeEnd();                            //        },
        generator.writeStartObject();                    //        {
        generator.writeStartObject("title.keyword");     //              "title.keyword" : {
        generator.write("order", "asc");                 //               "order" : "asc"
        generator.writeEnd();                            //            }
        generator.writeEnd();                            //        }
        generator.writeEnd();                            //    ]
        generator.writeEnd();                            // }
        generator.close();
        String jsonString = writer.toString();
        return jsonString;
    }
    
   
    /**
     * <p>
     * Build a query on publisher. For example:
     * </p>
     * <pre>
     {"query":
         {"match":
             {"publisher.keyword":"Arbor House"}
         },
         sort":[
            {"author_last_name": {"order" : "asc"}},
            {"title.keyword":{"order":"asc"}}
         ]
      }
     * </pre>
     * <p>
     * Note that the search is done on the keyword field.
     * </p>
     * @param publisher
     * @return
     * @throws JsonProcessingException
     */
    protected String buildPublisherKeywordQuery(final String publisher) throws JsonProcessingException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                     // {
        generator.writeStartObject("query");              //  "query" : {
        generator.writeStartObject("match");              //     "match" : {
        generator.write("publisher.keyword", publisher);  //         "publisher.keyword" : "[publisher]",
        generator.writeEnd();                             //      }
        generator.writeEnd();                             //   }
        generator.writeStartArray("sort");               //    "sort" : [
        generator.writeStartObject();                    //        {
        generator.writeStartObject("author_last_name");  //          "author_last_name" : {
        generator.write("order", "asc");                 //               "order" : "asc"
        generator.writeEnd();                            //            }
        generator.writeEnd();                            //        },
        generator.writeStartObject();                    //        {
        generator.writeStartObject("title.keyword");     //              "title.keyword" : {
        generator.write("order", "asc");                 //               "order" : "asc"
        generator.writeEnd();                            //            }
        generator.writeEnd();                            //        }
        generator.writeEnd();                            //    ]
        generator.writeEnd();                             // }
        generator.close();
        return writer.toString();
    }
    
    
    /**
     * <p>
     * Return the result of a GET query.
     * </p>
     * <p>
     * By default Elasticsearch only returns ten results. The total in the query
     * indicates how many results are available. Here, for example, is a query that
     * has thirteen result values. Of those, ten are returned. The next three values
     * must be returned by issuing another query.
     * </p>
     * 
     * <pre>
     * {
         ...
        "hits": {
        "total": 13,
        "max_score": 0,
        "hits": [
            ....
        ]
     * </pre>
     * <p>
     * Query results are numbered from 0. So the first ten results are 0...9. In the
     * example above the subsequent results would be fetched by asking from the
     * result starting at 10, with a size of three.
     * </p>
     * 
     * <pre>
      {
         "from" : 10, "size" : 3,
         "query" : {
            ...
        }
      }
     * </pre>
     * <p>
     * This code assumes that all of the results will easily fit into memory.
     * </p>
     * 
     * @param indexName
     * @param jsonQuery
     * @return
     * @throws IOException 
     */
    protected List<BookInfo> getQueryResult(final String indexName, final String jsonQuery) throws IOException {
        List<BookInfo> bookList = new ArrayList<BookInfo>();
        String result = HttpService.getDocument(indexName, Mapping.TYPE_NAME, SEARCH_SUFFIX, jsonQuery);
        // Get the first n results (where n <= 10). totalAvail is the total number of
        // results that are available
        int totalAvail = JSONUtils.extractFromQueryResult(result, bookList);
        int startIx = bookList.size();
        while (totalAvail > bookList.size()) {
            int numLeft = totalAvail - bookList.size();
            int fetchSize = Math.min(GET_MAX, numLeft);
            String newQuery = buildFromQuery(startIx, fetchSize, jsonQuery);
            result = HttpService.getDocument(indexName, Mapping.TYPE_NAME, SEARCH_SUFFIX, newQuery);
            JSONUtils.extractFromQueryResult(result, bookList);
            startIx = startIx + GET_MAX;
        }
        return bookList;
    }


    /**
     * <p>
     * Aggregate a field by term count.
     * </p>
     * <p>
     * One of the attractive features of Elasticsearch is it's ability to calculate
     * a variety of field aggregations. One of these is a bucket aggregation that
     * counts the instances of a term in a particular field.
     * </p>
     * <p>
     * For example, a term aggregation on publishers:
     * </p>
     * 
     * <pre>
      POST _search
      {
        "size" : 0,
        "aggs" : {
           "publishers" : {
               "terms" : { "field" : "publisher.keyword" }
            }
         }
       }
     * </pre>
     * <p>
     * If the "size" : 0 attribute is not included, the aggregate query will return
     * all of the records that were used to calculate the aggregation.
     * </p>
     * <p>
     * Aggregations can only be done on keyword fields. An attempt to aggregate on a
     * "text" field will result in an error.
     * </p>
     * <p>
     * An aside: the fact that aggregates use the HTTP POST operation and queries
     * use GET is inconsistent.
     * </p>
     * <p>
     * An example of the aggregation result is shown below:
     * </p>
     * 
     * <pre>
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
     * <p>
     * From https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket.html
     * </p>
     * <blockquote> 
     * The maximum number of buckets allowed in a single response is
     * limited by a dynamic cluster setting named search.max_buckets. It is disabled
     * by default (-1) but requests that try to return more than 10,000 buckets (the
     * default value for future versions) will log a deprecation warning.
     * </blockquote>
     * 
     * @param fieldName
     * @return
     */
    public List<BucketAggregation> bucketAggregation(final String index, final String aggregateName, final String fieldName) {
        String jsonString = buildBucketTermsAggregate(aggregateName, fieldName);
        String result = HttpService.postDocument(index, Mapping.TYPE_NAME, SEARCH_SUFFIX, jsonString);
        List<BucketAggregation> termList = JSONUtils.extractBucketTermAggregationResult(aggregateName, result);
        return termList;
    }
    
    /**
     * 
     * @param aggregateName The name assigned for the aggregate. This name is arbitrary. For example, if 
     *                      the call involves an aggregate of book publishers, the aggregateName could be
     *                      "publishers".
        // Aggregations can only be performed on keyword fields, not text fields.
        String fieldName = "publisher.keyword";
     * @param fieldName The field name in the Elasticsearch mapping. Aggregates can only be performed on keyword
     *                  fields. So in the case of publisher, this would be publisher.keyword. Book genre is a keyword
     *                  field so for a genre aggregate this would just be the field name.
     * @return a bucket aggregation key/value object
     */
    public List<JSONUtils.BucketAggregation> bucketAggregation(final String aggregateName, final String fieldName) {
        List<JSONUtils.BucketAggregation> termList = bucketAggregation(BookIndex.BOOK_INDEX_NAME, aggregateName, fieldName );
        return termList;
    }


    public boolean deleteByTitleAuthor(String title, String author) {
        boolean deleteResult = deleteByTitleAuthor(BookIndex.BOOK_INDEX_NAME, title, author);
        return deleteResult;
    }

    /**
     * <p>
     * Delete a bookinfo "document" via a title/author search.
     * </p>
     * <p>
     * See
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
     * </p>
     * 
     * <pre>
     * POST bookinfo/_delete_by_query
       {
         "query": {
         "bool" : {
            "filter" : [
               { "match" : { "author" : "gibson" }},
               { "match" : { "title" : "neuromancer"}}
           ]
         }
       }
     }
     * </pre>
     * <p>
     * A successful result will look something like this:
     * </p>
     * 
     * <pre>
      {
        "took": 31,
        "timed_out": false,
        "total": 1,
        "deleted": 1,
        "batches": 1,
        "version_conflicts": 0,
        "noops": 0,
        "retries": {
          "bulk": 0,
          "search": 0
        },
        "throttled_millis": 0,
        "requests_per_second": -1,
        "throttled_until_millis": 0,
        "failures": []
      }
     * </pre>
     * <p>
     * A failed result (e.g., deleting BookInfo that is not present) will look the
     * same, except that "deleted" will have the value of zero.
     * </p>
     * 
     * <pre>
       {
         "took": 4,
         "timed_out": false,
         "total": 0,
         "deleted": 0,
         "batches": 0,
         "version_conflicts": 0,
         "noops": 0,
         "retries": {
           "bulk": 0,
           "search": 0
         },
         "throttled_millis": 0,
         "requests_per_second": -1,
         "throttled_until_millis": 0,
         "failures": []
       }
     * </pre>
     * 
     * @param title
     * @param author
     * @return
     */
    public boolean deleteByTitleAuthor(final String index, final String title, final String author) {
        boolean deleteRslt = false;
        final String deleteByQuery = "_delete_by_query";
        try {
            String jsonDeleteByQuery = buildTitleAuthorQuery(title, author);
            String deleteResult = HttpService.postDocument(index, Mapping.TYPE_NAME, deleteByQuery, jsonDeleteByQuery);
            if (deleteResult != null && deleteResult.length() > 0) {
                // We assume that the title author pair results in a single book being deleted
                JsonNode jsonNode = JSONUtils.stringToJsonNode(deleteResult); // this call is associated with the
                                                                              // IOException
                JsonNode deletedNode = jsonNode.get("deleted");
                if (deletedNode != null) {
                    int numDeleted = deletedNode.asInt();
                    if (numDeleted >= 1) {
                        deleteRslt = true;
                    }
                }
            } else {
                logger.error("delete by title author did not return a result");
            }
        } catch (JsonProcessingException e) {
            logger.error("Error building JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("Error parsing the JSON result from delete by title author: " + e.getLocalizedMessage());
        }
        return deleteRslt;
    }

    /**
     * <p>
     * Find a book by an author name. If a search is done on "John" the search will
     * return all of the books by authors who are named "John".
     * </p>
     */
    public List<BookInfo> findBookByAuthor(String author) {
        List<BookInfo> bookList = findBookByAuthor(BookIndex.BOOK_INDEX_NAME, author);
        return bookList;
    }

    public List<BookInfo> findBookByAuthor(final String indexName, final String author) {
        List<BookInfo> bookList = null;
        try {
            final String jsonQuery = buildFilterQuery(new Author(author));
            bookList = getQueryResult(indexName, jsonQuery);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return bookList;
    }

    /**
     * <p>
     * Find a book by one or more title words. For example:
     * </p>
     * 
     * <pre>
     * GET index/type/_search
       {
        "query": {
          "bool": {
            "filter": {
              "match": {
                "author": "gibson"
              }
            }
          }
        }
      }
     * </pre>
     */
    public List<BookInfo> findBookByTitle(String titleWord) {
        return findBookByTitle(BookIndex.BOOK_INDEX_NAME, titleWord);
    }

    public List<BookInfo> findBookByTitle(final String index, final String titleWord) {
        List<BookInfo> bookList = null;
        try {
            final String jsonString = buildMatchQuery(new Title(titleWord));
            bookList = getQueryResult(index, jsonString);
        } catch (IOException e) {
            logger.error("Error building JSON for findBooksByTitle query: " + e.getLocalizedMessage());
        }
        return bookList;
    }

    /**
     * <p>
     * Find a book by title and author (this will, generally, return a single book)
     * </p>
     */
    public List<BookInfo> findBookByTitleAuthor(String title, String author) {
        List<BookInfo> book = findBookByTitleAuthor(BookIndex.BOOK_INDEX_NAME, title, author);
        return book;
    }

    /**
     * <p>
     * Find a book by searching for it by the author and title. This will generally
     * return a single book. The query is:
     * </p>
     * 
     * <pre>
      {
         "query": {
         "bool" : {
            "filter" : [
               { "match" : { "author" : "gibson" }},
               { "match" : { "title" : "neuromancer"}}
           ]
         }
       }
     }
     * </pre>
     * 
     * @param index
     * @param author
     * @param title
     * @return
     */
    public List<BookInfo> findBookByTitleAuthor(final String index, String title, String author) {
        List<BookInfo> book = null;
        try {
            final String jsonString = buildTitleAuthorQuery(title, author);
            book = getQueryResult(index, jsonString);
        } catch (IOException e) {
            logger.error("Error building JSON query: " + e.getLocalizedMessage());
        }
        return book;
    }
    
    
    public List<BookInfo> findBooksByGenre(final String index, String genre) {
        List<BookInfo> bookList = new ArrayList<BookInfo>();
        try {
            final String jsonString = buildGenreQuery( genre );
            bookList = getQueryResult(index, jsonString);
        } catch (IOException e) {
            logger.error("Error building JSON query: " + e.getLocalizedMessage());
        }
        return bookList;
    }
    
    public List<BookInfo> findBooksByGenre(final String genre) {
        List<BookInfo> bookList = findBooksByGenre(BookIndex.BOOK_INDEX_NAME, genre);
        return bookList;
    }

    public List<BookInfo> findBooksByPublisherKeyword(final String index, String publisher) {
        List<BookInfo> bookList = new ArrayList<BookInfo>();
        try {
            final String jsonString = buildPublisherKeywordQuery( publisher );
            bookList = getQueryResult(index, jsonString);
        } catch (IOException e) {
            logger.error("Error building JSON query: " + e.getLocalizedMessage());
        }
        return bookList;
    }

    
    public List<BookInfo> findBooksByPublisherKeyword(final String publisher) {
        List<BookInfo> bookList = findBooksByPublisherKeyword(BookIndex.BOOK_INDEX_NAME, publisher);
        return bookList;
    }


    public List<BookInfo> getBooks() {
        List<BookInfo> bookList = getBooks(BookIndex.BOOK_INDEX_NAME);
        return bookList;
    }

    /**
     * Return all of the books in the database, sorted by genre.
     * 
     * @param index
     * @return
     */
    public List<BookInfo> getBooks(final String index) {
        String jsonQuery = buildMatchAllQuery();
        List<BookInfo> bookList = new ArrayList<BookInfo>();
        try {
            bookList = getQueryResult(index, jsonQuery);
        } catch (IOException e) {
            logger.error("Error building the getBooks matchAll query: " + e.getLocalizedMessage());
        }
        return bookList;
    }
    
    
    /**
     * <p>
     * Search for an exact book in the Elasticsearch database.
     * </p>
     * <p>
     * Elasticsearch will not do a record match, but selected fields can be matched.
     * </p>
     * 
     * <pre>
     {
    "query": {
    "bool" : {
      "filter" : [
        { "match" : { "author" : "William Gibson" }},
        { "match" : { "title" : "Neuromancer"}},
        { "match" : { "publisher" : "Ace" }},
        { "match" : { "year" : "1984" }}
      ]
    }
    }
    }
     * </pre>
     * <p>
     * Or
     * </p>
     * 
     * <pre>
     {
    "query": {
    "bool" : {
    "filter" : [
      { "match" : { "author" : "William Gibson" }},
      { "match" : { "title" : "Mona Lisa Overdrive"}},
      { "match" : { "publisher" : "Bantam" }},
      { "match" : { "year" : "1988" }}
    ]
    }
    }
    }
     * </pre>
     */
    public boolean hasBookEntry(BookInfo bookInfo) {
        boolean foundBook = hasBookEntry(BookIndex.BOOK_INDEX_NAME, bookInfo);
        return foundBook;
    }
    
    public boolean hasBookEntry(final String indexName, BookInfo bookInfo) {
        boolean foundBook = false;
        try {
            String jsonQuery = buildBookInfoQuery(bookInfo);
            List<BookInfo> book = getQueryResult(indexName, jsonQuery);
            foundBook = (book.size() > 0);
        } catch (IOException e) {
            logger.error("Error building JSON query string for BookSearchService.hasBookEntry()");
        }
        return foundBook;
    }

    public void writeBookToDB(BookInfo info) {
        if (info != null) {
            boolean writeOK = elasticService.addDocument(BookIndex.BOOK_INDEX_NAME, Mapping.TYPE_NAME, info);
            if (!writeOK) {
                logger.error("Error writing book (title = " + info.getTitle() + ") to the database");
            }
        }
    }
    

    /**
     * Load a collection of books into the Elasticsearch database.
     * 
     * @param info
     */
    public void loadBookList(List<BookInfo> bookList) {
        ArrayList<Object> objList = new ArrayList<Object>();
        objList.addAll(bookList);
        elasticService.bulkLoad(BookIndex.BOOK_INDEX_NAME, Mapping.TYPE_NAME, objList);
    }
    
}
