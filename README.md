# Log4j2 Elasticsearch appender

This NoSql Log4j2 appender logs messages to Elasticsearch. 

It uses an Elasticsearch TransportClient instance with bulk indexing strategy to connect to a remote server 
that is a node of a running Elasticsearch cluster.

In the `log4j2.xml` configuration file, you can specify the following parameters:

`cluster` the Elasticsearch cluster name (default: `elasticsearch`)

`host` the Elasticsearch node in the cluste to connect to (default: `localhost`)

`port` the port of the Elasticsearch node where the transport protocol is available (default: 9300)

`index` the index name of the Elasticsearch cluster to write log messages to (default: `log4j2`). 
The index name may be a date format string like 'log4j2-'yyyyMMdd

`type` the type of the Elasticsearch index to write log messages to (default: `log4j2`)

For advanced usage, there are the following settings:

`sniff` true for all Elasticsearch nodes should be used to connect to, false if only one node should be used (default: `false`)

`timeout` timeout for Elasticsearch cluster ping (default: `30s`)

`maxActionsPerBulkRequest` maximum number of indexing action in a single bulk request (default: `1000`)

`maxConcurrentBulkRequests` maxmimum number of concurrent bulk request (default: 2* number of available CPUs)

`maxVolumePerBulkRequest` maximum volume of a single bulk request (default: `10mb`)

`flushInterval` maximum wait time before a single bulk request is flushed to the Elasticsearch cluster (default: `30s`)

## Log4j2.xml example

    <configuration status="OFF">
        <appenders>
            <NoSql name="elasticsearchAppender">
                <Elasticsearch cluster="elasticsearch" host="localhost" port="9300" index="log4j2" type="log4j2"/>
            </NoSql>
            <NoSql name="elasticsearchTimeAppender">
                <Elasticsearch cluster="elasticsearch" host="localhost" port="9300" index="'log4j2-'yyyyMMdd" type="log4j2"/>
            </NoSql>
        </appenders>
        <loggers>
            <logger name="test">
                <appender-ref ref="elasticsearchAppender"/>
            </logger>
            <logger name="time">
                <appender-ref ref="elasticsearchTimeAppender"/>
            </logger>
            <root level="info">
                <appender-ref ref="elasticsearchAppender"/>
            </root>
        </loggers>
    </configuration>

## Java code example

    Logger logger = LogManager.getLogger("test");
    logger.info("Hello World");

## Indexed log message example

    curl 'localhost:9200/log4j2/_search?pretty'
    {
      "took" : 1,
      "timed_out" : false,
      "_shards" : {
        "total" : 5,
        "successful" : 5,
        "failed" : 0
      },
      "hits" : {
        "total" : 1,
        "max_score" : 1.0,
        "hits" : [ {
          "_index" : "log4j2",
          "_type" : "log4j2",
          "_id" : "dzvP2kbtS8Sr0uEZojMfKg",
          "_score" : 1.0,
          "_source":{"date":"2014-07-18T06:17:38.896Z","contextStack":[],"level":"INFO",
          "marker":null,"thrown":null,"source":{"fileName":"ElasticsearchAppenderTest.java",
          "methodName":"testLog","className":"ElasticsearchAppenderTest","lineNumber":11},
          "loggerName":"test","message":"Hello World","millis":1405664258896,
          "contextMap":{},"threadName":"main"}
        } ]
      }
    }    


# Versions

| Log4j2 Elasticsearch appender   | Elasticssearch Version | Release date |
| --------------------------------| -----------------------|--------------|
| 1.4.0.1                         | 1.4.0                  | Nov 21, 2014 |
| 1.4.0.0                         | 1.4.0                  | Nov 12, 2014 |
| 1.0.0                           | 1.2.2                  | Jul 18, 2014 |


# Installation

    Maven coordinates
    
        <repositories>
            <repository>
                <id>xbib</id>
                <url>http://xbib.org/repository</url>
            </repository>
        </repositories>

        <dependencies>
            <dependency>
                <groupId>org.xbib.logging.log4j2</groupId>
                <artifactId>log4j2-elasticsearch</artifactId>
                <version>1.4.0.1</version>
            </dependency>
        </dependencies>


# Project docs

The Maven project site is available at [Github](http://jprante.github.io/log4j2-elasticsearch)

# License

Log4j2 Elasticsearch Appender

Copyright (C) 2014 Jörg Prante

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

