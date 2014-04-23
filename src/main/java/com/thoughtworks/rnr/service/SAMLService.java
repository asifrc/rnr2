package com.thoughtworks.rnr.service;

import com.thoughtworks.rnr.model.ConfigurationParser;
import com.thoughtworks.rnr.saml.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SAMLService {

    private final String oktaRedirectURL = "https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml?SAMLRequest=";

    public String createSAMLRequest() {
//        Configuration configuration = configurationParser.parse();
//        return oktaRedirectURL + configuration.getRequest();
        return oktaRedirectURL;
    }
}

