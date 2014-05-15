package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import org.junit.Before;
import org.mockito.Mock;
import org.opensaml.ws.security.SecurityPolicyException;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLControllerTest {

    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    SAMLService samlService;

    SAMLController samlController;
    String samlAssertion = "samlAssertion";
    String samlResponse = "SAMLResponse";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController(samlService);
    }

    @Test
    public void shouldValidateSAMLResponse() throws UnsupportedEncodingException, SecurityPolicyException {
//        samlController.handleOKTACallback(httpServletRequest);
//        verify(samlService.getSAMLResponse(anyString()));

    }
}
