package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService2;
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLControllerTest {
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private SAMLService2 samlService2;

    SAMLController samlController;
    String samlResponse = "SAMLResponse";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController(samlService2);
    }

    @Test
    public void shouldGetSAMLResponseParameterFromHTTPRequest() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        samlController.handleOKTACallback(httpServletRequest);

        verify(httpServletRequest).getParameter(samlResponse);
    }

    @Test
    public void shouldVerifyOKTASignOn() throws SAXException, ParserConfigurationException, IOException, ValidationException, CertificateException, UnmarshallingException, SecurityPolicyException {
        samlController.handleOKTACallback(httpServletRequest);

        verify(samlService2).verifyOKTASignOn(anyString());
    }

}
