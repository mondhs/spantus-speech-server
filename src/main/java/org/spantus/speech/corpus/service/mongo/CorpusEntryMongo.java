package org.spantus.speech.corpus.service.mongo;

import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractDate;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractFloat;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractInt;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractLong;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractString;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.formatDate;

import java.util.Date;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class CorpusEntryMongo {
	@SuppressWarnings("unused")
	transient private static final Logger LOG = LoggerFactory.getLogger(CorpusEntryMongo.class); 
	
    private String id;
    private Long timeStamp;
    private String manualTranscript;
    private String fileName;
    private Date createdEntryOn;
    private Date updatedEntryOn;
    private Long fileSize;
    private Float lengthInSec;
    private Float sampleRate;
    private Integer channels;
    private Integer sampleSizeInBits;
    private String autoTranscript;
    private String grammar;

	private String userName;

	private String userHost;

    public CorpusEntryMongo() {
	}

    public static CorpusEntryMongo fromDbObject(DBObject object) {
        if (object == null) {
            return null;
        }
        CorpusEntryMongo mEntry = new CorpusEntryMongo();
        mEntry.setId(extractString(object.get("_id")));
        mEntry.setManualTranscript(extractString(object.get("manualTranscript")));
        mEntry.setFileName(extractString(object.get("fileName")));
        mEntry.setSampleRate(extractFloat(object.get("sampleRate")));
        mEntry.setLengthInSec(extractFloat(object.get("lengthInSec")));
        mEntry.setChannels(extractInt(object.get("channels")));
        mEntry.setSampleSizeInBits(extractInt(object.get("sampleSizeInBits")));
        mEntry.setFileSize(extractLong(object.get("fileSize")));
        mEntry.setCreatedEntryOn(extractDate(object.get("createdEntryOn")));
        mEntry.setUpdatedEntryOn(extractDate(object.get("updatedEntryOn")));
        mEntry.setAutoTranscript(extractString(object.get("autoTranscript")));
        mEntry.setGrammar(extractString(object.get("grammar")));
        mEntry.setUserHost(extractString(object.get("userHost")));
        mEntry.setUserName(extractString(object.get("userName")));
        return mEntry;
    }



	public DBObject toDbObject() {
        DBObject object = new BasicDBObject();
        if (id != null) {
            object.put("_id", new ObjectId(id));
        }
        object.put("manualTranscript", manualTranscript);
        object.put("fileName", fileName);
        object.put("sampleRate", sampleRate);
        object.put("lengthInSec", lengthInSec);
        object.put("channels", channels);
        object.put("sampleSizeInBits", sampleSizeInBits);
        object.put("createdEntryOn", formatDate(createdEntryOn));
        object.put("updatedEntryOn", formatDate(updatedEntryOn));
        object.put("fileSize", fileSize);
        object.put("autoTranscript", autoTranscript);
        object.put("grammar", grammar);
        object.put("userName", userName);
        object.put("userHost", userHost);
        return object;
    }
	

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Float getLengthInSec() {
		return lengthInSec;
	}

	public void setLengthInSec(Float lengthInSec) {
		this.lengthInSec = lengthInSec;
	}

	public Float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public Integer getChannels() {
		return channels;
	}

	public void setChannels(Integer channels) {
		this.channels = channels;
	}

	public Integer getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public void setSampleSizeInBits(Integer sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}


	public String getGrammar() {
		return grammar;
	}

	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}

	public Date getCreatedEntryOn() {
		return createdEntryOn;
	}

	public void setCreatedEntryOn(Date createdEntry) {
		this.createdEntryOn = createdEntry;
	}

	public Date getUpdatedEntryOn() {
		return updatedEntryOn;
	}

	public void setUpdatedEntryOn(Date updatedEntry) {
		this.updatedEntryOn = updatedEntry;
	}


	public String getAutoTranscript() {
		return autoTranscript;
	}

	public void setAutoTranscript(String autoTranscript) {
		this.autoTranscript = autoTranscript;
	}

	public String getManualTranscript() {
		return manualTranscript;
	}

	public void setManualTranscript(String manualTranscript) {
		this.manualTranscript = manualTranscript;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserHost(String userHost) {
		this.userHost = userHost;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserHost() {
		return userHost;
	}
	

}
