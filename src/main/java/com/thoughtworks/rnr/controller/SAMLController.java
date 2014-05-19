package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;

@Controller

public class SAMLController {

    private SAMLService samlService;

    @Autowired
    public SAMLController (SAMLService samlService) {
        this.samlService = samlService;
    }

    //    TODO: http://sureshatt.blogspot.com/2012/11/how-to-read-saml-20-response-with.html

    @RequestMapping(value = "/auth/saml/callback", method = RequestMethod.POST)
    public String handleOKTACallback(HttpServletRequest request) throws IOException, CertificateException, UnmarshallingException, ValidationException, ParserConfigurationException, SAXException, SecurityPolicyException {
        String oktaResponse = request.getParameter("SAMLResponse");
//        SAMLService2 samlService2 = new SAMLService2();
//        Principal user = samlService2.verifyOKTASignOn(oktaResponse);
//        samlService2.putPrincipalInSessionContext(request, user);

        return "redirect:/home";
    }



}
