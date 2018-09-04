/** \file
 * 
 * Jun 14, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import booksearch_es.json.JSONUtils;

/**
 * <h4>
 * ElasticsearchService
 * </h4>
 * <p>
 * Support for Elasticsearch operations.
 * </p>
 * <p>
 * Elasticsearch uses a REST interface (e.g., PUT, DELETE, GET, HEAD HTTP operations). The ElasticsearchService 
 * operations are built on top of the HttpService. However, these operations are designed to be generic. They
 * could work with in any application.
 * </p>
 * <p>
 * Jul 11, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class ElasticsearchService {
    private static String BULK = "_bulk";
    private static int OK_STATUS = 201;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    /**
     * Calculate an MD5 hash and return it as a hexadecimal string.
     * 
     * @param text The text that the MD5 hash will be calculated on
     * @return the MD5 hash as a hexadecimal string
     */
    public String calculateMD5String( final String text )  {
        String md5InHexStr = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest( text.getBytes( Charset.defaultCharset()));
            md5InHexStr = Hex.encodeHexString( digest );
        } catch (NoSuchAlgorithmException e) {
            logger.error("This should not have happened. Attempting to build an MD5 digest object caused an exception: " + e.getLocalizedMessage());
        }
        return md5InHexStr;
    }
    
    /**
     * <p>
     * Convert a Java Object to JSON and write the object to an Elasticsearch index and type.  The ID for the
     * object is calculated by taking the MD5 hash of the object.
     * </p>
     * <p>
     * The result of the Elastic search index operation is:
     * </p>
     * <pre>
     * {
    "_shards" : {
        "total" : 2,
        "failed" : 0,
        "successful" : 2
    },
    "_index" : "twitter",
    "_type" : "_doc",
    "_id" : "1",
    "_version" : 1,
    "_seq_no" : 0,
    "_primary_term" : 1,
    "result" : "created"
}
     * </pre>
     * <p>
     * Presumably when the result field has the value "created", the operation succeeded. The code below checks for this condition
     * and returns true if this is the case.
     * </p>
     * 
     * @param index
     * @param type
     * @param obj
     * @return
     */
    public boolean addDocument( final String index, final String type, final Object obj) {
        boolean addRslt = false;
        if (index != null && index.length() > 0) {
            if (obj != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    final String jsonString = mapper.writeValueAsString( obj );
                    // calculate an MD5 hash of the JSON string to use as the document ID
                    final String md5ID = calculateMD5String( jsonString );
                    String httpResponse = HttpService.putDocument(index, type, md5ID, jsonString);
                    if (httpResponse != null && httpResponse.length() > 0) {
                        //  Check response to see whether the "document" was added to the Elasticsearch database
                        JsonNode node = JSONUtils.stringToJsonNode(httpResponse);
                        JsonNode resultNode = node.get("result");
                        if (resultNode != null) {
                            String resultVal = resultNode.textValue();
                            if (resultVal != null && resultVal.length() > 0) {
                                addRslt = resultVal.equals("created");
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error converting object to JSON: " + e.getLocalizedMessage());
                } catch (IOException e) {
                    logger.error("Error processing the HTTP response: " + e.getLocalizedMessage());
                }
            } else {
                logger.error("Attempted to send a null object to Elasticsearch. Bad programmer. No cookie!");
            }
        } else {
            logger.error("addDocument: an index must be specified");
        }
        return addRslt;
    } // add Document
    
    
    /**
     * <p>
     * Build the "create" prolog for an Elasticsearch load object command.
     * </p>
     * <pre>
     * {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"748d62eb46d498720a337fc16a9be24c"}}
     * </pre>
     * @param index
     * @param type
     * @param hash
     * @return
     */
    protected String buildCreateJSON(final String index, final String type, final String hash ) {
        // Build a JSON Generator (from the javax library)
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);   
        generator.writeStartObject();         // {
        generator.writeStartObject("create"); //    "create" : {
        generator.write("_index", index);     //        "_index" : <index>,
        generator.write("_type", type);       //        "_type"  : <type>
        generator.write("_id", hash);        //        "_id"    : <id>
        generator.writeEnd();                 //    }    
        generator.writeEnd();                 // }
        generator.close();
        String createStr = writer.toString();
        return createStr;
    }
    
    /**
     * <pre>
     * * { "create" : { "_index" : <some_index>, "_type" : <some_type>,  "_id" : <some_id> } } \n
     * { <json_data> } \n
     * </pre>
     * <p>
     * Note that the prefix (e.g., "create") and the JSON data must be on separate lines (e.g., text 
     * separated by "\n").
     * </p>
     * <p>
     * Here is an example:
     * </p>
     * <pre>
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"748d62eb46d498720a337fc16a9be24c"}}
        {"title":"Neuromancer","author":"William Gibson","genre":"Science Fiction","publisher":"Ace","year":"1984","price":"14.77"}
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"4e7f7c3f639a9e5d09de075594496cce"}}
        {"title":"Count Zero","author":"William Gibson","genre":"Science Fiction","publisher":"HarperCollins Publishers","year":"1986","price":"47.50"}
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"4e6cc29ef1025a9fc00fe1702ad26da3"}}
        {"title":"Mona Lisa Overdrive","author":"William Gibson","genre":"Science Fiction","publisher":"Bantam Books","year":"1988","price":"14.00"}
     * </pre>
     * 
     * @param index
     * @param type
     * @param objList
     * @param builder
     */
    protected void addBulkLoadObject(final String index, final String type, final String jsonString, StringBuilder builder ) {
        // calculate an MD5 hash of the JSON string to use as the document ID
        final String md5ID = calculateMD5String( jsonString );
        // Build a JSON Generator (from the javax library)
        String prologStr = buildCreateJSON(index, type, md5ID );
        builder.append(prologStr);
        builder.append("\n");
        builder.append(jsonString);
        builder.append("\n");
    }
    
    
    protected void addBulkLoadObject(final String index, final String type, final Object obj, StringBuilder builder ) {
        if (obj != null ) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                final String jsonString = mapper.writeValueAsString( obj );
                addBulkLoadObject(index, type, jsonString, builder);
            } catch (JsonProcessingException e) {
                logger.error("Error converting object to JSON: " + e.getLocalizedMessage());
            }
        } else {
            logger.error("Attempted to send a null object to Elasticsearch. Bad programmer. No cookie!");
        }
    }
    
    protected boolean sendBulkLoadJSON(final String bulkJSON, int numObjects) {
        boolean bulkLoadRslt = false;
        // The index and type are included in the bulk load prefix for each object, so they are ommitted from the URL
        String url = IElasticsearch.ES_URL + "/" + BULK;
        String httpResponse = HttpService.postDocument(url, bulkJSON);
        if (httpResponse != null && httpResponse.length() > 0) {
            //  Check response to see whether the "document" was added to the Elasticsearch database
            try {
                JsonNode node = JSONUtils.stringToJsonNode(httpResponse);
                if (node != null) {
                    // search the JSON tree for "status" : 201 nodes
                    List<JsonNode> statusNodes = node.findValues("status");
                    if (statusNodes != null && statusNodes.size() == numObjects) {
                        // now check that they values are actually 201
                        bulkLoadRslt = true;
                        for (JsonNode statusNode : statusNodes) {
                            if (statusNode.asInt() != OK_STATUS) {
                                logger.error("Error in bulk load. HTTP status = " + statusNode.asInt());
                                bulkLoadRslt = false;
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error processing bulk load JSON: " + e.getLocalizedMessage());
            }
        }
        return bulkLoadRslt;
    }
    
    
    /**
     * <p>
     * Load multiple objects into the Elasticsearch database. 
     * </p>
     * <p>
     * Loading single objects into Elasticsearch is time consuming since each object load takes one HTTP transaction.
     * Elasticsearch supports a bulk load API which, according to a Stackoverflow post, allows documents of up to 
     * 2 Gb (2^31 bytes) to be loaded in a single HTTP transaction. 
     * </p>
     * <p>
     * This code doesn't check the size of the bulk load JSON. The caller should limit the number of objects to something
     * reasonable so the data limit will not be exceeded.
     * </p>
     * <p>
     * The performance gain from using the bulk load facility is large enough that a limit in the tens of thousands of objects
     * will still result in a fast Elasticsearch load.
     * </p>
     * <p>
     * As usual, the Elasticsearch documentation is not very clear on how to structure a bulk load request. A good write-up,
     * with Java example code, by Stelios C can be found here:
     * </p>
     * <pre>
     * http://cscengineer.net/2016/10/22/elastic-search-bulk-api/
     * </pre>
     * <p>
     * In the comment for the createBulkPayload() function, Stelios writes:
     * </p>
     * <pre>
     * Elastic search expects the data in the following format:
     * json containing operation \n
     * json data \n
     * for example:
     * { "create" : {"_index" : <some_index>,  "_type" : <some_type>, "_id" : <some_id>  } } \n
     * { <json_data> } \n
     * </pre>
     * <p>
     * Note that the create JSON is on one line and the object JSON is on the following line (where a line is a section of text
     * terminated by a "\n").
     * </p>
     * <p>
     * The Elasticsearch documentation on bulk operations can be found here:
     * </p>
     * <pre>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
     * </pre>
     * <p>
     * Obviously this code assumes that the Jackson serialized object is compatible with the Elasticsearch index.
     * </p>
     * <p>
     * A sample result from loading three objects is shown below:
     * </p>
     * <pre>
     * {
  "took": 12,
  "errors": false,
  "items": [
    {
      "create": {
        "_index": "bulk_load_test",
        "_type": "bookinfo",
        "_id": "748d62eb46d498720a337fc16a9be24c",
        "_version": 1,
        "result": "created",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1,
        "status": 201
      }
    },
    {
      "create": {
        "_index": "bulk_load_test",
        "_type": "bookinfo",
        "_id": "4e7f7c3f639a9e5d09de075594496cce",
        "_version": 1,
        "result": "created",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1,
        "status": 201
      }
    },
    {
      "create": {
        "_index": "bulk_load_test",
        "_type": "bookinfo",
        "_id": "4e6cc29ef1025a9fc00fe1702ad26da3",
        "_version": 1,
        "result": "created",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1,
        "status": 201
      }
    }
  ]
}
     * </pre>
     * @param index
     * @param type
     * @param objList
     * @return
     */
    public boolean bulkLoad(final String index, final String type, final List<Object> objList ) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : objList) {
            addBulkLoadObject(index, type, obj, builder);
        }
        String bulkJSON = builder.toString();
        boolean bulkLoadRslt = sendBulkLoadJSON( bulkJSON, objList.size() );
        return bulkLoadRslt;
    }
    
    
    protected String buildMatchAllQuery() {
        StringWriter writer = new StringWriter();
        javax.json.stream.JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                    // {
        generator.writeStartObject("query");              //  "query" : {
        generator.writeStartObject("match_all");          //      "match_all" : {
        generator.writeEnd();                            //                     }
        generator.writeEnd();                            //    }
        generator.writeEnd();                            // }
        generator.close();
        return writer.toString();
    }
    
    
    protected String buildFromMatchAllQuery(final int start, final int size) {
        StringWriter writer = new StringWriter();
        javax.json.stream.JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();                    // {
        generator.write("from", start);                   //  "from" : [start index],
        generator.write("size", size);                    //  "size" : [number to return],
        generator.writeStartObject("query");              //  "query" : {
        generator.writeStartObject("match_all");          //      "match_all" : {
        generator.writeEnd();                            //                     }
        generator.writeEnd();                            //    }
        generator.writeEnd();                            // }
        generator.close();
        return writer.toString();
    }
    
    
    /**
    * <p>
    * The JSON file being loaded should in JSON array format, where each element of the array is a JSON "record".
    * When the Elasticsearch index is loaded the assumption is that the field names in the Elasticsearch mapping
    * are the same as the field names in the JSON array (e.g., "title", "author", etc...)
    * </p>
    * <pre>
     [ {
          "title" : "Inversions",
          "author" : "Iain M. Banks",
          "genre" : "Science Fiction",
          "publisher" : "Atria",
          "year" : "2000",
          "price" : "14.13"
        }, {
          "title" : "Transition",
          "author" : "Iain M. Banks",
          "genre" : "Science Fiction",
          "publisher" : "Orbit",
          "year" : "2009",
          "price" : "25.99"
        },
        ...
        ]
     * </pre>
     * <p>
     * The data load format is:
     * </p>
     * <pre>
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"748d62eb46d498720a337fc16a9be24c"}}
        {"title":"Neuromancer","author":"William Gibson","genre":"Science Fiction","publisher":"Ace","year":"1984","price":"14.77"}
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"4e7f7c3f639a9e5d09de075594496cce"}}
        {"title":"Count Zero","author":"William Gibson","genre":"Science Fiction","publisher":"HarperCollins Publishers","year":"1986","price":"47.50"}
        {"create":{"_index":"bulk_load_test","_type":"bookinfo","_id":"4e6cc29ef1025a9fc00fe1702ad26da3"}}
        {"title":"Mona Lisa Overdrive","author":"William Gibson","genre":"Science Fiction","publisher":"Bantam Books","year":"1988","price":"14.00"}
     * </pre>
     * @param indexName the name of the Elasticsearch index
     * @param typeName the type name for the Elasticsearch mapping
     * @param jsonData the JSON data to be loaded into the Elasticsearch index.
     * @throws IOException 
     */
    public void loadElasticsearch(final String indexName, final String typeName, final String jsonData) throws IOException {
        JsonNode jsonArray = JSONUtils.stringToJsonNode( jsonData );
        if (jsonArray.isArray()) {
            StringBuilder builder = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();
            for (JsonNode jsonObjElem : jsonArray) {
                final String elementJSON = mapper.writeValueAsString( jsonObjElem );
                addBulkLoadObject(indexName, typeName, elementJSON, builder);
            }
            String bulkLoadJSON = builder.toString();
            int numElements = jsonArray.size();
            sendBulkLoadJSON(bulkLoadJSON, numElements);
        } else {
            logger.error("JSON load failed. JSON array expected");
        }
    }
    
    /**
     * <p>
     * Dump an index to JSON
     * </p>
     * <p>
     * Elasticsearch only allows one "type" per index, so the type is not needed in the
     * query.  For example, for an index named bookindex a search can be executed without
     * the type name.
     * </p>
     * <pre>
       GET bookindex/_search
       { "query" : {
           "match_all" : {}
          }  
       }
     * </pre>
     * <pre>
     * {
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
      {
        "_index": "bookindex",
        "_type": "bookinfo",
        "_id": "57d19c4f4aa74811de8c33453d214d06",
        "_score": 1,
        "_source": {
          "title": "Transition",
          "author": "Iain M. Banks",
          "genre": "Science Fiction",
          "publisher": "Orbit",
          "year": "2009",
          "price": "25.99"
        }
      },
      ...
     * </pre>
     * @param indexName the name of the Elasticsearch index
     * @return A JSON String containing the objects stored in the index.
     * @throws IOException 
     */
    public String dumpIndex(final String indexName) throws IOException {
        final int GET_MAX = 10;
        final String SEARCH_SUFFIX = "_search";
        final String TYPE = ""; // no type needed
        String jsonResult = "";
        String matchAllQuery = buildMatchAllQuery();
        String result = HttpService.getDocument(indexName, TYPE, SEARCH_SUFFIX, matchAllQuery);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ArrayNode jsonArrayNode = new ArrayNode( nodeFactory );
        // Get the first n results (where n <= 10). totalAvail is the total number of
        // results that are available
        int totalAvail = JSONUtils.extractJSONObjFromQueryResult(result, jsonArrayNode);
        int startIx = GET_MAX;
        while (totalAvail > jsonArrayNode.size()) {
            int numLeft = totalAvail - jsonArrayNode.size();
            int fetchSize = Math.min(GET_MAX, numLeft);
            String newQuery = buildFromMatchAllQuery(startIx, fetchSize);
            result = HttpService.getDocument(indexName, TYPE, SEARCH_SUFFIX, newQuery);
            JSONUtils.extractJSONObjFromQueryResult(result, jsonArrayNode);
            startIx = startIx + GET_MAX;
        }
        if (jsonArrayNode.size() > 0) {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter writer = new StringWriter();
            com.fasterxml.jackson.core.JsonGenerator generator = jsonFactory.createGenerator(writer);
            ObjectMapper mapper = new ObjectMapper();
            generator.useDefaultPrettyPrinter();
            mapper.writeTree(generator, jsonArrayNode);
            jsonResult = writer.toString();
            jsonResult = jsonResult + "\n";
        }
        return jsonResult;
    }
    
    
    /**
     * <p>
     * Check whether an Elasticsearch index exists.
     * </p>
     * <p>
     * The operation to check for an index is:
     * </p>
     * <pre>
     *   HEAD indexName
     * </pre>
     * <blockquote>
     * The HTTP status code indicates if the index exists or not. A 404 means it does not exist, and 200 means it does.
     * </blockquote>
     * 
     * @param indexName the name of the index
     * @return true if the index exists, false otherwise.
     */
    public boolean indexExists( final String indexName ) {
        boolean hasIndex = false;
        if (indexName != null && indexName.length() > 0) {
            int statusCode = HttpService.head(indexName);
            hasIndex = (statusCode == 200);
        }
        return hasIndex;
    }
}
