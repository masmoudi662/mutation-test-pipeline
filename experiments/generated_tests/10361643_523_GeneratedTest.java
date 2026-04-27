java
package org.slc.sli.api.security.context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.security.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContextValidatorTest {

    @InjectMocks
    private ContextValidator contextValidator;

    @Mock
    private IContextValidator mockValidator;

    @Mock
    private EntityService mockRepo;

    @Mock
    private EntityDefinition mockDef;

    @Mock
    private EdOrgHelper mockEdOrgHelper;

    @Mock
    private ApplicationContext mockApplicationContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = APIAccessDeniedException.class)
    public void testValidateContextToEntitiesNoValidator() throws APIAccessDeniedException {
        when(contextValidator.findValidator(anyString(), anyBoolean())).thenReturn(null);
        when(mockDef.getType()).thenReturn("student");
        Collection<String> ids = Collections.singletonList("123");
        contextValidator.validateContextToEntities(mockDef, ids, false);
    }

    @Test
    public void testValidateContextToEntitiesSuccess() throws APIAccessDeniedException {
        when(contextValidator.findValidator(anyString(), anyBoolean())).thenReturn(mockValidator);
        when(mockDef.getType()).thenReturn("student");
        when(mockDef.getStoredCollectionName()).thenReturn("students");
        Collection<String> ids = Collections.singletonList("123");
        when(mockRepo.findAll(anyString(), any())).thenReturn(new ArrayList<>());
        Set<String> validatedIds = new HashSet<>(ids);
        when(mockValidator.validate(any(Set.class))).thenReturn(validatedIds);

        contextValidator.validateContextToEntities(mockDef, ids, false);
    }

    @Test(expected = APIAccessDeniedException.class)
    public void testValidateContextToEntitiesFailure() throws APIAccessDeniedException {
        when(contextValidator.findValidator(anyString(), anyBoolean())).thenReturn(mockValidator);
        when(mockDef.getType()).thenReturn("student");
        when(mockDef.getStoredCollectionName()).thenReturn("students");
        Collection<String> ids = Collections.singletonList("123");
        when(mockRepo.findAll(anyString(), any())).thenReturn(new ArrayList<>());
        when(mockValidator.validate(any(Set.class))).thenReturn(new HashSet<>());
        contextValidator.validateContextToEntities(mockDef, ids, false);
    }

    @Test
    public void testFindValidator() {
        contextValidator.setApplicationContext(mockApplicationContext);
        IContextValidator validator = mock(IContextValidator.class);
        when(mockApplicationContext.getBean("studentValidator", IContextValidator.class)).thenReturn(validator);

        IContextValidator foundValidator = contextValidator.findValidator("student", false);
    }
}