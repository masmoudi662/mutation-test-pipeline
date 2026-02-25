java
package cascading.management.annotation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static org.junit.Assert.*;

public class URISanitizerTest
  {

  private static final Logger LOG = LoggerFactory.getLogger( URISanitizerTest.class );

  @Test
  public void testApplyNullValue()
    {
    URISanitizer sanitizer = new URISanitizer();
    assertNull( sanitizer.apply( Visibility.PUBLIC, null ) );
    }

  @Test
  public void testApplyURIValuePublic()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "http://user:password@example.com/path?query=value#fragment" );
    assertEquals( "/path", sanitizer.apply( Visibility.PUBLIC, uri ) );
    }

  @Test
  public void testApplyURIValueProtected()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "http://user:password@example.com/path?query=value#fragment" );
    assertEquals( "/path?query=xxxxx", sanitizer.apply( Visibility.PROTECTED, uri ) );
    }

  @Test
  public void testApplyURIValuePrivate()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "http://user:password@example.com/path?query=value#fragment" );
    assertEquals( "http://user:password@example.com/path?query=xxxxx", sanitizer.apply( Visibility.PRIVATE, uri ) );
    }

  @Test
  public void testApplyStringValuePublic()
    {
    URISanitizer sanitizer = new URISanitizer();
    String uriString = "http://user:password@example.com/path?query=value#fragment";
    assertEquals( "/path", sanitizer.apply( Visibility.PUBLIC, uriString ) );
    }

  @Test
  public void testApplyStringValueProtected()
    {
    URISanitizer sanitizer = new URISanitizer();
    String uriString = "http://user:password@example.com/path?query=value#fragment";
    assertEquals( "/path?query=xxxxx", sanitizer.apply( Visibility.PROTECTED, uriString ) );
    }

  @Test
  public void testApplyStringValuePrivate()
    {
    URISanitizer sanitizer = new URISanitizer();
    String uriString = "http://user:password@example.com/path?query=value#fragment";
    assertEquals( "http://user:password@example.com/path?query=xxxxx", sanitizer.apply( Visibility.PRIVATE, uriString ) );
    }

  @Test
  public void testApplyOpaqueURIProtected()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "mailto:john.doe@example.com" );
    assertEquals( "mailto:", sanitizer.apply( Visibility.PROTECTED, uri ) );
    }

  @Test
  public void testApplyOpaqueURIPrivate()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "mailto:john.doe@example.com" );
    assertEquals( "mailto:john.doe@example.com", sanitizer.apply( Visibility.PRIVATE, uri ) );
    }

  @Test
  public void testApplyMalformedURI()
    {
    URISanitizer sanitizer = new URISanitizer();
    String malformedUri = "http://user:password@example.com/path?query=%{invalid}";
    System.setProperty( "cascading.management.uri.failure.passthrough", "true" );
    String result = sanitizer.apply( Visibility.PUBLIC, malformedUri );
    assertEquals(malformedUri, result);
    System.clearProperty( "cascading.management.uri.failure.passthrough" );

    System.setProperty( "cascading.management.uri.failure.passthrough", "false" );
    result = sanitizer.apply( Visibility.PUBLIC, malformedUri );
    assertEquals("", result);
    System.clearProperty( "cascading.management.uri.failure.passthrough" );

    }
  }