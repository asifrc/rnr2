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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLControllerTest {
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private SAMLService samlService;
    @Mock
    private SalesForceService salesForceService;
    @Mock
    HttpServletResponse httpServletResponse;

    private SAMLController samlController;
    private String samlResponse = "SAMLResponse";
    private String userEmail;
    private String userId;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController(samlService, salesForceService);
    }

    @Test
    public void shouldGetSAMLResponseStringFromHTTPRequest() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        samlController.handleOKTACallback(httpServletRequest, httpServletResponse);

        verify(httpServletRequest).getParameter(samlResponse);
    }

    @Test
    public void shouldSetSession() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        when(httpServletRequest.getParameter(samlResponse)).thenReturn("some response");
        samlController.handleOKTACallback(httpServletRequest, httpServletResponse);

        verify(samlService).setSessionWhenSAMLResponseIsValid(httpServletRequest, "some response");
    }

    @Test
    public void shouldGetUserIDFromSAMLString() throws Exception {
        when(httpServletRequest.getParameter("SAMLResponse")).thenReturn("SAML-STRING");
        userId = "userId";
        when(samlService.getUserIdFromSAMLString("SAML-STRING")).thenReturn(userId);
        samlController.handleOKTACallback(httpServletRequest, httpServletResponse);
        verify(samlService).getUserIdFromSAMLString("SAML-STRING");
        verify(salesForceService).setUserEmail(userId);
    }
}
