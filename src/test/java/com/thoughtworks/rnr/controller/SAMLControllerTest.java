package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLControllerTest {
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private SAMLService samlService;

    SAMLController samlController;
    private String samlResponse = "SAMLResponse";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController(samlService);
    }

    @Test
    public void shouldGetSAMLResponseStringFromHTTPRequest() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        samlController.handleOKTACallback(httpServletRequest);

        verify(httpServletRequest).getParameter(samlResponse);
    }

    @Test
    public void shouldSetSession() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        when(httpServletRequest.getParameter(samlResponse)).thenReturn("some response");
        samlController.handleOKTACallback(httpServletRequest);

        verify(samlService).setSessionWhenSAMLResponseIsValid(httpServletRequest, "some response");
    }

    @Test
    public void shouldRedirectToHome() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        String actualViewName = samlController.handleOKTACallback(httpServletRequest);
        String expectedViewName = "redirect:/home";

        assertEquals(expectedViewName, actualViewName);
    }
}
