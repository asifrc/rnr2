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
import javax.servlet.http.HttpServletResponse;
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
    public String sendPostRequestToSalesForceRequestingAccessToken(HttpServletRequest request, HttpClient client, HttpServletResponse response) throws JSONException {
        try {
            setStartDateInSession(request, client);
        } catch (IOException e) {
            logger.debug("inside");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "redirect:/home";
    }

    private void setStartDateInSession(HttpServletRequest request, HttpClient client) throws IOException, JSONException, URISyntaxException {
        String startDate = salesForceService.getStartDate(request, client);
        request.getSession().setAttribute("startDate", startDate);
    }

}