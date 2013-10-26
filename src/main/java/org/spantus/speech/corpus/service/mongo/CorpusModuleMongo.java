package org.spantus.speech.corpus.service.mongo;

import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.audio.service.AudioNormalizerServiceImpl;
import org.spantus.speech.corpus.service.ICorpusService;
import org.spantus.speech.security.service.CorpusUserService;
import org.spantus.speech.security.service.mongo.CorpusUserServiceMongo;

import com.google.inject.AbstractModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


public class CorpusModuleMongo extends AbstractModule {
	private static final Logger LOG = LoggerFactory.getLogger(CorpusModuleMongo.class);
	private static final int FileMaxSize = 600000;
    @Override
    protected void configure() {
    	CorpusUserServiceMongo corpusUserServiceMongo = new CorpusUserServiceMongo();  
    	CorpusServiceMongo corpusService = new CorpusServiceMongo(corpusUserServiceMongo);
    	AudioNormalizerServiceImpl audioNormalizerServiceImpl = new AudioNormalizerServiceImpl();
    	audioNormalizerServiceImpl.setFileMaxSize(FileMaxSize);
    	corpusService.setAudioNormalizerService(audioNormalizerServiceImpl);
    	MongoClientURI clientUri = new MongoClientURI(getMongoURL());
    	try {
			CorpusClientMongo.setMongoClient(new MongoClient(clientUri));
			CorpusClientMongo.setMongoClientUri(clientUri);
		} catch (UnknownHostException e) {
			LOG.error("Unkonwn host",e);
			throw new IllegalArgumentException(e);
		}
        bind(ICorpusService.class).toInstance(corpusService);
        bind(CorpusUserService.class).toInstance(corpusUserServiceMongo);
    }
    private String getMongoURL() {
        String useMongoURL = "mongodb://localhost/SpantusRepo";
        String envMongoURL;
        String envBuildSecretDir;
        
        // Get MONGOHQ_URL_SPANTUS from :
        try {
            // 1. Jenkins build secret plugin
            if ((envBuildSecretDir = System.getenv("MONGO_SPANTUS_TEST")) != null) {
            	LOG.debug("Getting Build Secret from: " + envBuildSecretDir);
                FileInputStream propFile = new FileInputStream(envBuildSecretDir + "/" + "spantus-mongo.env");
                Properties p = new Properties(System.getProperties());
                p.load(propFile);
                System.setProperties(p);
                useMongoURL = System.getProperty("MONGOHQ_URL_SPANTUS");
                LOG.debug("MONGOHQ_URL_SPANTUS (from Build Secret): " + System.getProperty("MONGOHQ_URL_SPANTUS"));
            }
             
            // 2. System property
            else if ((envMongoURL = System.getProperty("MONGOHQ_URL_SPANTUS")) != null) {
            	LOG.debug("MONGOHQ_URL_SPANTUS (from system property): " + envMongoURL);
                useMongoURL = envMongoURL;
            }
            
            // 3, System environment
            else if ((envMongoURL = System.getenv("MONGOHQ_URL_SPANTUS")) != null){
            	LOG.debug("MONGOHQ_URL_SPANTUS (from system environment): " + envMongoURL);
                useMongoURL = envMongoURL;
            }
            
            // Error: MONGOHQ_URL_SPANTUS not set
            else {
            	LOG.error("MONGOHQ_URL_SPANTUS not set. Using url: {}",useMongoURL);
            }
        }
        catch (Exception e){
        	LOG.error(Arrays.toString(e.getStackTrace()));
        }
        return useMongoURL;
    }
}
