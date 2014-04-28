package com.thoughtworks.rnr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller

public class SAMLController {

    //    TODO: http://sureshatt.blogspot.com/2012/11/how-to-read-saml-20-response-with.html

    @RequestMapping(value = "/auth/saml/callback", method = RequestMethod.POST)
    public String sendToHome(HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/home";
    }

}
