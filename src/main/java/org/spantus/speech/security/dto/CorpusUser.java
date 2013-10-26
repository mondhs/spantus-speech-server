package org.spantus.speech.security.dto;

import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractDate;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.extractString;
import static org.spantus.speech.corpus.service.mongo.CorpusMongoUtil.formatDate;

import java.util.Date;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author as
 *
 */
public class CorpusUser {

	@JsonProperty
	private String userName;
	private String password;
	private String id;
	@JsonProperty
	private String userRole;
	@JsonProperty
	private Date updatedEntryOn;
	@JsonProperty
	private Date createdEntryOn;
	private String host;

	public static CorpusUser fromDbObject(DBObject object) {
        if (object == null) {
            return null;
        }
		CorpusUser mEntry = new CorpusUser();
        mEntry.setId(extractString(object.get("_id")));
        mEntry.setUserName(extractString(object.get("userName")));
        mEntry.setPassword(extractString(object.get("password")));
        mEntry.setUserRole(extractString(object.get("userRole")));
        mEntry.setHost(extractString(object.get("host")));
        mEntry.setCreatedEntryOn(extractDate(object.get("createdEntryOn")));
        mEntry.setUpdatedEntryOn(extractDate(object.get("updatedEntryOn")));
		return mEntry;
	}
	
	public DBObject toDbObject() {
        DBObject object = new BasicDBObject();
        if (id != null) {
            object.put("_id", new ObjectId(id));
        }
        object.put("userName", userName);
        object.put("password", password);
        object.put("userRole", userRole);
        object.put("host", host);
        object.put("createdEntryOn", formatDate(createdEntryOn));
        object.put("updatedEntryOn", formatDate(updatedEntryOn));
        return object;
    }
	
	
	
	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setId(String id) {
		this.id = id;
	}


	public void setUpdatedEntryOn(Date updatedEntryOn) {
		this.updatedEntryOn = updatedEntryOn;
	}

	public void setCreatedEntryOn(Date createdEntryOn) {
		this.createdEntryOn = createdEntryOn;
	}


	public String getId() {
		return id;
	}


	public Date getUpdatedEntryOn() {
		return updatedEntryOn;
	}


	public Date getCreatedEntryOn() {
		return createdEntryOn;
	}

	@Override
	public String toString() {
		return "CorpusUser [userName=" + userName + ", password=" + password
				+ ", id=" + id + ", updatedEntryOn=" + updatedEntryOn
				+ ", createdEntryOn=" + createdEntryOn + "]";
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}


}
