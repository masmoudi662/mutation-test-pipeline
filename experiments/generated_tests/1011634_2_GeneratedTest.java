java
package ch.genevajug.crappy.sampleservice.impl;

import ch.genevajug.crappy.sampleservice.EventService;
import ch.genevajug.crappy.sampleservice.PersonService;
import ch.genevajug.crappy.sampleservice.domain.BusinessMessages;
import ch.genevajug.crappy.sampleservice.domain.Person;
import ch.genevajug.crappy.sampleservice.domain.SampleServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleServiceImplTest {

    @Mock
    private PersonService personService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private SampleServiceImpl sampleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createPerson_success() {
        boolean result = sampleService.createPerson("John", "Doe");
        assertTrue(result);
        verify(personService, times(1)).create(any(Person), any(BusinessMessages.class));
        verify(eventService, never()).sendErrorEvent(any(Person.class), any(BusinessMessages.class));
    }

    @Test
    void createPerson_personServiceHasErrors() {
        doAnswer(invocation -> {
            BusinessMessages messages = invocation.getArgument(1);
            messages.addError("Some error");
            return null;
        }).when(personService).create(any(Person.class), any(BusinessMessages.class));

        boolean result = sampleService.createPerson("John", "Doe");
        assertFalse(result);
        verify(personService, times(1)).create(any(Person.class), any(BusinessMessages.class));
        verify(eventService, times(1)).sendErrorEvent(any(Person.class), any(BusinessMessages.class));
    }

    @Test
    void createPerson_firstNameNull_throwsException() {
        assertThrows(SampleServiceException.class, () -> sampleService.createPerson(null, "Doe"));
    }

    @Test
    void createPerson_lastNameNull_throwsException() {
        assertThrows(SampleServiceException.class, () -> sampleService.createPerson("John", null));
    }

    @Test
    void createPerson_firstNameEmpty_throwsException() {
         assertThrows(SampleServiceException.class, () -> sampleService.createPerson("", "Doe"));
    }

    @Test
    void createPerson_lastNameEmpty_throwsException() {
        assertThrows(SampleServiceException.class, () -> sampleService.createPerson("John", ""));
    }

    @Test
    void createPerson_firstNameBlank_throwsException() {
        assertThrows(SampleServiceException.class, () -> sampleService.createPerson("   ", "Doe"));
    }

    @Test
    void createPerson_lastNameBlank_throwsException() {
        assertThrows(SampleServiceException.class, () -> sampleService.createPerson("John", "   "));
    }
}