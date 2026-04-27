java
package com.att.tta.rs.controller;

import com.att.tta.rs.service.EventService;
import com.att.tta.rs.service.UserDetailsServiceImpl;
import com.att.tta.rs.util.model.Team;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class EventRestControllerTest {

    @InjectMocks
    private EventRestController eventRestController;

    @Mock
    private EventService eventService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(EventRestControllerTest.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCountByTeamName() {
        try {
            Team mockTeam = new Team();
            mockTeam.setTeamName("TestTeam");
            when(userDetailsService.getCurrentTeamForUser(request)).thenReturn(mockTeam);
            when(eventService.countByTeamName("TestTeam")).thenReturn(10L);

            ResponseEntity<Object> responseEntity = eventRestController.countByTeamName(request);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("10", responseEntity.getBody());
        } catch (Exception e) {
            logger.error("Exception in testCountByTeamName: ", e);
        }
    }

    @Test
    public void testCountByTeamName_NoEvents() {
        try {
            Team mockTeam = new Team();
            mockTeam.setTeamName("TestTeam");
            when(userDetailsService.getCurrentTeamForUser(request)).thenReturn(mockTeam);
            when(eventService.countByTeamName("TestTeam")).thenReturn(0L);

            ResponseEntity<Object> responseEntity = eventRestController.countByTeamName(request);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("0", responseEntity.getBody());
        } catch (Exception e) {
            logger.error("Exception in testCountByTeamName_NoEvents: ", e);
        }
    }

    @Test
    public void testCountByTeamName_DifferentCount() {
        try {
            Team mockTeam = new Team();
            mockTeam.setTeamName("AnotherTeam");
            when(userDetailsService.getCurrentTeamForUser(request)).thenReturn(mockTeam);
            when(eventService.countByTeamName("AnotherTeam")).thenReturn(5L);

            ResponseEntity<Object> responseEntity = eventRestController.countByTeamName(request);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("5", responseEntity.getBody());
        } catch (Exception e) {
            logger.error("Exception in testCountByTeamName_DifferentCount: ", e);
        }
    }

    @Test
    public void testRequestMappingAnnotation() throws NoSuchMethodException {
        RequestMapping requestMapping = EventRestController.class.getMethod("countByTeamName", HttpServletRequest.class)
            .getAnnotation(RequestMapping.class);

        assertEquals("/api/events/count/", requestMapping.value()[0]);
        assertEquals(RequestMethod.GET, requestMapping.method()[0]);
        assertEquals("application/json", requestMapping.produces()[0]);
    }

    @Test
    public void testApiOperationAnnotation() throws NoSuchMethodException {
        ApiOperation apiOperation = EventRestController.class.getMethod("countByTeamName", HttpServletRequest.class)
            .getAnnotation(ApiOperation.class);

        assertEquals("This API returns count of Event objects available in Elastic Search for given team name", apiOperation.value());
    }

}