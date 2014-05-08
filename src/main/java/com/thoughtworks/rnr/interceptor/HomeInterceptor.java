package com.thoughtworks.rnr.interceptor;

import com.thoughtworks.rnr.service.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class HomeInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private String redirectUrl;
    private SAMLService samlService;

    @Autowired
    public HomeInterceptor(SAMLService samlService) {
        this.samlService = samlService;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession(false);
        if (isNull(session)) {
            redirect(httpServletResponse);
            return false;
        }
        return true;
    }

    private void redirect(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect(redirectUrl);
    }

    private boolean isNull(HttpSession session) {
        return session == null || session.getAttribute("user") == null;
    }
}
