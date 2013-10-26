package org.spantus.speech.security.rest;

import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.security.dto.CorpusUser;
import org.spantus.speech.security.service.CorpusUserService;

import com.google.common.base.Strings;
import com.google.inject.Inject;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/user")
public class UserResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(UserResource.class);

	CorpusUserService corpusUserService;

	@Inject
	public UserResource(CorpusUserService corpusUserService) {
		this.corpusUserService = corpusUserService;
	}

	@POST
	@Path("/login")
	public Response loginUser(@FormParam("userName") String userName,
			@FormParam("password") String password) throws Exception {
		LOG.error("[loginUser] userName {}. Passwrod empty?: {}", userName,Strings.isNullOrEmpty(password) );
		UsernamePasswordToken token = new UsernamePasswordToken(userName,
				password);
//		token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		subject.login(token);
		subject.getSession(true);
		retrieveCurrentUserInfo();
		return Response.ok().build();
	}
	
	@GET
	@Path("/userName")
	public String retrieveCurrentUserName() throws Exception {
		Subject subject = SecurityUtils.getSubject();
		LOG.error("[retrieveCurrentUserInfo] subject {}", subject);
		LOG.error("[retrieveCurrentUserInfo] Principal {}",
				subject.getPrincipal());
		LOG.error("[retrieveCurrentUserInfo] Principals {}",
				subject.getPrincipals());
		LOG.error("[retrieveCurrentUserInfo] Session ID {}", subject
				.getSession().getId());
		LOG.error("[retrieveCurrentUserInfo] Session Host {}", subject
				.getSession().getHost());
		LOG.error("[retrieveCurrentUserInfo] Session Atributes {}", subject
				.getSession().getAttributeKeys());
		if(subject.getPrincipal() != null){
			return subject.getPrincipal().toString();
		}
		return null;
		
	}
	
	@GET
	@Path("/info")
	public CorpusUser retrieveCurrentUserInfo() throws Exception {
		Subject subject = SecurityUtils.getSubject();
		String userName = "" + subject.getPrincipal();
		if (!Strings.isNullOrEmpty(userName)) {
			CorpusUser user = corpusUserService.findCorpusUserByName(userName);
			if (user != null) {
				user.setPassword("");
			}
			return user;
		}
		return null;
	}

	@GET
	@Path("/logout")
	public Response logout() throws Exception {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		LOG.error("[logout] subject {}", subject);
		return Response.status(Status.UNAUTHORIZED).build();
	}

}
