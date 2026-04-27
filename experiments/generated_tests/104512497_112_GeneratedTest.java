java
package com.facebook.battery.reporter.cpu;

import com.facebook.battery.metrics.cpu.CpuFrequencyMetrics;
import com.facebook.battery.reporter.core.SystemMetricsReporter;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JSONObject.class})
public class CpuFrequencyMetricsReporterTest {

  private CpuFrequencyMetricsReporter reporter;

  @Mock
  private CpuFrequencyMetrics metrics;

  @Mock
  private SystemMetricsReporter.Event event;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    reporter = new CpuFrequencyMetricsReporter();
  }

  @Test
  public void testReportTo_withData() throws Exception {
    JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
    when(metrics.toJSONObject()).thenReturn(jsonObject);
    when(jsonObject.length()).thenReturn(1);
    when(jsonObject.toString()).thenReturn("test_json");

    reporter.reportTo(metrics, event);

    verify(event).add(CpuFrequencyMetricsReporter.CPU_TIME_IN_STATE_S, "test_json");
  }

  @Test
  public void testReportTo_noData() throws Exception {
    JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
    when(metrics.toJSONObject()).thenReturn(jsonObject);
    when(jsonObject.length()).thenReturn(0);

    reporter.reportTo(metrics, event);

    // Verify that add is not called
  }

  @Test
  public void testReportTo_nullJsonObject() {
    when(metrics.toJSONObject()).thenReturn(null);
    reporter.reportTo(metrics, event);
  }
}