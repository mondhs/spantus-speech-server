package org.spantus.speech.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.spantus.speech.corpus.service.local.CorpusLocalModule;
import org.spantus.speech.corpus.service.mongo.CorpusModuleMongo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;


public class GuiceRestConfig extends GuiceServletContextListener {
    private ServletContext sc;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.sc = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        AbstractModule dictionaryModule = loadDictionaryModule();
        return Guice.createInjector(new RestModule(), dictionaryModule);
    }

    private AbstractModule loadDictionaryModule() {
        String mode = this.sc.getInitParameter("mode");
        sc.log("Application mode: " + mode);
        if (mode != null && mode.equals("local")) {
        	return new CorpusLocalModule();
        } else {
        	return new CorpusModuleMongo();
            
        }
    }
}
