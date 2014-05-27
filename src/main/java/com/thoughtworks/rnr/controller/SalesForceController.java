package com.thoughtworks.rnr.controller;

import com.thoughtworks.rnr.service.SalesForceService;
import org.apache.commons.httpclient.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
public class SalesForceController {

    private static final Logger logger = LoggerFactory.getLogger(SalesForceController.class);
    private SalesForceService salesForceService;

    @Autowired
    public SalesForceController(SalesForceService salesForceService) {
        this.salesForceService = salesForceService;
    }

    @RequestMapping(value = "/oauth/_callback", method = RequestMethod.GET)
    public ModelAndView sendPostRequestToSalesForceRequestingAccessToken(HttpServletRequest request, HttpClient client) throws JSONException {
        ModelMap model = new ModelMap();
        try {
            JSONObject authResponse = salesForceService.queryForAuthResponse(request, client);
            salesForceService.setAccessTokenAndInstanceURL(authResponse, request, client);
            String startDate = salesForceService.queryThoughtWorksStartDate(client, request.getSession());
            model.addAttribute("startDate", startDate);
        } catch (IOException e) {
            logger.debug("inside");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return new ModelAndView("home", "startDateModel", model);
    }

}
