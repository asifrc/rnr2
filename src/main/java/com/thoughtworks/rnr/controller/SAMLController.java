package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import com.thoughtworks.rnr.service.SalesForceService;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;

@Controller
public class SAMLController {

    private SAMLService samlService;
    private SalesForceService salesForceService;

    @Autowired
    public SAMLController(SAMLService samlService, SalesForceService salesForceService) {
        this.samlService = samlService;
        this.salesForceService = salesForceService;
    }

    @RequestMapping(value = "/auth/saml/callback", method = RequestMethod.POST)
    public String handleOKTACallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            String samlResponse = request.getParameter("SAMLResponse");
            samlService.setSessionWhenSAMLResponseIsValid(request, samlResponse);
            salesForceService.authenticateWithSalesForce(request, response);
            return null;
        }
        catch (IOException | UnmarshallingException | ValidationException | ParserConfigurationException | SAXException | SecurityPolicyException | CertificateException e) {
            return "sorry";
        }
        return "sorry";
    }
}