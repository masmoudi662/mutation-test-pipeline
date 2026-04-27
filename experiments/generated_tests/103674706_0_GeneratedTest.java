java
package com.carl.wolf.core.foundation.module;

import com.carl.wolf.core.annotation.Menu;
import com.carl.wolf.core.annotation.Module;
import com.carl.wolf.core.bean.Menu;
import com.carl.wolf.core.exception.ModuleScanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DefaultModuleScanStrategyTest {

    private DefaultModuleScanStrategy moduleScanStrategy;
    private IMenuScanStrategy menuScanStrategy;

    @BeforeEach
    void setUp() {
        moduleScanStrategy = new DefaultModuleScanStrategy();
        menuScanStrategy = Mockito.mock(IMenuScanStrategy.class);
        moduleScanStrategy.setMenuScanStrategy(menuScanStrategy);
    }

    @Test
    void process_validModule_returnsModuleVo() throws ModuleScanException, NoSuchMethodException {
        Object mockBean = Mockito.mock(ValidModule.class);
        when(mockBean.getClass()).thenReturn(ValidModule.class);

        Method testMethod = ValidModule.class.getMethod("testMethod");
        when(menuScanStrategy.support(testMethod)).thenReturn(true);
        when(menuScanStrategy.process(testMethod)).thenReturn(new Menu());

        com.carl.wolf.core.bean.Module moduleVo = moduleScanStrategy.process(mockBean);

        assertNotNull(moduleVo);
        assertEquals("TestModule", moduleVo.getName());
        assertEquals("Test Description", moduleVo.getDescription());
        assertEquals(1, moduleVo.getOrder());
    }

    @Test
    void process_moduleAnnotationMissing_throwsModuleScanException() {
        Object mockBean = Mockito.mock(NoModule.class);
        when(mockBean.getClass()).thenReturn(NoModule.class);

        assertThrows(ModuleScanException.class, () -> moduleScanStrategy.process(mockBean));
    }

    @Module(name = "TestModule", description = "Test Description", order = 1)
    static class ValidModule {
        @Menu(name = "testMenu")
        public void testMethod() {}
    }

    static class NoModule {
    }
}