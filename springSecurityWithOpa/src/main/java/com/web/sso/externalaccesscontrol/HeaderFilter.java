package com.web.sso.externalaccesscontrol;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeaderFilter implements Filter {

    @Autowired
    private RequestTracker tracker;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        tracker.setMethod(req.getMethod());
        tracker.setUri(req.getRequestURI());
        chain.doFilter(request, response);
    }

}
