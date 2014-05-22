package com.thoughtworks.rnr.service;


import com.thoughtworks.rnr.factory.JSONObjectFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

//https://developer.salesforce.com/page/Getting_Started_with_the_Force.com_REST_API#Using_the_Force.com_REST_API

@Service
public class SalesForceService {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String INSTANCE_URL = "INSTANCE_URL";
    private static final String CLIENT_ID = "3MVG9Iu66FKeHhINkDZtpSFwPuzIarpL2Rs3AbfckOpkZhCvwKTdcDPUSkZUIESoKIrsbp9ugHPK3KqJXlA_R";
    private static final String CLIENT_SECRET = "4233795443642531062";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth/_callback";
    private static final String ENVIRONMENT = "https://test.salesforce.com";
    private static final String tokenUrl = ENVIRONMENT + "/services/oauth2/token";
    private final JSONObjectFactory jsonObjectFactory;
    private String authUrl = null;
    private String userEmail;
    private static final String START_DATE_QUERY = "SELECT Contact.pse__Start_Date__c, " +
            "(SELECT pse__Timecard_Header__c.pse__Total_Hours__c " +
            "FROM Contact.pse__Timecards__r) " +
            "FROM Contact " +
            "WHERE Contact.Email = '";

    @Autowired
    public SalesForceService(JSONObjectFactory jsonObjectFactory) throws UnsupportedEncodingException {
        this.jsonObjectFactory = jsonObjectFactory;
        authUrl = ENVIRONMENT
                + "/services/oauth2/authorize?response_type=code&client_id="
                + CLIENT_ID
                + "&redirect_uri="
                + URLEncoder.encode(REDIRECT_URI, "UTF-8");
    }

    public void authenticateWithSalesForce(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
        if (accessToken == null) {
            response.sendRedirect(authUrl);
        }
    }

    public void buildAndSendPostRequest(HttpServletRequest request, HttpClient httpClient) throws IOException, JSONException {
        String code = request.getParameter("code");
        PostMethod httpPost = new PostMethod(tokenUrl);

        httpPost.addParameter("code", code);
        httpPost.addParameter("grant_type", "authorization_code");
        httpPost.addParameter("client_id", CLIENT_ID);
        httpPost.addParameter("client_secret", CLIENT_SECRET);
        httpPost.addParameter("redirect_uri", REDIRECT_URI);

        httpClient.executeMethod(httpPost);

        JSONObject authResponse = new JSONObject(new JSONTokener(new InputStreamReader(httpPost.getResponseBodyAsStream())));
        setAccessTokenAndInstanceURL(authResponse, request, httpClient);
    }

    public void setAccessTokenAndInstanceURL(JSONObject responseJSON, HttpServletRequest request, HttpClient httpClient) throws JSONException {
        String accessToken = responseJSON.getString("access_token");
        String instanceURL = responseJSON.getString("instance_url");
        request.getSession().setAttribute(ACCESS_TOKEN, accessToken);
        request.getSession().setAttribute(INSTANCE_URL, instanceURL);

        try {
            queryThoughtWorksStartDate(httpClient, instanceURL, accessToken);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String queryThoughtWorksStartDate(HttpClient httpClient, String instanceUrl, String accessToken) throws URISyntaxException, IOException, JSONException {
        String query = START_DATE_QUERY + userEmail + "'";

        GetMethod get = new GetMethod(instanceUrl + "/services/data/v29.0/query");
        get.setRequestHeader("Authorization", "OAuth " + accessToken);
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("q", query);
        get.setQueryString(params);

        httpClient.executeMethod(get);
        getStartDateFromJsonOb(get);
        return "home";
    }

    private String getStartDateFromJsonOb(GetMethod get) throws JSONException, IOException {
        JSONObject authResponse = new JSONObject(new JSONTokener(new InputStreamReader(get.getResponseBodyAsStream())));
        JSONArray results = authResponse.getJSONArray("records");
        return results.getJSONObject(0).getString("pse__Start_Date__c");

    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

