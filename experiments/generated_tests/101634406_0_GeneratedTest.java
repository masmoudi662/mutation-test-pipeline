java
package com.ocadotechnology.newrelic.apiclient.internal;

import com.ocadotechnology.newrelic.apiclient.AlertsPoliciesApi;
import com.ocadotechnology.newrelic.apiclient.internal.client.NewRelicClient;
import com.ocadotechnology.newrelic.apiclient.internal.model.AlertsPolicyChannelsWrapper;
import com.ocadotechnology.newrelic.apiclient.internal.model.AlertsPolicyList;
import com.ocadotechnology.newrelic.apiclient.internal.model.AlertsPolicyWrapper;
import com.ocadotechnology.newrelic.apiclient.model.policies.AlertsPolicy;
import com.ocadotechnology.newrelic.apiclient.model.policies.AlertsPolicyChannels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAlertsPoliciesApiTest {

    private static final String POLICIES_URL = "https://api.newrelic.com/v2/alerts_policies.json";
    private static final MediaType APPLICATION_JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;

    @Mock
    private NewRelicClient client;

    private DefaultAlertsPoliciesApi alertsPoliciesApi;

    @Before
    public void setUp() {
        alertsPoliciesApi = new DefaultAlertsPoliciesApi(client);
    }

    @Test
    public void testGetByName_policyExists() {
        String policyName = "Test Policy";
        AlertsPolicy alertsPolicy = AlertsPolicy.builder().id(1).name(policyName).build();
        AlertsPolicyList alertsPolicyList = new AlertsPolicyList();
        alertsPolicyList.add(alertsPolicy);

        WebTarget target = mock(WebTarget.class);
        when(client.target(POLICIES_URL)).thenReturn(target);
        when(target.queryParam(eq("filter[name]"), anyString())).thenReturn(target);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request(APPLICATION_JSON_TYPE)).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.readEntity(AlertsPolicyList.class)).thenReturn(alertsPolicyList);
        when(response.getStatusInfo()).thenReturn(Response.Status.OK);

        Optional<AlertsPolicy> result = alertsPoliciesApi.getByName(policyName);

        assertEquals(policyName, result.map(AlertsPolicy::getName).orElse(null));
    }

    @Test
    public void testGetByName_policyDoesNotExist() {
        String policyName = "Nonexistent Policy";

        WebTarget target = mock(WebTarget.class);
        when(client.target(POLICIES_URL)).thenReturn(target);
        when(target.queryParam(eq("filter[name]"), anyString())).thenReturn(target);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request(APPLICATION_JSON_TYPE)).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.readEntity(AlertsPolicyList.class)).thenReturn(new AlertsPolicyList());
        when(response.getStatusInfo()).thenReturn(Response.Status.OK);

        Optional<AlertsPolicy> result = alertsPoliciesApi.getByName(policyName);

        assertEquals(Optional.empty(), result);
    }
}