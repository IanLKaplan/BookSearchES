# BookSearchES
A Java demonstration application that uses the Amazon Elasticsearch Service, Spring Boot and Spring MVC

<h3>Introduction</h3>
            <p>
                This article discusses how the open source Elasticsearch database and the Amazon Web Services (AWS)
                Elasticsearch Service can be used
                as the foundation for the type of product search that is used by on-line shopping sites.
            </p>
            <p>
                To illustrate this application of Elasticsearch, this article discusses a demonstration application, written
                in Java. This application is implemented using the Spring framework (Spring Boot and Spring MVC).
            </p>
            <p>
                The Java code for this demonstration application is published on GitHub
                (<a href="https://github.com/IanLKaplan/BookSearchES">https://github.com/IanLKaplan/BookSearchES</a>)
                under the Apache 2 software license.
            </p>
            <p>
                This application is an expanded version of a similar demonstration application that uses the AWS DynamoDB database
                (see <a href="spring_and_dynamodb.html">Spring and DynamoDB</a> and the GitHub repository
                <a href="https://github.com/IanLKaplan/booksearch">https://github.com/IanLKaplan/booksearch</a>)
            </p>
            <h3>
                Faceted Search
            </h3>
            <p>
                When you shop at a web site like Amazon or Lands End you are usually using search operations to find the
                products
                that you may be interested in purchasing. For example, if you are thinking of purchasing a tablet computer you might
                follows down
                the Amazon categories
            </p>
            <div>
                Electronics ==&gt; Computers &amp; Accessories ==&gt;
                Computers &amp; Tablets ==&gt; Tablets
            </div>
            <p>
                You can further select the tablet you are interested in by the operating system and the tablet size (in
                inches).
            </p>
            <p>
                These search categories (e.g., 10 to 10.9 inch tablets) are sometimes referred to as facets. Search that displays these
                facets is referred to as faceted search.
            </p>
            <p>
                        Some web sites display the categories (facets) with an associated count. This can be seen in the screen
                        capture from an on-line clothing retailer. If a category has a large number of items associated with it,
                        this tells the user that they need to use more detailed selection to find the items they are interested in.
                    </p>
                    <p>
                        Often unstructured search is used to find an item on a retail web site.  For example, if you are searching
                        for a "G8" LED bulb to replace a halogen bulb, you might search for "<tt>g8 led bulb</tt>" instead of
                        trying to find the Amazon category for LED light bulbs.
                    </p>
                    <p>
                        When designing the system architecture for a shopping site a database should be choosen that supports
                        both faceted search and unstructured search (the search for the "G8" LED bulb). Elasticsearch is built
                        on top of the Apache Lucene database, which is designed to support text search.
                    </p>
                     <h3>
                Elasticsearch
            </h3>
            <p>
                Elasticsearch is an open source (Apache license) database that is based on the Lucene full text indexing system.
            </p>
            <p>
                Elasticsearch is designed to support large scale data sets. The Elasticsearch index can be "sharded" across multiple
                processors. This allows the Elasticsearch processing load to be distributed over multiple Elastricsearch "nodes".
            </p>
            <p>
                An Elasticsearch instance will have one or more indices and each index will have an associated data type.
                The Elasticsearch data type is equivalent to a database table schema for a relational database. The Elasticsearch
                type definition is referred to as a mapping.
            </p>
            <p>
                An Elasticsearch mapping is flexible and additional fields can be added to a mapping without affecting the
                existing data (although existing data elements will not have data defined for the new field).
            </p>
            <p>
                The mapping (schema definition) associated with the Elasticsearch bookindex/book info index/type is shown below.
            </p>
            <p>
                The <tt>text</tt> fields can be searched for arbitrary strings. For example, searching for the word "venice" will
                in the bookinfo "title" field will return all of the bookinfo entries that contain the world "venice" in the tile.
            </p>
            <p>
                The keyword type defines a field that is searched by exact match. Searches on the "genre" field must exactly
                match the strings in that field to return a bookinfo element. For example, a search on "Science Fiction"
                and a search on "Fiction" return different data elements. For keyword fields, capitalization matters.
                For text fields, capitalization is ignored.
            </p>
            <p>
                Some fields, like "publisher" are stored as both text fields and keyword fields. When data is stored in the
                "publisher" field, it will update both the text and the keyword parts of the field.
            </p>
            <!-- HTML generated using hilite.me -->
            <div
                style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><pre
                style="margin: 0; line-height: 125%">{
  <span style="color: #007700">&quot;bookindex&quot;</span>: {
    <span style="color: #007700">&quot;mappings&quot;</span>: {
      <span style="color: #007700">&quot;bookinfo&quot;</span>: {
        <span style="color: #007700">&quot;_all&quot;</span>: {
          <span style="color: #007700">&quot;enabled&quot;</span>: <span
                    style="color: #008800; font-weight: bold">false</span>
        },
        <span style="color: #007700">&quot;properties&quot;</span>: {
          <span style="color: #007700">&quot;author&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;text&quot;</span>
          },
          <span style="color: #007700">&quot;author_last_name&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;keyword&quot;</span>
          },
          <span style="color: #007700">&quot;genre&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;keyword&quot;</span>
          },
          <span style="color: #007700">&quot;price&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;float&quot;</span>
          },
          <span style="color: #007700">&quot;publisher&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;text&quot;</span>,
            <span style="color: #007700">&quot;fields&quot;</span>: {
              <span style="color: #007700">&quot;keyword&quot;</span>: {
                <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;keyword&quot;</span>
              }
            }
          },
          <span style="color: #007700">&quot;title&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;text&quot;</span>,
            <span style="color: #007700">&quot;fields&quot;</span>: {
              <span style="color: #007700">&quot;keyword&quot;</span>: {
                <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;keyword&quot;</span>
              }
            }
          },
          <span style="color: #007700">&quot;year&quot;</span>: {
            <span style="color: #007700">&quot;type&quot;</span>: <span style="background-color: #fff0f0">&quot;date&quot;</span>,
            <span style="color: #007700">&quot;format&quot;</span>: <span style="background-color: #fff0f0">&quot;YYYY&quot;</span>
          }
        }
      }
    }
  }
}
</pre>
            </div> <!-- JSON mapping -->
             <h3>
                The Amazon Elasticsearch Service
            </h3>
            <p>
                The Amazon Elasticsearch Service provides hosted instances of Elasticsearch. This allows Elasticsearch
                to be configured from the Elasticsearch Service web page. This avoids the complexity of directly configuring
                an Elasticsearch cluster.
            </p>
            <p>
                The Elasticsearch Search service runs a recent version of Elasticsearch and allows the user to upgrade the
                version on demand.
            </p>
            <p>
                The Elasticsearch Service comes with an instance of the Kibana. The Kibana tool allows you to test
                Elastic search queries against the data in your Elasticsearch Service instance. This feature was
                very useful when developing the queries used by the Book Search application.
                </p>
            <h3>
                Elasticsearch is a REST Service
            </h3>
            <p>
                Elasticsearch is a Web service and all communication with Elasticsearch takes place over HTTP using the
                 <a href="https://spring.io/understanding/REST">REST operations</a> GET, PUT, POST and DELETE.
                For example, the Elasticsearch query below uses a GET operation.
            </p>
            <!-- HTML generated using hilite.me --><div style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><pre style="margin: 0; line-height: 125%"><span style="color: #FF0000; background-color: #FFAAAA">GET</span> <span style="color: #FF0000; background-color: #FFAAAA">index/type/_search</span>
{
  <span style="color: #007700">&quot;query&quot;</span>: {
     <span style="color: #007700">&quot;bool&quot;</span>: {
        <span style="color: #007700">&quot;filter&quot;</span>: {
           <span style="color: #007700">&quot;match&quot;</span>: {
              <span style="color: #007700">&quot;author&quot;</span>: <span style="background-color: #fff0f0">&quot;gibson&quot;</span>
           }
        }
     }
   }
}
</pre></div>
            <p>
                The query result is returned in the HTTP response as a JSON structure.
            </p>
            <p>
                HTTP POST operations have an entity (e.g., a string) argument associated with the operation.
                GET operations were originally designed to fetch data associated with a URL (URI). The GET operation used
                to query Elasticsearch are an extension of the standard HTTP GET and include an entity (see the HttpGetWithEntity
                object in the associated Book Search application GitHub source code).
            </p>
            <h4>
                Elasticsearch and Security
            </h4>
            <p>
                An Amazon Elasticsearch Service instance (domain) can be configured as either a public access end-point,
                which can be accessed from the Internet, or as an Amazon Virtual Private Cloud (VPC) accessible service.
            </p>
            <p>
                A VPC service has higher security, but debugging and monitoring can be more difficult. When the
                Elasticsearch Service is configured within a VPC, the HTTP transactions are simpler, since they
                do not have to be signed and authorized.
            </p>
            <p>
                The Book Search application is designed to run with an Internet accessible Elasticsearch Service end-point.
                This makes debugging and testing easier and allows the Book Service application to run on either
                my local computer system or an Amazon Elastic Beanstalk server. Access to the Elasticsearch Service end-point
                can be "locked down" to a single IP address or an IP address range for increased security.
            </p>
            <h4>
                Signed HTTP
            </h4>
            <p>
                The Book Search application uses the Java
                <a href="https://hc.apache.org/httpcomponents-client-ga/">Apache HTTP Client</a> library for communication
                with the Elasticsearch Service.
            </p>
            <p>
                Amazon has published documentation on how to build signed HTTP transactions based on the Apache HTTP Client.
                Unfortunately, this documentation can be difficult to find. The Book Search application includes
                <tt>AWSRequestSigningApacheInterceptor</tt> class which is at the core of these transactions. The Java code
                below shows how this class is used to build signed HTTP objects.
            </p>
            <!-- HTML generated using hilite.me --><div style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><pre style="margin: 0; line-height: 125%">   <span style="color: #008800; font-weight: bold">protected</span> <span style="color: #008800; font-weight: bold">static</span> CloseableHttpClient <span style="color: #0066BB; font-weight: bold">signedClient</span><span style="color: #333333">()</span> <span style="color: #333333">{</span>
        AWS4Signer signer <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> AWS4Signer<span style="color: #333333">();</span>
        signer<span style="color: #333333">.</span><span style="color: #0000CC">setServiceName</span><span style="color: #333333">(</span> SERVICE_NAME <span style="color: #333333">);</span>
        signer<span style="color: #333333">.</span><span style="color: #0000CC">setRegionName</span><span style="color: #333333">(</span> region<span style="color: #333333">.</span><span style="color: #0000CC">getName</span><span style="color: #333333">()</span> <span style="color: #333333">);</span>
        AWSCredentials credentials <span style="color: #333333">=</span> getCredentials<span style="color: #333333">(</span>ES_ID<span style="color: #333333">,</span> ES_KEY<span style="color: #333333">);</span>
        AWSCredentialsProvider credProvider <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> AWSStaticCredentialsProvider<span style="color: #333333">(</span> credentials <span style="color: #333333">);</span>
        HttpRequestInterceptor interceptor <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> AWSRequestSigningApacheInterceptor<span style="color: #333333">(</span>SERVICE_NAME<span style="color: #333333">,</span> signer<span style="color: #333333">,</span> credProvider<span style="color: #333333">);</span>
        <span style="color: #008800; font-weight: bold">return</span> HttpClients<span style="color: #333333">.</span><span style="color: #0000CC">custom</span><span style="color: #333333">()</span>
                <span style="color: #333333">.</span><span style="color: #0000CC">addInterceptorLast</span><span style="color: #333333">(</span>interceptor<span style="color: #333333">)</span>
                <span style="color: #333333">.</span><span style="color: #0000CC">build</span><span style="color: #333333">();</span>
    <span style="color: #333333">}</span>
    <span style="color: #008800; font-weight: bold">protected</span> <span style="color: #008800; font-weight: bold">static</span> String <span style="color: #0066BB; font-weight: bold">sendHTTPTransaction</span><span style="color: #333333">(</span> HttpUriRequest request <span style="color: #333333">)</span> <span style="color: #333333">{</span>
        String httpResult <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">null</span><span style="color: #333333">;</span>
        CloseableHttpClient httpClient <span style="color: #333333">=</span> signedClient<span style="color: #333333">();</span>
        <span style="color: #008800; font-weight: bold">try</span> <span style="color: #333333">{</span>
            HttpResponse response <span style="color: #333333">=</span> httpClient<span style="color: #333333">.</span><span style="color: #0000CC">execute</span><span style="color: #333333">(</span>request<span style="color: #333333">);</span>
            BufferedReader bufferedReader <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> BufferedReader<span style="color: #333333">(</span><span style="color: #008800; font-weight: bold">new</span> InputStreamReader<span style="color: #333333">(</span>response<span style="color: #333333">.</span><span style="color: #0000CC">getEntity</span><span style="color: #333333">().</span><span style="color: #0000CC">getContent</span><span style="color: #333333">()));</span>
            httpResult <span style="color: #333333">=</span> IOUtils<span style="color: #333333">.</span><span style="color: #0000CC">toString</span><span style="color: #333333">(</span>bufferedReader<span style="color: #333333">);</span>
        <span style="color: #333333">}</span> <span style="color: #008800; font-weight: bold">catch</span> <span style="color: #333333">(</span>IOException e<span style="color: #333333">)</span> <span style="color: #333333">{</span>
            logger<span style="color: #333333">.</span><span style="color: #0000CC">error</span><span style="color: #333333">(</span><span style="background-color: #fff0f0">&quot;HTTP Result error: &quot;</span> <span style="color: #333333">+</span> e<span style="color: #333333">.</span><span style="color: #0000CC">getLocalizedMessage</span><span style="color: #333333">());</span>
        <span style="color: #333333">}</span>
        <span style="color: #008800; font-weight: bold">return</span> httpResult<span style="color: #333333">;</span>
    <span style="color: #333333">}</span>
