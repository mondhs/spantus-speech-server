package org.spantus.speech.security.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.security.dto.CorpusUser;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RepositoryRealm extends AuthorizingRealm {

	private static final Logger LOG = LoggerFactory
			.getLogger(RepositoryRealm.class);
	private CorpusUserService corpusUserService;

	@Inject
	public RepositoryRealm(CorpusUserService corpusUserService,
			CacheManager cacheManager,
			@Named("SHA1") HashedCredentialsMatcher credentialsMatcher) {
		super(cacheManager, credentialsMatcher);
		setCachingEnabled(true);
		this.corpusUserService = corpusUserService;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		if (principals.fromRealm(getName()).isEmpty()) {
			return null;
		}

		final String userName = (String) principals.fromRealm(getName())
				.iterator().next();
		LOG.debug("[doGetAuthorizationInfo] {}", userName);

		final CorpusUser user = findCorpusUserByName(userName);
		if (user == null) {
			return null;
		}

		final SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		for (String role : Splitter.on(",").omitEmptyStrings().trimResults().split(user.getUserRole())) {
			authzInfo.addRole(role);	
			LOG.debug("[doGetAuthorizationInfo]role: {}", role);
		}
		

		return authzInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

		final UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		final CorpusUser user = findCorpusUserByName(upToken.getUsername());
		LOG.debug("[doGetAuthenticationInfo] {}", upToken.getUsername());

		return user == null ? null : new SimpleAuthenticationInfo(
				user.getUserName(), user.getPassword(), getName());
	}

	private CorpusUser findCorpusUserByName(String userName) {
		CorpusUser user = corpusUserService.findCorpusUserByName(userName);
		return user;
	}

}
