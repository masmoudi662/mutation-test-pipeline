java
package net.sourceforge.stripes.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

public class BigIntegerTypeConverterTest {

    @Test
    public void testValidConversion() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("12345", BigInteger.class, errors);
        Assert.assertEquals(result, new BigInteger("12345"));
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testInvalidConversion() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("abc", BigInteger.class, errors);
        Assert.assertNull(result);
        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void testConversionWithDecimal() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("123.45", BigInteger.class, errors);
        Assert.assertEquals(result, new BigInteger("123"));
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testConversionWithNegativeValue() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("-12345", BigInteger.class, errors);
        Assert.assertEquals(result, new BigInteger("-12345"));
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testConversionWithLargeValue() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        String largeValue = "123456789012345678901234567890";
        BigInteger result = converter.convert(largeValue, BigInteger.class, errors);
        Assert.assertEquals(result, new BigInteger(largeValue));
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testConversionWithLeadingAndTrailingSpaces() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("  12345  ", BigInteger.class, errors);
        Assert.assertEquals(result, new BigInteger("12345"));
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testConversionWithNullInput() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert(null, BigInteger.class, errors);
        Assert.assertNull(result);
    }

    @Test
    public void testConversionWithEmptyString() {
        BigIntegerTypeConverter converter = new BigIntegerTypeConverter();
        converter.setLocale(Locale.US);
        Collection<ValidationError> errors = new LinkedList<ValidationError>();
        BigInteger result = converter.convert("", BigInteger.class, errors);
        Assert.assertNull(result);
        Assert.assertFalse(errors.isEmpty());
    }
}