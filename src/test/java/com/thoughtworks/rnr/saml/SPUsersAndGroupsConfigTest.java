package com.thoughtworks.rnr.saml;

import org.opensaml.DefaultBootstrap;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SPUsersAndGroupsConfigTest {
    private TestsHelper helper;
    private Configuration configuration;

    @BeforeTest
    public void setup() throws IOException, CertificateException, XPathExpressionException,
                               SecurityPolicyException, ConfigurationException {
        DefaultBootstrap.bootstrap(); // initialize openSAML library

        helper = new TestsHelper();
        configuration = helper.loadConfig("/valid-config-with-sp-users.xml");
    }

    @Test
    public void testUsersInSPRange() {
        assertFalse(configuration.isUsernameAllowedForOkta("john.doe@acme.com"));
        assertTrue(configuration.isUsernameAllowedForOkta("no-such-user-in-confluence-list"));
    }

    @Test
    public void testGroupsInSPRange() {
        assertFalse(configuration.isInSPGroups(Arrays.asList("no-such-group")));
        assertTrue(configuration.isInSPGroups(Arrays.asList("jira-grp")));
        assertTrue(configuration.isInSPGroups(Arrays.asList("confluence-grp")));
    }
}
