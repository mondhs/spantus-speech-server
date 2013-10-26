package org.spantus.speech.security.service;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public class ShiroSecurityModule extends ShiroWebModule {

private static final Key<SpantusAuthenticatingFilter> VERBOSE_AUTH = Key.get(SpantusAuthenticatingFilter.class);
	
	public ShiroSecurityModule(ServletContext servletContext) {
		super(servletContext);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configureShiroWeb() {
		try {
			bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));

			bind(CacheManager.class).to(MemoryConstrainedCacheManager.class);
			expose(CacheManager.class);

			bind(AuthenticationStrategy.class)
					.to(FirstSuccessfulStrategy.class);
			expose(AuthenticationStrategy.class);
			bind(HashedCredentialsMatcher.class).annotatedWith(
					Names.named("SHA1")).toInstance(
					new HashedCredentialsMatcher("SHA1"));
			expose(HashedCredentialsMatcher.class).annotatedWith(
					Names.named("SHA1"));
			bindRealm().to(RepositoryRealm.class);
		} catch (NoSuchMethodException e) {
			addError(e);
		}
		
		addFilterChain("/api/grammar/**", ANON, config(NO_SESSION_CREATION, "true"));
		addFilterChain("/api/user/**", ANON);
		addFilterChain("/api/corpus/**", VERBOSE_AUTH, config(NO_SESSION_CREATION, "true"), config(ROLES, "ADMIN"));
		addFilterChain("/api/recognize/entry/**", VERBOSE_AUTH, config(NO_SESSION_CREATION, "true"), config(ROLES, "USER"));
		addFilterChain("/api/recognize/**", ANON);
		addFilterChain("/**", ANON, config(NO_SESSION_CREATION, "true"));
	}

	@Provides
	Ini loadShiroIni() {
		return Ini.fromResourcePath("classpath:shiro.ini");
	}

}
