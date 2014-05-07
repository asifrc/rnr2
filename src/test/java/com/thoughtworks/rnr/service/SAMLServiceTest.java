package com.thoughtworks.rnr.service;

import org.junit.Before;

public class SAMLServiceTest {

    private final String oktaRedirectURL = "https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml";
    private SAMLService samlService;

    @Before
    public void setup() {
        samlService = new SAMLService();
    }

}
