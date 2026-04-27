java
package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.modelmapper.config.Configuration;
import org.modelmapper.internal.MappingContextImpl;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class NumberConverterTest {
  private NumberConverter converter;
  private Configuration configuration;

  @BeforeMethod
  public void setup() {
    converter = new NumberConverter();
    configuration = new Configuration();
  }

  public void testConvertNumber() {
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(10, Integer.class,
        Long.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, 10L);
  }

  public void testConvertBoolean() {
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(true, Boolean.class,
        Integer.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, Integer.valueOf(1));

    context = new MappingContextImpl<Object, Number>(false, Boolean.class, Integer.class, configuration);
    result = converter.convert(context);
    assertEquals(result, Integer.valueOf(0));
  }

  public void testConvertDate() {
    Date date = new Date();
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(date, Date.class, Long.class,
        configuration);
    Number result = converter.convert(context);
    assertEquals(result, Long.valueOf(date.getTime()));
  }

  public void testConvertCalendar() {
    Calendar calendar = Calendar.getInstance();
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(calendar, Calendar.class,
        Long.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, Long.valueOf(calendar.getTime().getTime()));
  }

  public void testConvertXMLGregorianCalendar() throws Exception {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
        gregorianCalendar);
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(xmlGregorianCalendar,
        XMLGregorianCalendar.class, Long.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, Long.valueOf(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis()));
  }

  public void testConvertString() {
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>("123", String.class,
        Integer.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, Integer.valueOf(123));
  }

  public void testConvertNullSource() {
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(null, Object.class,
        Integer.class, configuration);
    Number result = converter.convert(context);
    assertNull(result);
  }

  public void testConvertBigDecimal() {
    BigDecimal bigDecimal = new BigDecimal("123.45");
    MappingContext<Object, Number> context = new MappingContextImpl<Object, Number>(bigDecimal, BigDecimal.class,
        Double.class, configuration);
    Number result = converter.convert(context);
    assertEquals(result, bigDecimal.doubleValue());
  }
}