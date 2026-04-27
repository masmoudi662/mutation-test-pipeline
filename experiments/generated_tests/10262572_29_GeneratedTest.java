java
package com.helger.phase2.util.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ChunkedInputStreamTest
{
  @Test
  public void testEmpty () throws IOException
  {
    final String sInput = "0\r\n\r\n";
    try (final InputStream aBAIS = new ByteArrayInputStream (sInput.getBytes ()))
    {
      try (final ChunkedInputStream aCIS = new ChunkedInputStream (aBAIS))
      {
        assertEquals (-1, aCIS.read ());
        assertEquals (-1, aCIS.read ());
      }
    }
  }

  @Test
  public void testSimple () throws IOException
  {
    final String sInput = "5\r\nhello\r\n0\r\n\r\n";
    try (final InputStream aBAIS = new ByteArrayInputStream (sInput.getBytes ()))
    {
      try (final ChunkedInputStream aCIS = new ChunkedInputStream (aBAIS))
      {
        assertEquals ('h', aCIS.read ());
        assertEquals ('e', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        assertEquals ('o', aCIS.read ());
        assertEquals (-1, aCIS.read ());
      }
    }
  }

  @Test
  public void testSimple2 () throws IOException
  {
    final String sInput = "5\r\nhello\r\n1\r\na\r\n0\r\n\r\n";
    try (final InputStream aBAIS = new ByteArrayInputStream (sInput.getBytes ()))
    {
      try (final ChunkedInputStream aCIS = new ChunkedInputStream (aBAIS))
      {
        assertEquals ('h', aCIS.read ());
        assertEquals ('e', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        assertEquals ('o', aCIS.read ());
        assertEquals ('a', aCIS.read ());
        assertEquals (-1, aCIS.read ());
      }
    }
  }

  @Test
  public void testCarriageReturn () throws IOException
  {
    final String sInput = "1\r\r\r\n0\r\n\r\n";
    try (final InputStream aBAIS = new ByteArrayInputStream (sInput.getBytes ()))
    {
      try (final ChunkedInputStream aCIS = new ChunkedInputStream (aBAIS))
      {
        assertEquals ('\r', aCIS.read ());
        assertEquals (-1, aCIS.read ());
      }
    }
  }

  @Test
  public void testInvalid () throws IOException
  {
    final String sInput = "5\r\nhell\r\n";
    try (final InputStream aBAIS = new ByteArrayInputStream (sInput.getBytes ()))
    {
      try (final ChunkedInputStream aCIS = new ChunkedInputStream (aBAIS))
      {
        assertEquals ('h', aCIS.read ());
        assertEquals ('e', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        assertEquals ('l', aCIS.read ());
        try
        {
          aCIS.read ();
          fail ();
        }
        catch (final IOException ex)
        {
          // Expected
        }
      }
    }
  }
}