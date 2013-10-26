package org.spantus.speech.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;


@Singleton
public class ResponseCorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse alteredResponse = ((HttpServletResponse) servletResponse);
            addHeadersFor200Response(servletRequest, alteredResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void addHeadersFor200Response(ServletRequest servletRequest, HttpServletResponse response) {
    	response.addHeader("Access-Control-Allow-Origin", "http://mondhs.github.io");
//        response.addHeader("Access-Control-Allow-Origin", "http://localhost:4000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
        response.addHeader("Access-Control-Allow-Headers", "GET, PUT, OPTIONS, X-XSRF-TOKEN, Authorization");
    }
}
