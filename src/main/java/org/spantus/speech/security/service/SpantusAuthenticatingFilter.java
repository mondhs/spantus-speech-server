package org.spantus.speech.security.service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpantusAuthenticatingFilter extends FormAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger( SpantusAuthenticatingFilter.class );

    @Override
    protected boolean onAccessDenied(ServletRequest request,
    		ServletResponse response) throws Exception {
//    	super.onAccessDenied(request, response);
    	Subject subject = SecurityUtils.getSubject();
    	if(subject!= null){
    		subject.getSession(true);
    		LOG.debug("[onAccessDenied] " + subject);
    	}
    	LOG.debug("[onAccessDenied] no subject");
    	((HttpServletResponse)response).setStatus(401);
    	return false;
    }

}
