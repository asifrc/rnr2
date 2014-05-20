package com.thoughtworks.rnr.interceptor;

import com.thoughtworks.rnr.service.SAMLService;
import org.junit.Before;
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
    private String redirectUrl;
    private final String SESSION_KEY = "okta.rnr.user";

    @Mock
    private SAMLService mockSAMLService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpSession mockHttpSession;
    @Mock
    private Object handler;

    private HomeInterceptor homeInterceptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        redirectUrl = "redirectURL";
        homeInterceptor = new HomeInterceptor(redirectUrl);
    }

    @Test
    public void shouldRedirectToOktaOnAGetRequest() throws Exception {
        homeInterceptor.preHandle(mockRequest, mockResponse, handler);
        verify(mockResponse).sendRedirect(redirectUrl);
    }

    @Test
    public void shouldRedirectToOKTALoginWhenNoSessionExists() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(null);

        homeInterceptor.preHandle(mockRequest, mockResponse, new Object());

        verify(mockResponse).sendRedirect(redirectUrl);
    }

    @Test
    public void shouldPassThroughToHomeControllerWhenAUserKeyIsSetInTheSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockHttpSession);
        when(mockHttpSession.getAttribute(SESSION_KEY)).thenReturn("a string");

        boolean interceptorReturn = homeInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(0)).sendRedirect(anyString());
        assertTrue(interceptorReturn);
    }

    @Test
    public void shouldRedirectToOKTALoginWhenNoPrincipalIsAttachedToTheSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockHttpSession);
        when(mockHttpSession.getAttribute(SESSION_KEY)).thenReturn(null);
        shouldRedirectToSAMLRequest();
    }

    private void shouldRedirectToSAMLRequest() throws Exception {
        boolean interceptorReturn = homeInterceptor.preHandle(mockRequest, mockResponse, handler);

        assertFalse(interceptorReturn);
        verify(mockResponse).sendRedirect(redirectUrl);
    }
}
