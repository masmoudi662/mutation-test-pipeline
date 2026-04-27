java
package com.expedia.www.haystack.client.dispatchers.clients;

import com.expedia.www.haystack.client.Span;
import com.expedia.www.haystack.client.dispatchers.formats.Format;
import com.expedia.www.haystack.client.dispatchers.formats.ProtoBufFormat;
import com.expedia.www.haystack.remote.clients.BaseHttpClient;
import com.expedia.www.haystack.remote.clients.ClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpCollectorClientTest {

    @Mock
    private Span mockSpan;

    @Mock
    private ProtoBufFormat mockFormat;

    private HttpCollectorClient client;

    @Before
    public void setUp() {
        client = new HttpCollectorClient();
        client.format = mockFormat;
    }

    @Test
    public void testSend() throws Exception {
        byte[] spanBytes = new byte[]{1, 2, 3};

        when(mockFormat.format(mockSpan)).thenReturn(com.google.protobuf.ByteString.copyFrom(spanBytes));
        client.setHttpClient(new MockHttpClient(200));
        assertTrue(client.send(mockSpan));
    }

    private static class MockHttpClient extends org.apache.http.impl.client.CloseableHttpClient {
        private int statusCode;

        public MockHttpClient(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        protected org.apache.http.client.methods.CloseableHttpResponse doExecute(org.apache.http.HttpHost target, org.apache.http.HttpRequest request, org.apache.http.protocol.HttpContext context) {
            return new MockHttpResponse(statusCode);
        }

        @Override
        public void close() {
        }
    }

    private static class MockHttpResponse implements org.apache.http.client.methods.CloseableHttpResponse {
        private int statusCode;

        public MockHttpResponse(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public org.apache.http.StatusLine getStatusLine() {
            return new org.apache.http.message.BasicStatusLine(new org.apache.http.ProtocolVersion("HTTP", 1, 1), statusCode, "OK");
        }

        @Override
        public void close() {
        }

        @Override
        public org.apache.http.HttpEntity getEntity() {
            return null;
        }

        @Override
        public java.util.Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(java.util.Locale loc) {
        }

        @Override
        public org.apache.http.ProtocolVersion getProtocolVersion() {
            return null;
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public org.apache.http.Header[] getHeaders(String name) {
            return new org.apache.http.Header[0];
        }

        @Override
        public org.apache.http.Header getFirstHeader(String name) {
            return null;
        }

        @Override
        public org.apache.http.Header getLastHeader(String name) {
            return null;
        }

        @Override
        public org.apache.http.Header[] getAllHeaders() {
            return new org.apache.http.Header[0];
        }

        @Override
        public void addHeader(org.apache.http.Header header) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void setHeader(org.apache.http.Header header) {
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void setHeaders(org.apache.http.Header[] headers) {
        }

        @Override
        public void removeHeader(org.apache.http.Header header) {
        }

        @Override
        public void removeHeader(String name) {
        }

        @Override
        public org.apache.http.HeaderIterator headerIterator() {
            return null;
        }

        @Override
        public org.apache.http.HeaderIterator headerIterator(String name) {
            return null;
        }

        @Override
        public org.apache.http.params.HttpParams getParams() {
            return null;
        }

        @Override
        public void setParams(org.apache.http.params.HttpParams params) {
        }
    }
}