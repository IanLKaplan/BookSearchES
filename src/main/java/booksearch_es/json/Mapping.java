/** \file
 * 
 * Jun 25, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.json;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

final public class Mapping {
    
    public final static String TYPE_NAME = "bookinfo";
    
    /**
     * <p>
     * Return the Elasticsearch JSON mapping object for the BookInfo class. The mapping object defines the
     * Elasticsearch record for a given type.
     * </p>
     * <p>
     * To create an Elasticsearch mapping we use:
     * </p>
     * <pre>
     * PUT test
       {
          "mappings" : {
             "type1" : {
                 "properties" : {
                 "field1" : { "type" : "text" }
             }
           }
         }
       }
     * </pre>
     * <p>
     * If "_all" is enabled a query that doesn't specify a field name will be made across all fields. For a
     * bookinfo object, this doesn't make any sense because queries are targeted at specific fields like
     * title or author. To avoid this behavior "_all" is disabled:
     * </p>
     * <pre>
     * "_all": {
             "enabled": false
           }
     * </pre>
     * <p>
     * This not only controls query behavior, but also reduces the size of the index.
     * </p>
     * <p>
     * Note that with Elasticsearch 6.0 there is one type per index (e.g., multiple types in an index are no longer
     * allowed).
     * </p>
     * <p>
     * With Elasticseaerch 5.0 indexing changed as well.  The "index" : "analyzed" or "not_analyzed" (described in 
     * Elasticsearch in Action). Insterad of a "not_analyzed" field the "keyword" type was introduced. This creates a
     * field that contains a keyword version of the text.
     * </p>
     * <pre>
     * https://www.elastic.co/blog/strings-are-dead-long-live-strings
     * </pre>
     * <p>
     * Agreggations can only be performed on the keyword fields (for example, publisher.keyword).
     * </p>
     * <p>
     * Except that this doesn't seem to be true for the AWS version. Given the poor Elasticsearch documentation, it's hard to 
     * tell. In any case, the keyword field is explicitly added in below.
     * </p>
     * <p>
     * For the bookinfo type:
     * </p>
     * <pre>
      {
        "mappings": {
          "bookinfo": {
             "_all": {
             "enabled": false
           },
       "properties": {
          "author": {
            "type": "text"
          },
          "author_last_name": {
            "type": "keyword"
          },
          "genre": {
            "type": "keyword"
          },
          "price": {
            "type": "float"
          },
          "publisher": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword"
              }
            }
          },
          "title": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword"
              }
            }
          },
          "year": {
            "type": "date",
            "format": "YYYY"
          }
        }
      }
  }
}
     * @param prettyPrint if this argument is true, the JSON result is indented for easy reading.
     * @return the JSON for the BookInfo mapping.
     */
    public static String bookInfoMapping(final boolean prettyPrint) {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = null;
        if (prettyPrint) {
            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
            generator = jgf.createGenerator(writer);   
        } else {
            generator = Json.createGenerator(writer);
        }
        generator.writeStartObject(); // {
        generator.writeStartObject("mappings"); // "mappings" {
        generator.writeStartObject(TYPE_NAME); // "bookinfo" : {
        generator.writeStartObject("_all" );      //   "_all : {
        generator.write("enabled", false);        //       "enabled" : false
        generator.writeEnd();                     //           }
        generator.writeStartObject("properties"); // "properties" {
        
        generator.writeStartObject("title");      //     "title" {
        generator.write("type", "text");          //          "type" : "text",
        generator.writeStartObject("fields");     //          "fields : {
        generator.writeStartObject("keyword");    //             "keyword" : {
        generator.write("type", "keyword");          //             "type" : "keyword"
        generator.writeEnd(); //                                  }
        generator.writeEnd(); //                               }
        generator.writeEnd(); //                          }
        
        generator.writeStartObject("author");      //     "author" {
        generator.write("type", "text");          //          "type" : "text"
        generator.writeEnd(); //                           }
        
        generator.writeStartObject("author_last_name"); // "author_last_name" {
        generator.write("type", "keyword");          //       "type" : "keyword"
        generator.writeEnd(); //                           }
        
        generator.writeStartObject("genre");      //     "genre" {
        generator.write("type", "keyword");       //          "type" : "keyword"
        generator.writeEnd(); //                          }
        
        generator.writeStartObject("publisher");      //     "publisher" {
        generator.write("type", "text");          //          "type" : "text",
        generator.writeStartObject("fields");     //          "fields : {
        generator.writeStartObject("keyword");    //             "keyword" : {
        generator.write("type", "keyword");          //             "type" : "keyword"
        generator.writeEnd(); //                                  }
        generator.writeEnd(); //                               }
        generator.writeEnd(); //                             }
        
        generator.writeStartObject("year");      //     "year" {
        generator.write("type", "date");         //          "type" : "date",
        generator.write("format", "YYYY");   //          "format" : "YYYY"  (just the year)
        generator.writeEnd(); //                          }
        
        generator.writeStartObject("price");      //     "price" {
        generator.write("type", "float");          //          "type" : "float"
        generator.writeEnd(); //                          }
        
        generator.writeEnd(); //                      } -- properties
        generator.writeEnd(); //                   }
        generator.writeEnd(); //    }
        generator.writeEnd(); // }
        generator.close();
        return writer.toString();
    }
}
