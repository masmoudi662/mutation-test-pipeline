java
package com.squareup.picasso3.pollexor;

import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import com.squareup.picasso3.Picasso.RequestTransformer;
import com.squareup.picasso3.Request;
import com.squareup.picasso3.pollexor.PollexorRequestTransformer.Callback;
import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import com.squareup.pollexor.ThumborUrlBuilder.ImageFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.squareup.pollexor.ThumborUrlBuilder.ImageFormat.WEBP;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.JELLY_BEAN_MR2})
public class PollexorRequestTransformerTest {

  @Mock Thumbor thumbor;
  private PollexorRequestTransformer transformer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    transformer = new PollexorRequestTransformer(thumbor);
  }

  @Test
  public void transformRequest_resourceId() {
    Request request = new Request.Builder(123).build();
    Request transformedRequest = transformer.transformRequest(request);
    assertEquals(request, transformedRequest);
  }

  @Test(expected = IllegalArgumentException.class)
  public void transformRequest_nullUri() {
    Request request = new Request.Builder(null).build();
    transformer.transformRequest(request);
  }

  @Test
  public void transformRequest_nonHttpUri() {
    Request request = new Request.Builder(Uri.parse("file://test")).build();
    Request transformedRequest = transformer.transformRequest(request);
    assertEquals(request, transformedRequest);
  }

  @Test
  public void transformRequest_noResize() {
    Request request = new Request.Builder(Uri.parse("http://test")).build();
    Request transformedRequest = transformer.transformRequest(request);
    assertEquals(request, transformedRequest);
  }

  @Test
  public void transformRequest_resize() {
    Request request = new Request.Builder(Uri.parse("http://test")).resize(100, 200).build();
    when(thumbor.buildImage("http://test")).thenReturn(new ThumborUrlBuilder("test"));
    Request transformedRequest = transformer.transformRequest(request);
    assertEquals("test/unsafe/100x200/filters:format(webp)", transformedRequest.uri.toString());
  }

  @Test
  public void transformRequest_centerInside() {
    Request request = new Request.Builder(Uri.parse("http://test")).resize(100, 200).centerInside().build();
    when(thumbor.buildImage("http://test")).thenReturn(new ThumborUrlBuilder("test"));
    Request transformedRequest = transformer.transformRequest(request);
    assertEquals("test/unsafe/fit-in/100x200/filters:format(webp)", transformedRequest.uri.toString());
  }
}