package org.apache.logging.log4j.nosql.appender;

import org.junit.Test;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NoSqlAppenderTest {
    @Test
    public void testNoProvider() {
        final NoSqlAppender appender = NoSqlAppender.createAppender("myName01", null, null, null, null);
        assertNull("The appender should be null.", appender);
    }

    @Test
    public void testProvider() {
        @SuppressWarnings("unchecked")
        final NoSqlProvider<?> provider = createStrictMock(NoSqlProvider.class);
        replay(provider);
        final NoSqlAppender appender = NoSqlAppender.createAppender("myName01", null, null, null, provider);
        assertNotNull("The appender should not be null.", appender);
        assertEquals("The toString value is not correct.",
                "myName01{ manager=noSqlManager{ description=myName01, bufferSize=0, provider=" + provider + " } }",
                appender.toString());
        appender.stop();
        verify(provider);
    }

    @Test
    public void testProviderBuffer() {
        @SuppressWarnings("unchecked")
        final NoSqlProvider<?> provider = createStrictMock(NoSqlProvider.class);
        replay(provider);
        final NoSqlAppender appender = NoSqlAppender.createAppender("anotherName02", null, null, "25", provider);
        assertNotNull("The appender should not be null.", appender);
        assertEquals("The toString value is not correct.",
                "anotherName02{ manager=noSqlManager{ description=anotherName02, bufferSize=25, provider=" + provider
                        + " } }", appender.toString());
        appender.stop();
        verify(provider);
    }
}