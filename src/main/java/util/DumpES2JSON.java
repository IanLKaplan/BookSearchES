/** \file
 * 
 * Aug 30, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import booksearch_es.service.ElasticsearchService;

/**
 * <h4>
 * DumpES2JSON
 * </h4>
 * <p>
 * Dump an Elasticsearch index to JSON and write the JSON to a file.
 * </p>
 * <p>
 * Arguments: there are two arguments for this application, the index name and the path of the 
 * file that the JSON should be written to.
 * </p>
 * <p>
 * Aug 30, 2018
 * </p>
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class DumpES2JSON {
    private final static String SEARCH_SUFFIX = "_search";
    
    protected void usage() {
        System.err.println("usage: " + this.getClass().getName() + " [index] [output file path]");
    }
    
    protected void dumpES(ElasticsearchService esService, final String indexName, Writer writer) {
        try {
            String json = esService.dumpIndex(indexName);
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Error dumping the Elasticsearch index: " + e.getLocalizedMessage());
        }
    }

    protected void application(String[] args) {
        if (args.length == 2) {
            String indexName = args[0];
            String outputPath = args[1];
            // check that we can write to the output file path
            File outputFile = new File( outputPath );
            if (outputFile.getParentFile().canWrite()) {
                ElasticsearchService esService = new ElasticsearchService();
                if (esService.indexExists(indexName)) {
                    FileOutputStream ostream = null;
                    OutputStreamWriter writer = null;
                    try {
                        ostream = new FileOutputStream( outputFile );
                        writer = new OutputStreamWriter( ostream );
                        dumpES(esService, indexName, writer);
                    } catch (FileNotFoundException e) {
                        System.err.println("Error allocating the FileOutputStream: " + e.getLocalizedMessage());
                    }
                    finally {
                        // The order that the write and stream are closed matters. If the stream is closed before the 
                        // write, some data may not be written to disk.
                        if (writer != null) {
                            try { writer.close(); } catch (IOException e) {}
                        }
                        if (ostream != null) {
                            try { ostream.close(); } catch (IOException e) {}
                        }
                    }
                } else {
                    System.err.println("The " + indexName + " does not exist for this domain");
                }
            } else {
                System.err.println("Cannot write to " + outputPath );
            }
        } else {
            usage();
        }
    }
    
    public static void main(String[] args) {
        // Initialize the Apache Log4j logger in an attempt to get the damn HTTP debug messages to STFU.
        org.apache.log4j.BasicConfigurator.configure();
        DumpES2JSON app = new DumpES2JSON();
        app.application(args);
    }

}
