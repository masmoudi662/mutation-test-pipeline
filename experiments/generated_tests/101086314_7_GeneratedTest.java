java
package org.openapitools.openapistylevalidator;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.info.License;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapistylevalidator.ValidatorParameters.NamingConvention;
import org.openapitools.openapistylevalidator.styleerror.StyleError;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenApiSpecStyleValidatorTest {

    @Test
    public void testValidateWithEmptyParameters() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateInfo() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        parameters.setValidateInfoLicense(true);
        OpenAPI openAPI = mock(OpenAPI.class);
        Info info = mock(Info.class);
        License license = mock(License.class);
        when(openAPI.getInfo()).thenReturn(info);
        when(info.getLicense()).thenReturn(license);
        validator.setOpenAPI(openAPI);
        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateOperations() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        parameters.setValidateOperationTags(true);
        OpenAPI openAPI = mock(OpenAPI.class);
        Map<String, PathItem> paths = Collections.singletonMap("/test", mock(PathItem.class));
        PathItem pathItem = mock(PathItem.class);
        Operation operation = mock(Operation.class);
        when(openAPI.getPaths()).thenReturn(paths);
        when(pathItem.getGet()).thenReturn(operation);
        when(paths.get("/test")).thenReturn(pathItem);
        validator.setOpenAPI(openAPI);

        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateModels() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        parameters.setValidateModelPropertiesExample(true);
        OpenAPI openAPI = mock(OpenAPI.class);
        Map<String, Schema> schemas = Collections.singletonMap("Test", mock(Schema.class));
        Schema schema = mock(Schema.class);

        when(openAPI.getComponents()).thenReturn(null);
        validator.setOpenAPI(openAPI);

        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateNaming() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        parameters.setModelNameNamingConvention(NamingConvention.CamelCase);
        OpenAPI openAPI = mock(OpenAPI.class);
        validator.setOpenAPI(openAPI);

        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateWithAllValidationsEnabled() {
        OpenApiSpecStyleValidator validator = new OpenApiSpecStyleValidator();
        ValidatorParameters parameters = new ValidatorParameters();
        parameters.setValidateInfoLicense(true);
        parameters.setValidateOperationTags(true);
        parameters.setValidateModelPropertiesExample(true);
        parameters.setModelNameNamingConvention(NamingConvention.CamelCase);
        OpenAPI openAPI = mock(OpenAPI.class);
        validator.setOpenAPI(openAPI);

        List<StyleError> errors = validator.validate(parameters);
        assertEquals(0, errors.size());
    }
}