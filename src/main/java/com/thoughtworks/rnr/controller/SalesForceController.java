package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SalesForceService;
import org.apache.commons.httpclient.HttpClient;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
public class SalesForceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceController.class);
    private SalesForceService salesForceService;

    @Autowired
    public SalesForceController(SalesForceService salesForceService) {
        this.salesForceService = salesForceService;
    }

    @RequestMapping(value = "/oauth/_callback", method = RequestMethod.GET)
    public String handleSalesForceCallback(HttpServletRequest request, HttpClient client) throws JSONException {
        try {
            setStartDateInSession(request, client);
        } catch (IOException | URISyntaxException e) {
            LOGGER.debug("Inside callback GET request from SalesForce: /oauth/_callback");
        }
        return "redirect:/home";
    }

    private void setStartDateInSession(HttpServletRequest request, HttpClient client) throws IOException, JSONException, URISyntaxException {
        String startDate = salesForceService.getStartDate(request, client);
        request.getSession().setAttribute("startDate", startDate);
    }
}