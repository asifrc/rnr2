package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SalesForceService;
import org.apache.commons.httpclient.HttpClient;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

public class SalesForceControllerTest {

    private SalesForceService mockSalesForceService;
    private HttpServletRequest mockRequest;
    private HttpClient mockClient;
    private SalesForceController salesForceController;
    private HttpSession mockHttpSession;

    @Before
    public void setUp() throws Exception {
        mockRequest = mock(HttpServletRequest.class);
        mockClient = mock(HttpClient.class);
        mockSalesForceService = mock(SalesForceService.class);
        salesForceController = new SalesForceController(mockSalesForceService);
        mockHttpSession = mock(HttpSession.class);

    }

    @Test
    public void shouldGetStartDateFromSalesForceService() throws JSONException, IOException, URISyntaxException {
        when(mockRequest.getSession()).thenReturn(mockHttpSession);

        salesForceController.handleSalesForceCallback(mockRequest, mockClient);

        verify(mockSalesForceService).getStartDate(mockRequest, mockClient);
    }

    @Test
    public void shouldSetStartDateAsAttributeOnSession() throws JSONException, IOException, URISyntaxException {
        when(mockRequest.getSession()).thenReturn(mockHttpSession);
        String startDate = "01/01/2014";
        when(mockSalesForceService.getStartDate(mockRequest, mockClient)).thenReturn(startDate);

        salesForceController.handleSalesForceCallback(mockRequest, mockClient);

        verify(mockHttpSession).setAttribute("startDate",startDate);

    }
}
