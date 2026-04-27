java
package net.petrikainulainen.spring.datajpa.service;

import net.petrikainulainen.spring.datajpa.dto.PersonDTO;
import net.petrikainulainen.spring.datajpa.model.Person;
import net.petrikainulainen.spring.datajpa.repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryPersonServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryPersonServiceTest.class);

    private static final Long PERSON_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private RepositoryPersonService personService;

    private PersonDTO personDTO;
    private Person person;

    @Before
    public void setUp() {
        personDTO = new PersonDTO();
        personDTO.setId(PERSON_ID);
        personDTO.setFirstName(FIRST_NAME);
        personDTO.setLastName(LAST_NAME);

        person = new Person();
        person.setId(PERSON_ID);
    }

    @Test
    public void update_PersonFound_ShouldUpdatePerson() throws PersonNotFoundException {
        when(personRepository.findOne(PERSON_ID)).thenReturn(person);

        personService.update(personDTO);

        ArgumentCaptor<String> firstNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> lastNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(personRepository, times(1)).findOne(PERSON_ID);
        person.update(FIRST_NAME, LAST_NAME);
        assertThat(person.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(person.getLastName()).isEqualTo(LAST_NAME);
    }

    @Test(expected = PersonNotFoundException.class)
    public void update_PersonNotFound_ShouldThrowException() throws PersonNotFoundException {
        when(personRepository.findOne(PERSON_ID)).thenReturn(null);

        personService.update(personDTO);
    }
}