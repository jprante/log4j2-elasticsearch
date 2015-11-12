/**
 *    Copyright 2014 Jörg Prante
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbib.logging.log4j2;

import org.elasticsearch.ElasticsearchIllegalStateException;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchTransportClient {

    private final Client client;

    private final BulkProcessor bulkProcessor;

    private final String index;

    private final String type;

    public ElasticsearchTransportClient(Client client, String index, String type,
                                        int maxActionsPerBulkRequest,
                                        int maxConcurrentBulkRequests,
                                        ByteSizeValue maxVolumePerBulkRequest,
                                        TimeValue flushInterval) {
        this.client = client;
        this.index = index;
        this.type = type;
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest requst, Throwable failure) {
            }
        };
        BulkProcessor.Builder builder = BulkProcessor.builder(client, listener)
                .setBulkActions(maxActionsPerBulkRequest)
                .setConcurrentRequests(maxConcurrentBulkRequests)
                .setFlushInterval(flushInterval);
        if (maxVolumePerBulkRequest != null) {
            builder.setBulkSize(maxVolumePerBulkRequest);
        }
        this.bulkProcessor = builder.build();
    }

    public ElasticsearchTransportClient index(Map<String, Object> source) {
        if (((TransportClient)client).connectedNodes().isEmpty()) {
            throw new ElasticsearchIllegalStateException("client is disconnected");
        }

        String index = this.index.indexOf('\'') < 0 ? this.index : getIndexNameDateFormat(this.index).format(new Date());
        bulkProcessor.add(new IndexRequest(index).type(type).create(false).source(source));

        return this;
    }

    public void close() {
        bulkProcessor.close();
        client.close();
    }

    private static final ThreadLocal<Map<String, SimpleDateFormat>> df = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        public Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<String, SimpleDateFormat>();
        }
    };

    private SimpleDateFormat getIndexNameDateFormat(String index) {
        Map<String, SimpleDateFormat> formatters = df.get();
        SimpleDateFormat formatter = formatters.get(index);
        if (formatter == null) {
            formatter = new SimpleDateFormat();
            formatter.applyPattern(index);
            formatters.put(index, formatter);
        }
        return formatter;
    }
}
