package com.thoughtworks.rnr.interceptor;

import com.thoughtworks.rnr.model.Constants;
import com.thoughtworks.rnr.service.SAMLService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class HomeInterceptorTest {
    @Mock
    SAMLService mockSAMLService;
    @Mock
    HttpServletRequest mockRequest;
    @Mock
    HttpServletResponse mockResponse;
    @Mock
    HttpSession mockHttpSession;
    @Mock
    Object handler;
    private HomeInterceptor homeInterceptor;
    private String oktaRedirectUrl = Constants.OKTA_REDIRECT_URL;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        homeInterceptor = new HomeInterceptor(mockSAMLService);
    }

    @Test
    public void shouldRedirectToOktaOnAGetRequest() throws Exception {
        homeInterceptor.preHandle(mockRequest, mockResponse, handler);
        oktaRedirectUrl = Constants.OKTA_REDIRECT_URL;
        verify(mockResponse).sendRedirect(oktaRedirectUrl);
    }

    @Test
    public void shouldRedirectToOKTALoginWhenNoSessionExists() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(null);

        homeInterceptor.preHandle(mockRequest, mockResponse, new Object());

        verify(mockResponse).sendRedirect(oktaRedirectUrl);
    }

    @Test
    public void shouldPassThroughToHomeControllerWhenAUserKeyIsSetInTheSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockHttpSession);
        when(mockHttpSession.getAttribute("user")).thenReturn("a string");

        boolean interceptorReturn = homeInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(0)).sendRedirect(anyString());
        assertTrue(interceptorReturn);
    }
    @Ignore
    @Test
    public void shouldRedirectToOKTALoginWhenNoPrincipalIsAttachedToTheSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockHttpSession);
        when(mockHttpSession.getAttribute("user")).thenReturn(null);
        shouldRedirectToSAMLRequest();
    }

    private void shouldRedirectToSAMLRequest() throws Exception {
        String mockSAMLRequest = "SAMLRequest";
        when(oktaRedirectUrl).thenReturn(mockSAMLRequest);

        boolean interceptorReturn = homeInterceptor.preHandle(mockRequest, mockResponse, handler);

        assertFalse(interceptorReturn);
        verify(mockResponse).sendRedirect(mockSAMLRequest);
    }
}
