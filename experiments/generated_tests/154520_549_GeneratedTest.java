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
	public void shouldBeAbleToConvertANumber() {
		assertThat(converter.convert("4", Long.class, bundle), equalTo(4l));
	}

	@Test
	public void shouldBeAbleToConvertANumberIncludingAPlusSign() {
		assertThat(converter.convert("+4", Long.class, bundle), equalTo(4l));
	}

	@Test
	public void shouldBeAbleToConvertANegativeNumber() {
		assertThat(converter.convert("-4", Long.class, bundle), equalTo(-4l));
	}

	@Test
	public void shouldBeAbleToConvertANumberWithManyDigits() {
		assertThat(converter.convert("493573894", Long.class, bundle), equalTo(493573894l));
	}

	@Test
	public void shouldConvertEmptyToNull() {
		assertThat(converter.convert("", Long.class, bundle), equalTo(null));
	}

	@Test
	public void shouldConvertNullToNull() {
		assertThat(converter.convert(null, Long.class, bundle), equalTo(null));
	}

	@Test
	public void shouldThrowAnExceptionIfUnableToParse() {
		ResourceBundle bundle = mock(ResourceBundle.class);
		when(bundle.getString("is_not_a_valid_integer")).thenReturn("{0} is not a valid number.");

		try {
			converter.convert("abc", Long.class, bundle);
			fail("Should throw an exception");
		} catch (ConversionError e) {
			assertThat(e.getMessage(), equalTo("abc is not a valid number."));
		}
	}
}