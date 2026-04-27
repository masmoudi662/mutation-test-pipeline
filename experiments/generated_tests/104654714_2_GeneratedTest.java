java
package org.apache.nifi.reporting.prometheus.metrics;

import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricsServiceTest {

    @Test
    public void testGetMetricsWithAppendPgId() {
        MetricsService metricsService = new MetricsService();
        ProcessGroupStatus status = mock(ProcessGroupStatus.class);

        when(status.getFlowFilesReceived()).thenReturn(100);
        when(status.getBytesReceived()).thenReturn(1000L);
        when(status.getFlowFilesSent()).thenReturn(50);
        when(status.getBytesSent()).thenReturn(500L);
        when(status.getQueuedCount()).thenReturn(20);
        when(status.getQueuedContentSize()).thenReturn(200L);
        when(status.getBytesRead()).thenReturn(300L);
        when(status.getBytesWritten()).thenReturn(400L);
        when(status.getActiveThreadCount()).thenReturn(5);
        when(status.getProcessingNanos()).thenReturn(1000000000L);

        Map<String, String> metrics = metricsService.getMetrics(status, true);

        assertEquals(10, metrics.size());
        assertEquals("100", metrics.get("flow.files.received.pgid"));
        assertEquals("1000", metrics.get("bytes.received.pgid"));
        assertEquals("50", metrics.get("flow.files.sent.pgid"));
        assertEquals("500", metrics.get("bytes.sent.pgid"));
        assertEquals("20", metrics.get("flow.files.queued.pgid"));
        assertEquals("200", metrics.get("bytes.queued.pgid"));
        assertEquals("300", metrics.get("bytes.read.pgid"));
        assertEquals("400", metrics.get("bytes.written.pgid"));
        assertEquals("5", metrics.get("active.threads.pgid"));
        assertEquals("1000000000", metrics.get("total.task.duration.nanos.pgid"));
    }

    @Test
    public void testGetMetricsWithoutAppendPgId() {
        MetricsService metricsService = new MetricsService();
        ProcessGroupStatus status = mock(ProcessGroupStatus.class);

        when(status.getFlowFilesReceived()).thenReturn(100);
        when(status.getBytesReceived()).thenReturn(1000L);
        when(status.getFlowFilesSent()).thenReturn(50);
        when(status.getBytesSent()).thenReturn(500L);
        when(status.getQueuedCount()).thenReturn(20);
        when(status.getQueuedContentSize()).thenReturn(200L);
        when(status.getBytesRead()).thenReturn(300L);
        when(status.getBytesWritten()).thenReturn(400L);
        when(status.getActiveThreadCount()).thenReturn(5);
        when(status.getProcessingNanos()).thenReturn(1000000000L);

        Map<String, String> metrics = metricsService.getMetrics(status, false);

        assertEquals(10, metrics.size());
        assertEquals("100", metrics.get("flow.files.received"));
        assertEquals("1000", metrics.get("bytes.received"));
        assertEquals("50", metrics.get("flow.files.sent"));
        assertEquals("500", metrics.get("bytes.sent"));
        assertEquals("20", metrics.get("flow.files.queued"));
        assertEquals("200", metrics.get("bytes.queued"));
        assertEquals("300", metrics.get("bytes.read"));
        assertEquals("400", metrics.get("bytes.written"));
        assertEquals("5", metrics.get("active.threads"));
        assertEquals("1000000000", metrics.get("total.task.duration.nanos"));
    }
}