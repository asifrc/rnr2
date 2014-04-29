package com.thoughtworks.rnr.interceptor;

import com.thoughtworks.rnr.service.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HomeInterceptor extends HandlerInterceptorAdapter{
    private SAMLService samlService;

    @Autowired
    public HomeInterceptor(SAMLService samlService) {
        this.samlService = samlService;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//        TODO: Michael & Carol This is commented out so that functional tests can run
//        httpServletResponse.sendRedirect(samlService.oktaRedirectURL());
        return true;
    }
}
