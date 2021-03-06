package com.thoughtworks.rnr.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//https://developer.salesforce.com/page/Getting_Started_with_the_Force.com_REST_API#Using_the_Force.com_REST_API

@Service
public class SalesForceService {
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String INSTANCE_URL = "INSTANCE_URL";
    private static final String CLIENT_ID = "3MVG9Iu66FKeHhINkDZtpSFwPuzIarpL2Rs3AbfckOpkZhCvwKTdcDPUSkZUIESoKIrsbp9ugHPK3KqJXlA_R";
    private static final String CLIENT_SECRET = "4233795443642531062";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth/_callback";
    private static final String ENVIRONMENT = "https://test.salesforce.com";
    private static final String TOKEN_URL = ENVIRONMENT + "/services/oauth2/token";
    private static final String START_DATE_QUERY = "SELECT pse__Start_Date__c from Contact WHERE email = ";
    private String authUrl;

    private static final String OLD_QUERY_FOR_REFERENCE_ONLY = "SELECT Contact.pse__Start_Date__c, " +
            "(SELECT pse__Timecard_Header__c.pse__Total_Hours__c " +
            "FROM Contact.pse__Timecards__r) " +
            "FROM Contact " +
            "WHERE Contact.Email = '";

    public SalesForceService() throws UnsupportedEncodingException {
        authUrl = ENVIRONMENT
                + "/services/oauth2/authorize?response_type=code&client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");
    }

    public void authenticateWithSalesForce(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
        if (accessToken == null) {
            response.sendRedirect(authUrl);
        }
    }

    public String fetchStartDate(HttpServletRequest request, HttpClient client) throws IOException, JSONException, URISyntaxException {
        JSONObject authResponse = requestAuthResponseFromSalesForce(request, client);
        setAccessTokenAndInstanceURLInSession(authResponse, request);
        return queryThoughtWorksStartDate(client, request.getSession());
    }

    private JSONObject requestAuthResponseFromSalesForce(HttpServletRequest request, HttpClient httpClient) throws IOException, JSONException {
        PostMethod postMethod = buildHttpPostMethod(request);
        httpClient.executeMethod(postMethod);
        JSONObject authResponse = parseHttpResponseIntoJSON(postMethod);
        return authResponse;
    }

    private PostMethod buildHttpPostMethod(HttpServletRequest request) {
        PostMethod postMethod = new PostMethod(TOKEN_URL);
        postMethod.addParameter("code", request.getParameter("code"));
        postMethod.addParameter("grant_type", "authorization_code");
        postMethod.addParameter("client_id", CLIENT_ID);
        postMethod.addParameter("client_secret", CLIENT_SECRET);
        postMethod.addParameter("redirect_uri", REDIRECT_URI);
        return postMethod;
    }

    private void setAccessTokenAndInstanceURLInSession(JSONObject authResponse, HttpServletRequest request) throws JSONException {
        String accessToken = authResponse.getString("access_token");
        String instanceURL = authResponse.getString("instance_url");
        request.getSession().setAttribute(ACCESS_TOKEN, accessToken);
        request.getSession().setAttribute(INSTANCE_URL, instanceURL);
    }

    private String queryThoughtWorksStartDate(HttpClient httpClient, HttpSession session) throws URISyntaxException, IOException, JSONException {
        GetMethod getMethod = setupHttpGetMethod(session);

        httpClient.executeMethod(getMethod);

        JSONObject jsonObject = parseHttpResponseIntoJSON(getMethod);
        String startDate = getStartDateFromJSON(jsonObject);
        return formatDate(startDate);
    }

    private GetMethod setupHttpGetMethod(HttpSession session) {
        GetMethod getMethod = new GetMethod(session.getAttribute(INSTANCE_URL) + "/services/data/v29.0/query");
        String accessToken = (String) session.getAttribute(ACCESS_TOKEN);
        getMethod.setRequestHeader("Authorization", "OAuth " + accessToken);
        NameValuePair[] params = createQueryString(session);
        getMethod.setQueryString(params);
        return getMethod;
    }

    private JSONObject parseHttpResponseIntoJSON(PostMethod postMethod) throws JSONException, IOException {
        return new JSONObject(new JSONTokener(new InputStreamReader(postMethod.getResponseBodyAsStream())));
    }

    private JSONObject parseHttpResponseIntoJSON(GetMethod getMethod) throws JSONException, IOException {
        return new JSONObject(new JSONTokener(new InputStreamReader(getMethod.getResponseBodyAsStream())));
    }

    private NameValuePair[] createQueryString(HttpSession session) {
        NameValuePair[] params = new NameValuePair[1];
        String userEmail = (String) (session.getAttribute("UserEmail"));
        params[0] = new NameValuePair("q", START_DATE_QUERY + "'" + userEmail + "'");
        return params;
    }

    private String getStartDateFromJSON(JSONObject jsonObject) throws JSONException, IOException {
        JSONArray results = jsonObject.getJSONArray("records");
        return results.getJSONObject(0).getString("pse__Start_Date__c");
    }

    private String formatDate(String startDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = originalFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(date);
    }
}