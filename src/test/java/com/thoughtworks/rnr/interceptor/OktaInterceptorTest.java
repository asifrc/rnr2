package com.thoughtworks.rnr.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class OktaInterceptorTest {

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Object object;

    private OktaInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        interceptor = new OktaInterceptor();
    }

    @Test
    public void shouldSetAUserSession() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);
        when(mockRequest.getSession(true)).thenReturn(mockSession);

        interceptor.preHandle(mockRequest, mockResponse, object);

        verify(mockSession).setAttribute("user", "Value");
    }
}