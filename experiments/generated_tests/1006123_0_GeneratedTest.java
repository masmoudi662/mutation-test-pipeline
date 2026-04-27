java
package com.google.wave.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.wave.api.impl.GsonFactory;
import com.google.wave.api.impl.HttpService;

import junit.framework.TestCase;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.version.HashedVersion;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 *
 */
public class ClientWaveTest extends TestCase {

  private static final WaveId WAVE_ID = WaveId.of("example.com", "wave");
  private static final WaveletId WAVELET_ID = WaveletId.of("example.com", "wavelet");

  public void testFetchWavelet() throws Exception {
    HttpService service = mock(HttpService.class);
    GsonFactory gsonFactory = new GsonFactory();
    ClientWave clientWave = new ClientWave("http://localhost", "token", service, gsonFactory);

    Wavelet wavelet = new Wavelet();
    when(service.get(
        "http://localhost/wave/example.com/wave/wavelet/example.com/wavelet?token=token",
        Wavelet.class)).thenReturn(wavelet);

    Wavelet fetchedWavelet = clientWave.fetchWavelet(WAVE_ID, WAVELET_ID);
    assertEquals(wavelet, fetchedWavelet);
  }

    public void testFetchWaveletNullContext() throws Exception {
        HttpService service = mock(HttpService.class);
        GsonFactory gsonFactory = new GsonFactory();
        ClientWave clientWave = new ClientWave("http://localhost", "token", service, gsonFactory);

        Wavelet wavelet = new Wavelet();
        when(service.get(
                "http://localhost/wave/example.com/wave/wavelet/example.com/wavelet?token=token",
                Wavelet.class)).thenReturn(wavelet);

        Wavelet fetchedWavelet = clientWave.fetchWavelet(WAVE_ID, WAVELET_ID, null);
        assertEquals(wavelet, fetchedWavelet);
    }
}