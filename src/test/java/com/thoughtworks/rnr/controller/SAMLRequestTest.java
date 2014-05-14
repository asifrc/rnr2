package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.saml.*;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.cert.CertificateException;

import static org.testng.Assert.assertEquals;

/**
 * Test to verify the integrity of SAMLRequest
 */
public class SAMLRequestTest {

    private static final String MOCK_CURRENT_TIME = "2012-11-13T23:05:18.895Z";
    private static final String MOCK_UUID = "2440125e-7655-453a-bb98-186e6b4bcdfe";

    private Application application;
    private MockClock mockClock;

    private TestsHelper helper;
    private MockIdentifier mockIdentifier;

    @Before
    public void setup() throws IOException, XPathExpressionException, CertificateException, SecurityPolicyException, ConfigurationException {
        DefaultBootstrap.bootstrap(); // initialize openSAML library

        helper = new TestsHelper();
        Configuration newconfig = helper.loadConfig("/config.xml");
        application = newconfig.getDefaultApplication();

        mockClock = new MockClock();
        mockClock.setInstant(MOCK_CURRENT_TIME);

        mockIdentifier = new MockIdentifier();
        mockIdentifier.setId(MOCK_UUID);
    }

    @Test
    public void testAuthnRequest() {
        SAMLRequest samlRequest = new SAMLRequest(application, mockIdentifier, mockClock);

        AuthnRequest authnRequest = samlRequest.getAuthnRequest();

        assertEquals(authnRequest.getID(), MOCK_UUID);
        assertEquals(authnRequest.getIssueInstant().toDate().getTime(), mockClock.dateTimeNow().toDate().getTime());
        assertEquals(authnRequest.getAssertionConsumerServiceURL(), application.getAuthenticationURL());
        assertEquals(authnRequest.getIssuer().getValue(), application.getIssuer());
    }

    @Test
    public void testToString() {
        SAMLRequest samlRequest = new SAMLRequest(application,mockIdentifier,mockClock);
        String requestString = samlRequest.toString().replace("\r", "").replace("\n", "").trim();

        assertEquals(requestString,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<saml2p:AuthnRequest xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" AssertionConsumerServiceURL=\"https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml\" ID=\"2440125e-7655-453a-bb98-186e6b4bcdfe\" IssueInstant=\"2012-11-13T23:05:18.895Z\" ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Version=\"2.0\">" +
                        "<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://www.okta.com/k21tpw64VPAMDOMKRXBS</saml2:Issuer>" +
                        "<saml2p:NameIDPolicy Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\"/>" +
                        "<saml2p:RequestedAuthnContext Comparison=\"exact\">" +
                        "<saml2:AuthnContextClassRef xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef></saml2p:RequestedAuthnContext>" +
                        "</saml2p:AuthnRequest>");
    }
}
