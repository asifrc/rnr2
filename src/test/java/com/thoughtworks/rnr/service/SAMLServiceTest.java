package com.thoughtworks.rnr.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SAMLServiceTest {

    private final String oktaRedirectURL = "https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml";
    private SAMLService samlService;

    @Before
    public void setup() {
        samlService = new SAMLService();
    }

    @Test
    public void shouldReturnRedirectURL() {
        assertThat(samlService.oktaRedirectURL(), is(oktaRedirectURL));
    }
}
