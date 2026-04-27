java
package org.jasig.cas.authentication.principal;

import org.jasig.cas.authentication.Credential;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChainingPrincipalResolverTests {

    private ChainingPrincipalResolver resolver;
    private List<PrincipalResolver> chain;

    @Before
    public void setUp() {
        resolver = new ChainingPrincipalResolver();
        chain = new ArrayList<>();
        resolver.setChain(chain);
    }

    @Test
    public void testEmptyChain() {
        Credential credential = mock(Credential.class);
        assertNull(resolver.resolve(credential));
    }

    @Test
    public void testSingleResolver() {
        PrincipalResolver mockResolver = mock(PrincipalResolver.class);
        Principal expectedPrincipal = new SimplePrincipal("testUser", new HashMap<>());
        when(mockResolver.resolve(any(Credential.class))).thenReturn(expectedPrincipal);
        chain.add(mockResolver);

        Credential credential = mock(Credential.class);
        Principal actualPrincipal = resolver.resolve(credential);
        assertEquals(expectedPrincipal, actualPrincipal);
    }

    @Test
    public void testChainedResolvers() {
        PrincipalResolver mockResolver1 = mock(PrincipalResolver.class);
        PrincipalResolver mockResolver2 = mock(PrincipalResolver.class);
        Principal intermediatePrincipal = new SimplePrincipal("intermediateUser", new HashMap<>());
        Principal expectedPrincipal = new SimplePrincipal("finalUser", new HashMap<>());

        when(mockResolver1.resolve(any(Credential.class))).thenReturn(intermediatePrincipal);
        when(mockResolver2.resolve(any(Credential.class))).thenReturn(expectedPrincipal);

        chain.add(mockResolver1);
        chain.add(mockResolver2);

        Credential credential = mock(Credential.class);
        Principal actualPrincipal = resolver.resolve(credential);
        assertEquals(expectedPrincipal, actualPrincipal);
    }

    @Test
    public void testChainedResolversWithNullIntermediate() {
        PrincipalResolver mockResolver1 = mock(PrincipalResolver.class);
        PrincipalResolver mockResolver2 = mock(PrincipalResolver.class);
        Principal expectedPrincipal = new SimplePrincipal("finalUser", new HashMap<>());

        when(mockResolver1.resolve(any(Credential.class))).thenReturn(null);
        when(mockResolver2.resolve(any(Credential.class))).thenReturn(expectedPrincipal);

        chain.add(mockResolver1);
        chain.add(mockResolver2);

        Credential credential = mock(Credential.class);
        Principal actualPrincipal = resolver.resolve(credential);
        assertNull(actualPrincipal);
    }

    @Test
    public void testIdentifiableCredentialIsUsedInChain() {
        PrincipalResolver mockResolver1 = mock(PrincipalResolver.class);
        PrincipalResolver mockResolver2 = mock(PrincipalResolver.class);
        Principal intermediatePrincipal = new SimplePrincipal("intermediateUser", new HashMap<>());
        Principal expectedPrincipal = new SimplePrincipal("finalUser", new HashMap<>());

        when(mockResolver1.resolve(any(Credential.class))).thenReturn(intermediatePrincipal);
        when(mockResolver2.resolve(any(IdentifiableCredential.class))).thenReturn(expectedPrincipal);

        chain.add(mockResolver1);
        chain.add(mockResolver2);

        Credential credential = mock(Credential.class);
        Principal actualPrincipal = resolver.resolve(credential);
        assertEquals(expectedPrincipal, actualPrincipal);
    }
}