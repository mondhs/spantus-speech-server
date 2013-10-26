package org.spantus.speech.server;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.spantus.speech.corpus.rest.CorpusResource;
import org.spantus.speech.grammar.rest.LithuanianGrammarResource;
import org.spantus.speech.recognition.rest.RecognitionResource;
import org.spantus.speech.security.rest.UserResource;
import org.spantus.speech.security.service.ShiroSecurityModule;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestModule extends JerseyServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		install(new ShiroSecurityModule(getServletContext()));
		bindings();
		filters();
	}

	private void bindings() {
		bind(GuiceContainer.class);
		bind(UserResource.class);
		bind(CorpusResource.class);
		bind(LithuanianGrammarResource.class);
		bind(RecognitionResource.class);
		serve("/*")
		.with(GuiceContainer.class,
				ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING,
						"true"));
	}

	
	private void filters() {
		filter("/*").through(GuiceShiroFilter.class);
		filter("/api/recognize/*").through(ResponseCorsFilter.class);
	}

}
