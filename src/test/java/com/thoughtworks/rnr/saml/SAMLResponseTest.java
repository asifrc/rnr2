package com.thoughtworks.rnr.saml;

import com.thoughtworks.rnr.saml.util.SimpleClock;
import org.opensaml.DefaultBootstrap;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * Test to verify the integrity of SAMLResponse
 */
public class SAMLResponseTest {

    private static final String ISSUER = "http://www.okta.com/k8zvAOBDWBZBESJWLBYR";
    private static final String USER_ID = "admin";
    private static final String AUDIENCE = "confluence";
    private static Map<String, List<String>> ATTRIBUTES;
    private static final String DESTINATION = "http://192.168.22.135:8090/";
    private static final String RECIPIENT = "confluence";
    private static final long ISSUE_INSTANT = 1354063117933L;

    private Configuration configuration;

    static {
        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        List<String> values = new ArrayList<String>();
        values.add("Add-Min");
        attributes.put("firstName", values);
        ATTRIBUTES = Collections.unmodifiableMap(attributes);
    }

    @BeforeTest
    public void setup() throws XPathExpressionException, IOException, CertificateException, SecurityPolicyException, ConfigurationException {
        DefaultBootstrap.bootstrap(); // initialize openSAML library

        InputStream stream = getClass().getResourceAsStream("/valid-config.xml");
        String file;

        try {
            file = convertStreamToString(stream);
        } finally {
            stream.close();
        }

        configuration = new Configuration(file);

        MockClock mockClock = new MockClock();
        mockClock.setInstant("2012-11-28T00:38:37.895Z");
    }

    @Test
    public void testResponse() throws IOException, SecurityPolicyException {
        InputStream stream = getClass().getResourceAsStream("/valid-response.xml");
        final String assertion;

        try {
            assertion = new String(DatatypeConverter.parseBase64Binary(convertStreamToString(stream)), "UTF-8");
        } finally {
            stream.close();
        }

        SAMLResponse response = new SAMLResponse(assertion, configuration, new SimpleClock());
        assertEquals(response.getDestination(), DESTINATION);
        assertEquals(response.getAudience(), AUDIENCE);
        assertEquals(response.getRecipient(), RECIPIENT);
        assertEquals(response.getIssuer(), ISSUER);
        assertEquals(response.getUserEmail(), USER_ID);
        assertEquals(response.getAttributes(), ATTRIBUTES);
        assertEquals(response.getIssueInstant().getTime(), ISSUE_INSTANT);
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
