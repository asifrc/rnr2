package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller

public class SAMLController {

    @Autowired
    private SAMLService samlService;

    @Autowired
    public SAMLController (SAMLService samlService) {
        this.samlService = samlService;
    }

    //    TODO: http://sureshatt.blogspot.com/2012/11/how-to-read-saml-20-response-with.html

    @RequestMapping(value = "/auth/saml/callback", method = RequestMethod.POST)
    public String handleOKTACallback(HttpServletRequest request) {
        String oktaResponse = request.getParameter("SAMLResponse");
//        try {
//            SAMLResponse samlResponse = samlService.getSAMLResponse(oktaResponse);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (SecurityPolicyException e) {
//            e.printStackTrace();
//        }
        return "redirect:/home";
    }

}