</pre></div>
            <p>
                The code below shows how the <tt>sendHTTPTransaction()</tt> function is used to send an HTTP get with an
                entity argument (e.g., the type of GET that is used for a search operation).
            </p>
            <!-- HTML generated using hilite.me --><div style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><pre style="margin: 0; line-height: 125%"><span style="color: #008800; font-weight: bold">public</span> <span style="color: #008800; font-weight: bold">static</span> String <span style="color: #0066BB; font-weight: bold">getDocument</span><span style="color: #333333">(</span><span style="color: #008800; font-weight: bold">final</span> String index<span style="color: #333333">,</span> <span style="color: #008800; font-weight: bold">final</span> String type<span style="color: #333333">,</span> <span style="color: #008800; font-weight: bold">final</span> String suffix<span style="color: #333333">,</span> <span style="color: #008800; font-weight: bold">final</span> String jsonPayload<span style="color: #333333">)</span> <span style="color: #333333">{</span>
        String responseString <span style="color: #333333">=</span> <span style="background-color: #fff0f0">&quot;&quot;</span><span style="color: #333333">;</span>
        String url <span style="color: #333333">=</span> buildURL<span style="color: #333333">(</span>index<span style="color: #333333">,</span> type<span style="color: #333333">,</span> suffix<span style="color: #333333">);</span>
        <span style="color: #008800; font-weight: bold">try</span> <span style="color: #333333">{</span>
            HttpGetWithEntity get <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> HttpGetWithEntity<span style="color: #333333">(</span> url <span style="color: #333333">);</span>
            get<span style="color: #333333">.</span><span style="color: #0000CC">setHeader</span><span style="color: #333333">(</span><span style="background-color: #fff0f0">&quot;Content-type&quot;</span><span style="color: #333333">,</span> <span style="background-color: #fff0f0">&quot;application/json&quot;</span><span style="color: #333333">);</span>
            StringEntity stringEntity <span style="color: #333333">=</span> <span style="color: #008800; font-weight: bold">new</span> StringEntity<span style="color: #333333">(</span> jsonPayload<span style="color: #333333">,</span> StandardCharsets<span style="color: #333333">.</span><span style="color: #0000CC">UTF_8</span><span style="color: #333333">);</span>
            get<span style="color: #333333">.</span><span style="color: #0000CC">setEntity</span><span style="color: #333333">(</span>stringEntity<span style="color: #333333">);</span>
            responseString <span style="color: #333333">=</span> sendHTTPTransaction<span style="color: #333333">(</span> get <span style="color: #333333">);</span>
        <span style="color: #333333">}</span> <span style="color: #008800; font-weight: bold">catch</span> <span style="color: #333333">(</span>Exception e<span style="color: #333333">)</span> <span style="color: #333333">{</span>
            logger<span style="color: #333333">.</span><span style="color: #0000CC">error</span><span style="color: #333333">(</span><span style="background-color: #fff0f0">&quot;HttpGet with entity failed: &quot;</span> <span style="color: #333333">+</span> e<span style="color: #333333">.</span><span style="color: #0000CC">getLocalizedMessage</span><span style="color: #333333">());</span>
        <span style="color: #333333">}</span>
        <span style="color: #008800; font-weight: bold">return</span> responseString<span style="color: #333333">;</span>
    <span style="color: #333333">}</span>
