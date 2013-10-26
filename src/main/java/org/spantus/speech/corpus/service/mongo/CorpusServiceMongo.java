package org.spantus.speech.corpus.service.mongo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.audio.service.AudioNormalizerService;
import org.spantus.speech.corpus.dto.CorpusEntry;
import org.spantus.speech.corpus.service.ICorpusService;
import org.spantus.speech.security.dto.CorpusUser;
import org.spantus.speech.security.service.CorpusUserService;

import com.google.common.base.Strings;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class CorpusServiceMongo implements ICorpusService {
	private static final Logger LOG = LoggerFactory
			.getLogger(CorpusServiceMongo.class);
	public static final String COLLECTION_NAME = "SpantusCorpus";

	private AudioNormalizerService audioNormalizerService;
	private CorpusUserService corpusUserService;
	
	public CorpusServiceMongo(CorpusUserService corpusUserService){
		this.corpusUserService = corpusUserService;
	}
	

	@Override
	public CorpusEntry createEntry(CorpusEntry corpusEntry) {
		LOG.debug("[createEntry] new entry {}", corpusEntry);
		corpusEntry.createdEntryOn = Calendar.getInstance().getTime();
		CorpusUser user = corpusUserService.findCurrent();
		corpusEntry.userName = user.getUserName();
		corpusEntry.userHost = user.getHost();
		corpusEntry.updatedEntryOn = corpusEntry.createdEntryOn;
		CorpusEntryMongo resource = newCorpusEntryMongo(corpusEntry);
		DBObject object = resource.toDbObject();
		findCollection().save(object);
		CorpusEntry entry = newCorpusEntry(CorpusEntryMongo
				.fromDbObject(object));
		LOG.debug("[createEntry] created {}", corpusEntry);
		return entry;
	}

	@Override
	public CorpusEntry updateEntry(String id, CorpusEntry corpusEntry) {
		LOG.debug("[updateEntry] {}:{}", id, corpusEntry);
		corpusEntry.updatedEntryOn =  Calendar.getInstance().getTime();
		CorpusUser user = corpusUserService.findCurrent();
		corpusEntry.userName = user.getUserName();
		corpusEntry.userHost = user.getHost();
		CorpusEntryMongo resource = updateEntryWithFileDetails(id, corpusEntry,
				corpusEntry.fileName);

		return newCorpusEntry(resource);

	}

	@Override
	public CorpusEntry findEntryById(String id) {
		LOG.debug("[findEntryById] {}", id);
		CorpusEntry corpusEntry = null;
		try {
			CorpusEntryMongo mEntry = CorpusEntryMongo
					.fromDbObject(findCollection().findOne(
							new BasicDBObject("_id", new ObjectId(id))));
			corpusEntry = newCorpusEntry(mEntry);
		} catch (IllegalArgumentException e) {
			LOG.error("entry cannot be retrieved {}", e.getMessage());
		}
		return corpusEntry;
	}

	@Override
	public List<CorpusEntry> findTopEntries() {
		LOG.debug("[findAllEntries] +++");
		List<CorpusEntry> corpus = new ArrayList<CorpusEntry>();
		DBCursor collection = findCollection().find();
		collection.sort(new BasicDBObject("_id", -1));
		collection.limit(10);
		
		for (DBObject object : collection) {
			CorpusEntryMongo mEntry = CorpusEntryMongo.fromDbObject(object);
			CorpusEntry corpusEntry = newCorpusEntry(mEntry);
			corpus.add(corpusEntry);
		}
		LOG.debug("[findAllEntries] --- found {}", corpus.size());
		return corpus;
	}
	
	@Override
	public Integer countEntries() {
		return findCollection().find().count();
	}
	
	@Override
	public List<CorpusEntry> findEntriesByText(String searchText){
		LOG.debug("[findAllEntries] +++");
		List<CorpusEntry> corpus = new ArrayList<CorpusEntry>();
		BasicDBObject whereQuery = null;
		if(!Strings.isNullOrEmpty(searchText)){
			
			DBObject clauseManualTranscript = new BasicDBObject("manualTranscript", new BasicDBObject("$regex", "^"+searchText+".*$")
			.append("$options", "i"));  
			DBObject clauseAutoTranscript = new BasicDBObject("autoTranscript", new BasicDBObject("$regex", "^"+searchText+".*$")
			.append("$options", "i")); 
			BasicDBList or = new BasicDBList();
			or.add(clauseManualTranscript);
			or.add(clauseAutoTranscript);
			whereQuery = new BasicDBObject("$or", or);
		}else{
			whereQuery = new BasicDBObject(); 
		}
		DBCursor collection = findCollection().find(whereQuery);
		collection.sort(new BasicDBObject("_id", -1));
		collection.limit(10);
		
		for (DBObject object : collection) {
			CorpusEntryMongo mEntry = CorpusEntryMongo.fromDbObject(object);
			CorpusEntry corpusEntry = newCorpusEntry(mEntry);
			corpus.add(corpusEntry);
		}
		LOG.debug("[findAllEntries] --- found {}", corpus.size());
		return corpus;
	}	

	@Override
	public CorpusEntry createEntry(CorpusEntry corpusEntry,
			InputStream inputStream) {
		LOG.debug("[createEntry] +++ entry {} with stream {}", corpusEntry,
				inputStream);
		CorpusEntry rtnCorpusEntry = createEntry(corpusEntry);
		String fileNameById = createName(rtnCorpusEntry.id);
		GridFSInputFile audioForInput = createFile(inputStream, fileNameById);
		audioForInput.save();
		String fileName = audioForInput.getFilename();
		CorpusEntryMongo resource = updateEntryWithFileDetails(
				rtnCorpusEntry.id, rtnCorpusEntry, fileName);
		rtnCorpusEntry = newCorpusEntry(resource);
		LOG.debug("[createEntry] --- entry {}", rtnCorpusEntry);

		return rtnCorpusEntry;
	}

	/**
	 * 
	 * @param id
	 * @param corpusEntry
	 * @param fileName
	 * @return
	 */
	private CorpusEntryMongo updateEntryWithFileDetails(String id,
			CorpusEntry corpusEntry, String fileName) {
		GridFS gridFS = new GridFS(CorpusClientMongo.getDB());
		GridFSDBFile file = gridFS.findOne(fileName);
		if (file == null) {
			LOG.error("File does not exists {}", fileName);
		} else {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			retrieveAudioStreamByName(fileName, output);
			AudioInputStream ais = createInputStream(output);
			AudioFormat format = ais.getFormat();

			corpusEntry.fileName = fileName;
			corpusEntry.fileSize = file.getLength();
			corpusEntry.createdEntryOn = file.getUploadDate();
			corpusEntry.sampleRate = format.getSampleRate();
			corpusEntry.lengthInSec = ais.getFrameLength()
					/ format.getFrameRate();
			corpusEntry.channels = format.getChannels();
			corpusEntry.sampleSizeInBits = format.getSampleSizeInBits();
		}

		CorpusEntryMongo resource = newCorpusEntryMongo(corpusEntry);
		resource.setId(id);
		findCollection().save(resource.toDbObject());
		return resource;
	}

	private AudioInputStream createInputStream(ByteArrayOutputStream output) {
		LOG.debug("[createInputStream]");
		try {
			// Files.write(output.toByteArray(), new File("/tmp/test2.wav"));
			// if(true){
			// return null;
			// }
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new ByteArrayInputStream(output
							.toByteArray()));
			LOG.debug("[createInputStream] audio format {}",
					audioInputStream.getFormat());
			return audioInputStream;
		} catch (UnsupportedAudioFileException e) {
			LOG.error("format not supported", e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LOG.error("I/O error", e);
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public void retrieveAudioStreamByName(String fileName, OutputStream output) {
		LOG.debug("[retrieveAudioStreamByName] fileName {}", fileName);
		GridFS gridFS = new GridFS(CorpusClientMongo.getDB());
		GridFSDBFile file = gridFS.findOne(fileName);
		if (file == null) {
			// LOG.debug("Files exists");
			// for (DBObject obj : new
			// GridFS(CorpusClientMongo.getDB()).getFileList()) {
			// LOG.debug("obj: {}", obj );
			// }
			throw new IllegalArgumentException("file does not exits: "
					+ fileName);
		}
		try {
			file.writeTo(output);
		} catch (IOException e) {
			LOG.error("cannot write to stream", e);
			throw new IllegalArgumentException(e);
		}
	}

	private DBCollection findCollection() {
		return CorpusClientMongo.getDB().getCollection(COLLECTION_NAME);
	}
	
	private CorpusEntryMongo newCorpusEntryMongo(CorpusEntry corpusEntry) {
		CorpusEntryMongo entryMongo = new CorpusEntryMongo();
		entryMongo.setId(corpusEntry.id);
		entryMongo.setCreatedEntryOn(corpusEntry.createdEntryOn);
		entryMongo.setUpdatedEntryOn(corpusEntry.updatedEntryOn);
		entryMongo.setFileSize(corpusEntry.fileSize);
		entryMongo.setFileName(corpusEntry.fileName);
		entryMongo.setManualTranscript(corpusEntry.manualTranscript);
		entryMongo.setAutoTranscript(corpusEntry.autoTranscript);
		entryMongo.setSampleRate(corpusEntry.sampleRate);
		entryMongo.setLengthInSec(corpusEntry.lengthInSec);
		entryMongo.setChannels(corpusEntry.channels);
		entryMongo.setSampleSizeInBits(corpusEntry.sampleSizeInBits);
		entryMongo.setGrammar(corpusEntry.grammar);
		entryMongo.setUserName(corpusEntry.userName);
		entryMongo.setUserHost(corpusEntry.userHost);
		return entryMongo;
	}

	private CorpusEntry newCorpusEntry(CorpusEntryMongo mEntry) {
		CorpusEntry corpusEntry = new CorpusEntry();
		corpusEntry.id = mEntry.getId();
		corpusEntry.manualTranscript = mEntry.getManualTranscript();
		corpusEntry.autoTranscript = mEntry.getAutoTranscript();
		corpusEntry.fileName = mEntry.getFileName();
		corpusEntry.sampleRate = mEntry.getSampleRate();
		corpusEntry.lengthInSec = mEntry.getLengthInSec();
		corpusEntry.channels = mEntry.getChannels();
		corpusEntry.sampleSizeInBits = mEntry.getSampleSizeInBits();
		corpusEntry.createdEntryOn = mEntry.getCreatedEntryOn();
		corpusEntry.updatedEntryOn = mEntry.getUpdatedEntryOn();
		corpusEntry.fileSize = mEntry.getFileSize();
		corpusEntry.grammar = mEntry.getGrammar();
		corpusEntry.userName = mEntry.getUserName();
		corpusEntry.userHost =  mEntry.getUserHost();
		return corpusEntry;
	}

	private String createName(String objectId) {
		return objectId.toString() + ".wav";
	}

	private GridFSInputFile createFile(InputStream inputStream, String fileName) {
		GridFS gridFS = new GridFS(CorpusClientMongo.getDB());
		InputStream audioStream = normalizeStream(inputStream);
		GridFSInputFile audioForInput = gridFS
				.createFile(audioStream, fileName);
		audioForInput.setContentType("audio/basic");
		return audioForInput;
	}

	private InputStream normalizeStream(InputStream inputStream) {
		return getAudioNormalizerService().normalize(inputStream);
	}

	@Override
	public CorpusEntry deleteEntryById(String entryId) {
		DBCollection collection = findCollection();
		DBObject dbObj = collection.findOne(new BasicDBObject("_id",
				new ObjectId(entryId)));
		CorpusEntryMongo mEntry = CorpusEntryMongo.fromDbObject(dbObj);
		GridFS gridFS = new GridFS(CorpusClientMongo.getDB());
		gridFS.remove(mEntry.getFileName());
		collection.remove(dbObj);
		return newCorpusEntry(mEntry);
	}

	public AudioNormalizerService getAudioNormalizerService() {
		return audioNormalizerService;
	}

	public void setAudioNormalizerService(
			AudioNormalizerService audioNormalizerService) {
		this.audioNormalizerService = audioNormalizerService;
	}



}
