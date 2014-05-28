package com.thoughtworks.rnr.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SalesForceServiceTest {
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String INSTANCE_URL = "INSTANCE_URL";

    @Mock HttpServletRequest mockHttpServletRequest;
    @Mock HttpSession mockHttpSession;
    @Mock HttpServletResponse mockHttpServletResponse;
    @Mock JSONObject mockJSONObject;
    @Mock HttpClient mockHttpClient;
    @Mock PostMethod mockPostMethod;

    private SalesForceService salesForceService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        salesForceService = new SalesForceService();
    }

    @Test
    public void authenticateWithSalesForce_shouldRedirectToSalesForceAuthenticationURLIfNoAccessToken() throws Exception {
        when(mockHttpServletRequest.getSession()).thenReturn(mockHttpSession);
        when(mockHttpSession.getAttribute(ACCESS_TOKEN)).thenReturn(null);

        salesForceService.authenticateWithSalesForce(mockHttpServletRequest, mockHttpServletResponse);

        verify(mockHttpServletResponse).sendRedirect(any(String.class));
    }

//    @Ignore
//    @Test
//    public void queryThoughtWorksStartDate_shouldReturnDateString() throws IOException, URISyntaxException, JSONException {
//        JSONObject stubJSONObject = new JSONObject("{records: [{pse__Start_Date__c: 2007-01-10}]}");
//        assertThat(date, matches("^\\d{4}-\\d{2}-\\d{2}$"));
//    }
}