java
package org.apache.oltu.oauth2.common.validators;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

public class AbstractValidatorTest {

    private AbstractValidator<HttpServletRequest> validator;
    private HttpServletRequest request;

    @Before
    public void setUp() {
        validator = new AbstractValidator<HttpServletRequest>() {
            @Override
            public void validateMethod(HttpServletRequest request) throws OAuthProblemException {

            }

            @Override
            public void validateParams(HttpServletRequest request) throws OAuthProblemException {

            }

        };
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testValidateContentTypeValid() throws OAuthProblemException {
        when(request.getContentType()).thenReturn(OAuth.ContentType.URL_ENCODED);
        validator.validateContentType(request);
    }

    @Test(expected = OAuthProblemException.class)
    public void testValidateContentTypeInvalid() throws OAuthProblemException {
        when(request.getContentType()).thenReturn("application/json");
        validator.validateContentType(request);
    }

    @Test(expected = OAuthProblemException.class)
    public void testValidateContentTypeNull() throws OAuthProblemException {
        when(request.getContentType()).thenReturn(null);
        validator.validateContentType(request);
    }

    @Test(expected = OAuthProblemException.class)
    public void testValidateContentTypeEmpty() throws OAuthProblemException {
        when(request.getContentType()).thenReturn("");
        validator.validateContentType(request);
    }

    @Test
    public void testValidateContentTypeWithCharset() throws OAuthProblemException {
        when(request.getContentType()).thenReturn(OAuth.ContentType.URL_ENCODED + ";charset=UTF-8");
        validator.validateContentType(request);
    }
}