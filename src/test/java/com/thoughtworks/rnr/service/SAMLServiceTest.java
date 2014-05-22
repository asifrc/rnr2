package com.thoughtworks.rnr.service;

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

import static org.mockito.MockitoAnnotations.initMocks;

public class SAMLServiceTest {

    @Mock
    HttpServletRequest request;
    private String samlResponse;
    private SAMLService samlService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlService = new SAMLService();

    }

    @Test
    public void shouldReturnUserIDFromSamlResponse() throws CertificateException, UnmarshallingException, IOException, ValidationException, SAXException, ParserConfigurationException, SecurityPolicyException {
        //set up mocks -- there is a lot of mocking-- design decisions?

//        assertThat(samlService.setSessionWhenSAMLResponseIsValid(request,samlResponse), is("userID"));


    }
}