</pre></div>
         <p>
             For more details, please refer to the Java source code in the associated GitHub repository.
         </p>
            <h3>
                Elasticsearch Documentation
            </h3>
            <p>
                One of the challenges that you will face if you decide to use Elasticsearch is the documentation. In
                developing the Book Search application and the associated Elasticsearch support code, I relied on three
                documentation sources:
            </p>
            <ol>
                <li>
                    <i>Elasticsearch in Action</i> by Radu Gheorghe, Matthew Lee Hinman, and Roy Russo
                    Manning Publications, November 2015</li>
                <li>
                    The <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html"><i>Elasticsearch Reference</i></a>
                    published on the <a href="http://elastic.co">elastic.co</a> web site.
                </li>
                <li>
                    Lost of Web searches to answer questions that I could not find answers to in the above references.
                </li>
            </ol>
            <p>
                The book <i>Elasticsearch in Action</i> is very useful in understanding the capabilities of Elasticsearch
                and its architecture. As with most Manning books, the writing quality is high and I recommend reading the
                first five chapters of this book.
            </p>
            <p>
                There are several problems with <i>Elasticsearch in Action</i> when it comes to writing software that
                uses Elasticsearch. The book is based on Elasticsearch version 2.X. At the time this web page was written
                Elasticsearch is on version 6.X.
            </p>
            <p>
                There have been significant changes in Elasticsearch between 2.X and 6.X. Some of the queries and other
                operations described in the book do not work with Elasticsearch version 6.X.
            </p>
            <p>
                The Elasticsearch architecture has also changed. In <i>Elasticsearch in Action</i> the authors state that
                there can be multiple types per index. Later versions of Elasticsearch allow one type per index, making
                indices and types equivalent.
            </p>
            <p>
                For the Java developer there are few resources to guide the development of Java code for Elasticsearch
                outside of the Amazon documentation (which is often incomplete and fragemented). In <i>Elasticsearch in Action</i>
                most operations are described in terms of command-line <tt>curl</tt> operations. A
                search operation, from <i>Elasticsearch in Action</i>, is shown below.
            </p>
            <!-- HTML generated using hilite.me --><div style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><pre style="margin: 0; line-height: 125%"><span style="color: #FF0000; background-color: #FFAAAA">%</span> <span style="color: #FF0000; background-color: #FFAAAA">curl</span> <span style="color: #FF0000; background-color: #FFAAAA">&#39;localhost:</span><span style="color: #0000DD; font-weight: bold">9200</span><span style="color: #FF0000; background-color: #FFAAAA">/get-together/group/_search?pretty&#39;</span> <span style="color: #FF0000; background-color: #FFAAAA">-d</span> <span style="color: #FF0000; background-color: #FFAAAA">&#39;</span>{
  <span style="color: #007700">&quot;query&quot;</span>: {
    <span style="color: #007700">&quot;query_string&quot;</span>: {
      <span style="color: #007700">&quot;query&quot;</span>: <span style="background-color: #fff0f0">&quot;elasticsearch&quot;</span>
    }
  }
}<span style="color: #FF0000; background-color: #FFAAAA">&#39;</span>
</pre></div>
<p>
               I hope that this article and the associated code on GitHub will provide a useful resource for Java
               developers. The HTTP and Elasticsearch code is independent of the Book Search demonstration application
               and you may freely use it in your own code.
           </p>
<h3>
            Spring Boot and Spring MVC
            </h3>
            <p>
            The Book Search application is built using the Spring framework. You should be able to clone the code
            and import it as a Spring Tool Suite project (Spring Tool Suite is a version of Eclipse customized for
            Spring. The project uses Maven to load the necessary Java libraries.
            </p>
            <h3>
            The Book Search Application
            </h3>
            <p>
            The Book Search application can be run on your local system. Before you do this, however, you will need
            to configure an AWS Elasticsearch Service domain. You will need to get the ID and secret key for accessing
            your domain. The ID and secret key can then be added to the IElassticsearch.java Interface.
            </p>
            <p>
            The books.json file contains sample data that can be loaded into the application. The JSON file can be loaded
            with the LoadESFromJSON application.
            </p>
