package org.spantus.speech.corpus.service.local;

import org.spantus.speech.corpus.service.ICorpusService;

import com.google.inject.AbstractModule;


public class CorpusLocalModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ICorpusService.class).toInstance(new CorpusServiceMem());
    }
}
