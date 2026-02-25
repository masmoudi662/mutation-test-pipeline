java
package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.LongConverter;

public class LongConverterTest {

	private LongConverter converter;
	private ResourceBundle bundle;
	private Locale pt_BR;

	@Before
	public void setup() {
		pt_BR = new Locale("pt", "BR");
		bundle = ResourceBundle.getBundle("messages", pt_BR);
		converter = new LongConverter();
	}

	@Test
	public void shouldBeAbleToConvertAValidLong() {
		assertThat(converter.convert("3", Long.class, bundle), equalTo(3l));
	}

	@Test
	public void shouldBeAbleToConvertANegativeValidLong() {
		assertThat(converter.convert("-4", Long.class, bundle), equalTo(-4l));
	}

	@Test
	public void shouldBeAbleToConvertAEmptyValue() {
		assertThat(converter.convert("", Long.class, bundle), equalTo(null));
	}

	@Test
	public void shouldBeAbleToConvertANullValue() {
		assertThat(converter.convert(null, Long.class, bundle), equalTo(null));
	}

	@Test(expected=ConversionException.class)
	public void shouldThrowsAnExceptionWhenTheValueIsAnInvalidLong() {
		try {
			converter.convert("abc", Long.class, bundle);
		} catch (ConversionException e) {
			assertThat(e.getMessage(), equalTo("abc não é um número inteiro válido."));
			throw e;
		}
	}

	@Test
	public void shouldThrowsAnExceptionWhenTheValueIsAnInvalidLongWithoutAProperBundle() {
		ResourceBundle empty = mock(ResourceBundle.class);
		when(empty.getString("is_not_a_valid_integer")).thenReturn(null);

		try {
			converter.convert("abc", Long.class, empty);
			fail("should throw exception");
		} catch (ConversionException e) {
			assertThat(e.getMessage(), equalTo("abc não é um número inteiro válido."));
		}
	}
}