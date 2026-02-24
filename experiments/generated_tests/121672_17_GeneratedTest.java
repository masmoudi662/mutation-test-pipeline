java
package cascading.management.annotation;

import org.junit.Test;
import java.net.URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class URISanitizerTest
  {

  @Test
  public void testNullValue()
    {
    URISanitizer sanitizer = new URISanitizer();
    assertNull( sanitizer.apply( Visibility.PUBLIC, null ) );
    }

  @Test
  public void testHierarchicalURI_Public()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "jdbc://user:password@host:port/path?param1=value1&param2=value2" );
    String result = sanitizer.apply( Visibility.PUBLIC, uri );
    assertEquals( "/path", result );
    }

  @Test
  public void testHierarchicalURI_Protected()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "jdbc://user:password@host:port/path?param1=value1&param2=value2" );
    String result = sanitizer.apply( Visibility.PROTECTED, uri );
    assertEquals( "/path?param1=value1&param2=value2&", result );
    }

  @Test
  public void testHierarchicalURI_Private()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "jdbc://user:password@host:port/path?param1=value1&param2=value2" );
    String result = sanitizer.apply( Visibility.PRIVATE, uri );
    assertEquals( "jdbc://user:password@host:port/path?param1=value1&param2=value2&", result );
    }

  @Test
  public void testOpaqueURI_Public()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "mailto:someone@email.com" );
    String result = sanitizer.apply( Visibility.PUBLIC, uri );
    assertEquals( "mailto:", result );
    }

  @Test
  public void testOpaqueURI_Private()
    {
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "mailto:someone@email.com" );
    String result = sanitizer.apply( Visibility.PRIVATE, uri );
    assertEquals( "mailto:someone@email.com", result );
    }

  @Test
  public void testParameterFiltering()
    {
    System.setProperty( URISanitizer.PARAMETER_FILTER_PROPERTY, "password, api_key" );
    URISanitizer sanitizer = new URISanitizer();
    URI uri = URI.create( "http://host/path?user=test&password=secret&api_key=123" );
    String result = sanitizer.apply( Visibility.PROTECTED, uri );
    assertEquals( "/path?user=test&", result );
    System.clearProperty( URISanitizer.PARAMETER_FILTER_PROPERTY );
    }

  @Test
  public void testInvalidURI_PassThrough()
    {
    System.setProperty( URISanitizer.FAILURE_MODE_PASS_THROUGH, "true" );
    URISanitizer sanitizer = new URISanitizer();
    String invalidURI = "invalid uri";
    String result = sanitizer.apply( Visibility.PUBLIC, invalidURI );
    assertEquals( invalidURI, result );
    System.clearProperty( URISanitizer.FAILURE_MODE_PASS_THROUGH );
    }

  @Test
  public void testInvalidURI_Default()
    {
    URISanitizer sanitizer = new URISanitizer();
    String invalidURI = "invalid uri";
    String result = sanitizer.apply( Visibility.PUBLIC, invalidURI );
    assertEquals( "", result );
    }

  @Test
  public void testEncoding()
    {
    URISanitizer sanitizer = new URISanitizer();
    String uriString = "http://host/path[with]brackets{and}braces;and,commas\\\\";
    String result = sanitizer.apply( Visibility.PUBLIC, uriString );
    assertEquals( "/path%5Bwith%5Dbrackets%7Band%7Dbraces%3Band%2Ccommas/", result );
    }
  }