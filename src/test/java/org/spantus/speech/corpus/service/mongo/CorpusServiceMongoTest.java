package org.spantus.speech.corpus.service.mongo;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.corpus.dto.CorpusEntry;
import org.spantus.speech.security.service.local.CorpusUserServiceLocal;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class CorpusServiceMongoTest {
	private static final Logger LOG = LoggerFactory.getLogger(CorpusServiceMongoTest.class);
	
	private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private MongoClient _mongo;
    
    private CorpusServiceMongo corpusServiceMongo;
	private MongoClientURI _clientUri;

	@Before
	public void beforeEach() throws Exception {

        MongodStarter runtime = MongodStarter.getDefaultInstance();
        _mongodExe = runtime.prepare(new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(12346, Network.localhostIsIPv6()))
            .build());
        _mongod = _mongodExe.start();
        _clientUri = new MongoClientURI("mongodb://localhost:12346/SpantusCorpus");
        _mongo = new MongoClient(_clientUri);
        CorpusClientMongo.setMongoClient(_mongo);
        CorpusClientMongo.setMongoClientUri(_clientUri);
        
        corpusServiceMongo = new CorpusServiceMongo(new CorpusUserServiceLocal());
	}

	@After
	public void afterEach() throws Exception {
		_mongod.stop();
        _mongodExe.stop();
	}

	@Test
	public void shouldCreateNewObjectInMongoDb() {
		// given
		CorpusEntry newEntry = newCorpusEntry("DESC1");

		// when
		CorpusEntry rtnEntry = corpusServiceMongo.createEntry(newEntry);

		// then
		Assert.assertEquals("manualTranscript matcheds","DESC1", rtnEntry.manualTranscript);
		Assert.assertNotNull("id is set",rtnEntry.id);
		Assert.assertNotNull("createdEntryOn is set",rtnEntry.createdEntryOn);
		Assert.assertNotNull("updatedEntryOn is set",rtnEntry.updatedEntryOn);
		Assert.assertEquals("updatedEntryOn is same as createdEntryOn",rtnEntry.updatedEntryOn, rtnEntry.createdEntryOn);
		
		Assert.assertTrue("Collection exists?", CorpusClientMongo.getDB().collectionExists(CorpusServiceMongo.COLLECTION_NAME));
		Assert.assertEquals("Collection exists?", 1, CorpusClientMongo.getDB().getCollection(CorpusServiceMongo.COLLECTION_NAME).find().size(),0);
	}
	
	@Test
	public void shouldFindTopNewObjectInMongoDb() {
		// given
		for (int i = 0; i <= 100; i++) {
			corpusServiceMongo.createEntry(newCorpusEntry(MessageFormat.format("D_{0}_DESC1", i)));	
		}
		
		// when
		List<CorpusEntry> rtnEntries = corpusServiceMongo.findTopEntries();
		

		for (CorpusEntry corpusEntry : rtnEntries) {
			LOG.debug("corpus desc {}, created: {}", corpusEntry.manualTranscript, corpusEntry.createdEntryOn);
		}
		
		// then
		Assert.assertEquals("manualTranscript matcheds",10, rtnEntries.size(),0);
		Assert.assertEquals("id is set","D_100_DESC1", rtnEntries.get(0).manualTranscript);
		Assert.assertEquals("id is set","D_91_DESC1", rtnEntries.get(9).manualTranscript);
	}
	
	@Test
	public void shouldFindByManualTranscriptNewObjectInMongoDb() {
		// given
		for (int i = 0; i <= 100; i++) {
			CorpusEntry entry = newCorpusEntry(MessageFormat.format("D_{0}_DESC1", i));
			entry.autoTranscript = MessageFormat.format("T_{0}_DESC1", i);
			corpusServiceMongo.createEntry(entry);	
		}
		
		// when
		List<CorpusEntry> rtnEntriesByDesc = corpusServiceMongo.findEntriesByText("D_0");
		List<CorpusEntry> rtnEntriesByTransc = corpusServiceMongo.findEntriesByText("T_0");
		
		// then
		Assert.assertEquals("manualTranscript matcheds",1, rtnEntriesByDesc.size(),0);
		Assert.assertEquals("id is set","D_0_DESC1", rtnEntriesByDesc.get(0).manualTranscript);
		
		Assert.assertEquals("manualTranscript matcheds",1, rtnEntriesByTransc.size(),0);
		Assert.assertEquals("id is set","T_0_DESC1", rtnEntriesByTransc.get(0).autoTranscript);
		
	}
	
	
	
	@Test
	public void shouldUpdateExistingObjectInMongoDb() {
		// given
		CorpusEntry newEntry = newCorpusEntry("DESC1");

		// when
		CorpusEntry createdEntry = corpusServiceMongo.createEntry(newEntry);
		createdEntry.manualTranscript = "DESC2";
		CorpusEntry updateEntry = corpusServiceMongo.updateEntry(createdEntry.id, createdEntry);

		// then
		Assert.assertEquals("manualTranscript matcheds","DESC2", updateEntry.manualTranscript);
		Assert.assertEquals("id is set",createdEntry.id, updateEntry.id);
		Assert.assertNotNull("createdEntryOn is set",updateEntry.createdEntryOn);
		Assert.assertNotNull("updatedEntryOn is set",updateEntry.updatedEntryOn);
		Assert.assertTrue("updatedEntryOn after createdEntryOn",updateEntry.updatedEntryOn.getTime() > updateEntry.createdEntryOn.getTime());
		Assert.assertTrue("Collection exists?", CorpusClientMongo.getDB().collectionExists(CorpusServiceMongo.COLLECTION_NAME));
		Assert.assertEquals("Collection exists?", 1, CorpusClientMongo.getDB().getCollection(CorpusServiceMongo.COLLECTION_NAME).find().size(),0);
	}
	
	
    private CorpusEntry newCorpusEntry(String manualTranscript) {
    	CorpusEntry corpusEntry = new CorpusEntry();
    	corpusEntry.manualTranscript = manualTranscript;
    	corpusEntry.fileName = "/"+manualTranscript;
//    	calendar.add(Calendar.DAY_OF_MONTH, 1);
//    	corpusEntry.createdEntryOn = calendar.getTime();
		return corpusEntry;
	}
    

	public MongoClient getMongoClient() {
        return _mongo;
    }
}
