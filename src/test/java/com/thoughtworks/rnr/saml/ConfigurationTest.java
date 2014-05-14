package com.thoughtworks.rnr.saml;

import org.opensaml.DefaultBootstrap;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Test to verify the integrity of Configuration
 */
public class ConfigurationTest {
    static final String ISSUER1 = "http://www.okta.com/k8zvAOBDWBZBESJWLBYR";
    static final String ISSUER2 = "http://www.okta.com/k8lqGUUCIFIJHKUOGQKG";
    static final String AUTHENTICATION_URL1 = "http://rain.okta1.com:1802/app/template_saml_2_0/k8zvAOBDWBZBESJWLBYR/sso/saml";
    static final String AUTHENTICATION_URL2 = "http://rain.okta1.com:1802/app/template_saml_2_0/k8lqGUUCIFIJHKUOGQKG/sso/saml";
    static final String CERTIFICATE =
            "[\n" +
            "[\n" +
            "  Version: V3\n" +
            "  Subject: EMAILADDRESS=info@okta.com, CN=bootstrap, OU=SSOProvider, O=Okta, L=San Francisco, ST=California, C=US\n" +
            "  Signature Algorithm: SHA1withRSA, OID = 1.2.840.113549.1.1.5\n" +
            "\n" +
            "  Key:  Sun RSA public key, 1024 bits\n" +
            "  modulus: 101438407598598116085679865987760095721749307901605456708912786847324207000576780508113360584555007890315805735307890113536927352312915634368993759211767770602174860126854831344273970871509573365292777620005537635317282520456901584213746937262823585533063042033441296629204165064680610660631365266976782082747\n" +
            "  public exponent: 65537\n" +
            "  Validity: [From: %s,\n" +
            "               To: %s]\n" +
            "  Issuer: EMAILADDRESS=info@okta.com, CN=bootstrap, OU=SSOProvider, O=Okta, L=San Francisco, ST=California, C=US\n" +
            "  SerialNumber: [    01294c35 ac03]\n" +
            "\n" +
            "]\n" +
            "  Algorithm: [SHA1withRSA]\n" +
            "  Signature:\n" +
            "0000: 26 44 3F 34 83 55 77 4A   23 1B EC 3B 6F A7 49 81  &D?4.UwJ#..;o.I.\n" +
            "0010: 7A 2F 73 5B 78 4B ED C0   D7 BA F7 58 03 5B B8 56  z/s[xK.....X.[.V\n" +
            "0020: 16 A3 FB 99 A3 89 A4 72   05 00 BC D8 A8 8C 4E 06  .......r......N.\n" +
            "0030: 31 D1 AD 7D 7C 5C 84 41   7C A9 A5 EE 3B F3 0D 42  1....\\.A....;..B\n" +
            "0040: 00 F4 00 9F C1 D7 00 B4   ED C1 80 62 D4 7F E5 CC  ...........b....\n" +
            "0050: 91 39 88 D2 C3 37 2B 9E   98 DF 19 41 4C 8C D4 CC  .9...7+....AL...\n" +
            "0060: 92 5E 5F 1B 55 C4 07 37   47 E3 83 D7 9F 5E 87 5B  .^_.U..7G....^.[\n" +
            "0070: 2F C5 B9 6B FE A9 32 82   EC 71 98 76 18 C2 BE 54  /..k..2..q.v...T\n" +
            "\n" +
            "]";
    static final long tsBefore = 1276883812000L;
    static final long tsAfter = 2223655072000L;

    private String certificate;
    private Configuration configuration;
    private TestsHelper helper;

    @BeforeTest
    public void setup() throws IOException, CertificateException, XPathExpressionException, SecurityPolicyException, ConfigurationException {
        DefaultBootstrap.bootstrap(); // initialize openSAML library

        helper = new TestsHelper();

        configuration = helper.loadConfig("/valid-config.xml");

        Date dateBefore = new Date(tsBefore);
        Date dateAfter = new Date(tsAfter);
        certificate = String.format(CERTIFICATE, dateBefore.toString(), dateAfter.toString());
    }

    @Test
    public void testConfiguration() throws IOException, XPathExpressionException, CertificateException {
        assertEquals(configuration.getApplications().size(), 2);

        Application application = configuration.getApplication(ISSUER1);
        String cert = certificate.replaceAll("[\r\n]+", "\n");

        assertEquals(application.getIssuer(), ISSUER1);
        assertEquals(application.getAuthenticationURL(), AUTHENTICATION_URL1);
        assertEquals(application.getCertificate().getX509Cert().toString().replaceAll("[\r\n]+", "\n"), cert);

        application = configuration.getApplication(ISSUER2);

        assertEquals(application.getIssuer(), ISSUER2);
        assertEquals(application.getAuthenticationURL(), AUTHENTICATION_URL2);
        assertEquals(application.getCertificate().getX509Cert().toString().replaceAll("[\r\n]+", "\n"), cert);

    }

    @Test
    public void testIpAccess() throws XPathExpressionException, IOException, CertificateException, SecurityPolicyException {
        assertTrue(configuration.isIpAllowedForOkta("192.168.3.10"));
        assertFalse(configuration.isIpAllowedForOkta("192.168.3.221"));

        assertTrue(configuration.isIpAllowedForSP("192.168.3.221"));
        assertTrue(configuration.isIpAllowedForSP("1.0.0.0"));
        assertTrue(configuration.isIpAllowedForSP("255.255.255.255"));

        Configuration configuration = helper.loadConfig("/valid-config-no-ips.xml");

        assertTrue(configuration.isIpAllowedForOkta("1.0.0.0"));
        assertTrue(configuration.isIpAllowedForOkta("255.255.255.255"));

        //it is allowed for okta since not allowed for service provider
        assertFalse(configuration.isIpAllowedForSP("1.2.3.4"));
    }
}
