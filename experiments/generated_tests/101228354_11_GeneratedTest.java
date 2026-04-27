java
package com.ldtteam.blockout.binding.dependency.injection;

import com.ldtteam.blockout.binding.dependency.IDependencyDataProvider;
import com.ldtteam.blockout.binding.dependency.IDependencyReceiver;
import com.ldtteam.blockout.binding.dependency.IDependencyObject;
import com.ldtteam.blockout.proxy.ProxyHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DependencyObjectInjectorTest
{

    private IDependencyReceiver target;
    private IDependencyDataProvider provider;

    @BeforeEach
    void setUp()
    {
        target = mock(IDependencyReceiver.class);
        provider = mock(IDependencyDataProvider.class);
    }

    @Test
    void inject_noFields()
    {
        try (MockedStatic<ProxyHolder> proxyHolderMockedStatic = mockStatic(ProxyHolder.class))
        {
            ProxyHolder proxyHolder = mock(ProxyHolder.class);
            proxyHolderMockedStatic.when(ProxyHolder::getInstance).thenReturn(proxyHolder);

            ReflectionManager reflectionManager = mock(ReflectionManager.class);
            when(proxyHolder.getReflectionManager()).thenReturn(reflectionManager);

            when(reflectionManager.getFieldsForClass(any())).thenReturn(new Field[0]);

            DependencyObjectInjector.inject(target, provider);

            verify(reflectionManager, times(1)).getFieldsForClass(any());
        }
    }

    @Test
    void inject_noDependencyObjectFields() throws NoSuchFieldException
    {
        try (MockedStatic<ProxyHolder> proxyHolderMockedStatic = mockStatic(ProxyHolder.class))
        {
            ProxyHolder proxyHolder = mock(ProxyHolder.class);
            proxyHolderMockedStatic.when(ProxyHolder::getInstance).thenReturn(proxyHolder);

            ReflectionManager reflectionManager = mock(ReflectionManager.class);
            when(proxyHolder.getReflectionManager()).thenReturn(reflectionManager);

            Field stringField = String.class.getDeclaredField("value");

            when(reflectionManager.getFieldsForClass(any())).thenReturn(new Field[] { stringField });

            DependencyObjectInjector.inject(target, provider);

            verify(reflectionManager, times(1)).getFieldsForClass(any());
        }
    }

    @Test
    void inject_dependencyObject_noData() throws NoSuchFieldException
    {
        try (MockedStatic<ProxyHolder> proxyHolderMockedStatic = mockStatic(ProxyHolder.class))
        {
            ProxyHolder proxyHolder = mock(ProxyHolder.class);
            proxyHolderMockedStatic.when(ProxyHolder::getInstance).thenReturn(proxyHolder);

            ReflectionManager reflectionManager = mock(ReflectionManager.class);
            when(proxyHolder.getReflectionManager()).thenReturn(reflectionManager);

            Field dependencyObjectField = TestClass.class.getDeclaredField("dependency");
            when(reflectionManager.getFieldsForClass(any())).thenReturn(new Field[] { dependencyObjectField });

            when(target.getId()).thenReturn("testId");

            when(provider.hasDependencyData(any())).thenReturn(false);

            DependencyObjectInjector.inject(target, provider);

            verify(reflectionManager, times(1)).getFieldsForClass(any());
        }
    }

    private static class TestClass
    {
        IDependencyObject<String> dependency;
    }
}