java
package com.gms.service.configuration;

import com.gms.domain.configuration.BConfiguration;
import com.gms.domain.security.BAuthorization;
import com.gms.domain.security.ownedentity.EOwnedEntity;
import com.gms.domain.security.role.BRole;
import com.gms.domain.security.user.EUser;
import com.gms.repository.configuration.BConfigurationRepository;
import com.gms.repository.security.authorization.BAuthorizationRepository;
import com.gms.repository.security.ownedentity.EOwnedEntityRepository;
import com.gms.repository.security.role.BRoleRepository;
import com.gms.repository.security.user.EUserRepository;
import com.gms.util.configuration.ConfigKey;
import com.gms.util.constant.DefaultConst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigurationServiceTest {

    @Mock
    private EUserRepository userRepository;
    @Mock
    private EOwnedEntityRepository entityRepository;
    @Mock
    private BRoleRepository roleRepository;
    @Mock
    private BAuthorizationRepository authRepository;
    @Mock
    private DefaultConst dc;

    @InjectMocks
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isDefaultUserAssignedToEntityWithRole_success() {
        EUser user = new EUser();
        user.setId(1L);
        EOwnedEntity entity = new EOwnedEntity();
        entity.setId(2L);
        BRole role = new BRole();
        role.setId(3L);

        when(dc.getUserAdminDefaultName()).thenReturn("admin");
        when(dc.getUserAdminDefaultEmail()).thenReturn("admin@example.com");
        when(userRepository.findFirstByUsernameOrEmail("admin", "admin@example.com")).thenReturn(user);
        when(dc.getEntityDefaultUsername()).thenReturn("entity");
        when(entityRepository.findFirstByUsername("entity")).thenReturn(entity);
        when(dc.getRoleAdminDefaultLabel()).thenReturn("admin_role");
        when(roleRepository.findFirstByLabel("admin_role")).thenReturn(role);

        boolean result = configurationService.isDefaultUserAssignedToEntityWithRole();

        assertTrue(result);
        verify(authRepository).save(any(BAuthorization.class));
    }

    @Test
    void isDefaultUserAssignedToEntityWithRole_userNotFound() {
        when(dc.getUserAdminDefaultName()).thenReturn("admin");
        when(dc.getUserAdminDefaultEmail()).thenReturn("admin@example.com");
        when(userRepository.findFirstByUsernameOrEmail("admin", "admin@example.com")).thenReturn(null);

        boolean result = configurationService.isDefaultUserAssignedToEntityWithRole();

        assertFalse(result);
        verify(authRepository, never()).save(any(BAuthorization.class));
    }

    @Test
    void isDefaultUserAssignedToEntityWithRole_entityNotFound() {
        EUser user = new EUser();
        user.setId(1L);

        when(dc.getUserAdminDefaultName()).thenReturn("admin");
        when(dc.getUserAdminDefaultEmail()).thenReturn("admin@example.com");
        when(userRepository.findFirstByUsernameOrEmail("admin", "admin@example.com")).thenReturn(user);
        when(dc.getEntityDefaultUsername()).thenReturn("entity");
        when(entityRepository.findFirstByUsername("entity")).thenReturn(null);

        boolean result = configurationService.isDefaultUserAssignedToEntityWithRole();

        assertFalse(result);
        verify(authRepository, never()).save(any(BAuthorization.class));
    }

    @Test
    void isDefaultUserAssignedToEntityWithRole_roleNotFound() {
        EUser user = new EUser();
        user.setId(1L);
        EOwnedEntity entity = new EOwnedEntity();
        entity.setId(2L);

        when(dc.getUserAdminDefaultName()).thenReturn("admin");
        when(dc.getUserAdminDefaultEmail()).thenReturn("admin@example.com");
        when(userRepository.findFirstByUsernameOrEmail("admin", "admin@example.com")).thenReturn(user);
        when(dc.getEntityDefaultUsername()).thenReturn("entity");
        when(entityRepository.findFirstByUsername("entity")).thenReturn(entity);
        when(dc.getRoleAdminDefaultLabel()).thenReturn("admin_role");
        when(roleRepository.findFirstByLabel("admin_role")).thenReturn(null);

        boolean result = configurationService.isDefaultUserAssignedToEntityWithRole();

        assertFalse(result);
        verify(authRepository, never()).save(any(BAuthorization.class));
    }
}