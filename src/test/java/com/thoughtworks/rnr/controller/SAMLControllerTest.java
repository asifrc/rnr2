package com.thoughtworks.rnr.controller;

import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by kpradhan on 4/24/14.
 */
public class SAMLControllerTest {

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    SAMLController samlController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        samlController = new SAMLController();
    }

    @Test
    public void sendToHomeShouldGetSAMLResponseFromRequest() {
        samlController.sendToHome(httpServletRequest, httpServletResponse);
        verify(httpServletRequest).getParameter("SAMLResponse");
    }
}
