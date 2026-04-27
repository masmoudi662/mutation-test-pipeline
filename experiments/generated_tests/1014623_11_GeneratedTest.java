java
package com.rhymestore.store;

import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class RedisStoreTest
{
    @InjectMocks
    private RedisStore redisStore;

    private final Charset encoding = Charset.forName("UTF-8");

    @Before
    public void setUp()
    {
        redisStore = new RedisStore();
        redisStore.encoding = encoding;
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSum_emptyString()
    {
        String value = "";
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_simpleString()
    {
        String value = "test";
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_stringWithSpaces()
    {
        String value = "test string";
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_stringWithSpecialCharacters()
    {
        String value = "test!@#$%^&*()_+";
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_stringWithUnicodeCharacters()
    {
        String value = "测试字符串";
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_longString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
        {
            sb.append("a");
        }
        String value = sb.toString();
        String expected = Hashing.md5().hashString(value, encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }

    @Test
    public void testSum_null()
    {
        String value = null;
        String expected = Hashing.md5().hashString(value != null ? value : "null", encoding).toString();
        String actual = redisStore.sum(value);
        assertEquals(expected, actual);
    }
}