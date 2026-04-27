java
package com.ning.metrics.eventtracker;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.metrics.serialization.writer.CallbackHandler;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpSenderTest
{
    private HttpSender sender;
    private AsyncHttpClient client;
    private BlockingQueue<Runnable> workers;
    private CallbackHandler handler;
    private File file;
    private HttpJob job;
    @Before
    public void setup() throws IOException
    {
        client = mock(AsyncHttpClient.class);
        workers = new LinkedBlockingQueue<Runnable>();
        handler = mock(CallbackHandler.class);
        file = File.createTempFile("test", ".tmp");
        sender = new HttpSender(client, workers);

    }
    @Test
    public void testSendSuccess() throws Exception
    {
        sender.send(file, handler);
    }
}