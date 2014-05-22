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

@Controller
public class SalesForceController {

    private static final Logger logger = LoggerFactory.getLogger(SalesForceController.class);
    private SalesForceService salesForceService;

    @Autowired
    public SalesForceController(SalesForceService salesForceService) {
        this.salesForceService = salesForceService;
    }

    @RequestMapping(value = "/oauth/_callback", method = RequestMethod.GET)
    public String sendPostRequestToSalesForceRequestingAccessToken(HttpServletRequest request, HttpClient client) throws JSONException {
        try {
            salesForceService.buildAndSendPostRequest(request, client);
        } catch (IOException e) {
            logger.debug("inside");
        }
        return "home";
    }
}
