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

import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.nosql.appender.NoSqlConnection;
import org.apache.logging.log4j.nosql.appender.NoSqlObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ElasticsearchConnection implements NoSqlConnection<Map<String, Object>, ElasticsearchObject> {

    private final ElasticsearchTransportClient client;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public ElasticsearchConnection(final ElasticsearchTransportClient client) {
        this.client = client;
    }

    @Override
    public ElasticsearchObject createObject() {
        return new ElasticsearchObject();
    }

    @Override
    public ElasticsearchObject[] createList(final int length) {
        return new ElasticsearchObject[length];
    }

    @Override
    public void insertObject(final NoSqlObject<Map<String, Object>> object) {
        try {
            client.index(object.unwrap());
        } catch (Exception e) {
            throw new AppenderLoggingException("failed to write log event to Elasticsearch: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            client.close();
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }
}
