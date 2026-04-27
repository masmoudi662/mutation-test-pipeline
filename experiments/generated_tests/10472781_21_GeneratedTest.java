java
package org.apache.deltaspike.data.impl.handler;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.impl.builder.QueryBuilder;
import org.apache.deltaspike.data.impl.meta.RequiresTransaction;
import org.apache.deltaspike.data.impl.property.Property;
import org.apache.deltaspike.data.impl.property.query.NamedPropertyCriteria;
import org.apache.deltaspike.data.impl.property.query.PropertyQueries;
import org.apache.deltaspike.data.impl.util.EntityUtils;
import org.apache.deltaspike.data.impl.util.jpa.PersistenceUnitUtilDelegateFactory;
import org.apache.deltaspike.data.spi.DelegateQueryHandler;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EntityRepositoryHandlerTest {

    @Test
    public void testCount() {
        EntityRepositoryHandler<Object, Serializable> handler = new EntityRepositoryHandler<>();

        EntityManager entityManager = mock(EntityManager.class);
        TypedQuery<Long> typedQuery = mock(TypedQuery.class);
        QueryExecutionContext context = mock(QueryExecutionContext.class);

        handler.context = context;
        handler.entityManager = entityManager;

        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(context.applyRestrictions(any())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(10L);

        Long count = handler.count();

        assertEquals(10L, count);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(typedQuery).getSingleResult();
        verify(context).applyRestrictions(typedQuery);
    }
}