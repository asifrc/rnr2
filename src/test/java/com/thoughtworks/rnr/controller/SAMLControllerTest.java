package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import com.thoughtworks.rnr.service.SalesForceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLControllerTest {
    @Mock HttpServletRequest mockRequest;
    @Mock HttpServletResponse mockResponse;
    @Mock SAMLService mockSamlService;
    @Mock SalesForceService mockSalesForceService;

    private SAMLController samlController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController(mockSamlService, mockSalesForceService);
    }

    @Test
    public void shouldGetSAMLResponseStringFromHTTPRequest() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        samlController.handleOKTACallback(mockRequest, mockResponse);

        verify(mockRequest).getParameter("SAMLResponse");
    }

    @Test
    public void shouldSetSession() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        when(mockRequest.getParameter("SAMLResponse")).thenReturn("some response");
        samlController.handleOKTACallback(mockRequest, mockResponse);

        verify(mockSamlService).setSessionWhenSAMLResponseIsValid(mockRequest, "some response");
    }

    @Test
    public void shouldSetUserEmailOnSalesForceService() throws CertificateException, UnmarshallingException, IOException, ValidationException, SAXException, ParserConfigurationException, SecurityPolicyException {
        String userEmail = "Some email";
        when(mockRequest.getParameter("SAMLResponse")).thenReturn("some response");
        when(mockSamlService.getUserIdFromSAMLString("some response")).thenReturn(userEmail);

        samlController.handleOKTACallback(mockRequest, mockResponse);

        verify(mockSamlService).getUserIdFromSAMLString("some response");
        verify(mockSalesForceService).setUserEmail(userEmail);
    }

    @Test
    public void shouldAuthenticateWithSalesForce() throws IOException {
        samlController.handleOKTACallback(mockRequest, mockResponse);

        verify(mockSalesForceService).authenticateWithSalesForce(mockRequest, mockResponse);
    }

    @Test
    public void shouldReturnSorryViewWhenExceptionIsThrown() throws CertificateException, UnmarshallingException, IOException, ValidationException, SAXException, ParserConfigurationException, SecurityPolicyException {
        when(mockRequest.getParameter("SAMLResponse")).thenReturn("some response");
        doThrow(new SecurityPolicyException()).when(mockSamlService).setSessionWhenSAMLResponseIsValid(mockRequest, "some response");

        String actual = samlController.handleOKTACallback(mockRequest,mockResponse);

        assertThat(actual, is("sorry"));
    }
}