package com.thoughtworks.rnr.saml;

import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.security.SecurityPolicyException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test to verify the integrity of SAMLValidator
 */
public class SAMLValidatorTest {

    private static final String ISSUER = "http://www.okta.com/k8zvAOBDWBZBESJWLBYR";
    private static final String AUTHENTICATION_URL = "http://rain.okta1.com:1802/app/template_saml_2_0/k8zvAOBDWBZBESJWLBYR/sso/saml";
    private static final String ISSUER2 = "http://www.okta.com/k8lqGUUCIFIJHKUOGQKG";
    private static final String AUTHENTICATION_URL2 = "http://rain.okta1.com:1802/app/template_saml_2_0/k8lqGUUCIFIJHKUOGQKG/sso/saml";

    private SAMLValidator validator;
    private Configuration configuration;
    private Application application;
    private TestsHelper helper;

    @BeforeTest
    public void setup() throws SecurityPolicyException, IOException {
        helper = new TestsHelper();
        validator = new SAMLValidator();
        configuration = validator.getConfigurationFrom("src/main/java/com/thoughtworks/rnr/config.xml");
        application = configuration.getDefaultApplication();
    }

    @Test
    public void testGetConfigurationFrom() {
        assertEquals(configuration.getApplications().size(), 2);

        Application application = configuration.getApplication(ISSUER);
        assertEquals(application.getIssuer(), ISSUER);
        assertEquals(application.getAuthenticationURL(), AUTHENTICATION_URL);

        application= configuration.getApplication(ISSUER2);
        assertEquals(application.getIssuer(), ISSUER2);
        assertEquals(application.getAuthenticationURL(), AUTHENTICATION_URL2);
    }

    @Test
    public void testGetSAMLRequest() {
        SAMLRequest request = validator.getSAMLRequest(application);
        AuthnRequest authnRequest = request.getAuthnRequest();
        assertEquals(authnRequest.getAssertionConsumerServiceURL(), application.getAuthenticationURL());
        assertEquals(authnRequest.getIssuer().getValue(), application.getIssuer());
    }

    @Test
    public void testGetSAMLResponse() throws IOException {
        String responseStr = null;
        InputStream stream = getClass().getResourceAsStream("/valid-response.xml");
        try {
            responseStr = new String(DatatypeConverter.parseBase64Binary(helper.convertStreamToString(stream)), "UTF-8");
        } finally {
            stream.close();
        }

        try {
            SAMLResponse response = validator.getSAMLResponse(responseStr, configuration);
        } catch (SecurityPolicyException e) {
            assertEquals(e.getMessage(), "Conditions have expired");
        } catch (Exception e) {
            fail();
        }
    }
}
