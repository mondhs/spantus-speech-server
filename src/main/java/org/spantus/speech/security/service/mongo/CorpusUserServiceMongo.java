package org.spantus.speech.security.service.mongo;

import java.util.Calendar;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.corpus.service.mongo.CorpusClientMongo;
import org.spantus.speech.security.dto.CorpusUser;
import org.spantus.speech.security.service.CorpusUserService;

import com.google.common.base.Strings;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class CorpusUserServiceMongo implements CorpusUserService {
	private static final Logger LOG = LoggerFactory.getLogger(CorpusUserServiceMongo.class);
	public static final String COLLECTION_NAME = "SpantusCorpusUser";

	@Override
	public CorpusUser findCorpusUserByName(String userName) {
		LOG.debug("[findCorpusUserByName] +++");
		CorpusUser corpusUser = null;
		BasicDBObject whereQuery = null;
		if(!Strings.isNullOrEmpty(userName)){
			
			DBObject clauseUserName = new BasicDBObject("userName", userName);
			BasicDBList or = new BasicDBList();
			or.add(clauseUserName);
			whereQuery = new BasicDBObject("$or", or);
		}else{
			whereQuery = new BasicDBObject(); 
		}
		DBCursor collection = findCollection().find(whereQuery);
		collection.sort(new BasicDBObject("_id", -1));
		collection.limit(1);
		
		for (DBObject object : collection) {
			CorpusUser mEntry = CorpusUser.fromDbObject(object);
			corpusUser = mEntry;
			corpusUser.setUpdatedEntryOn(Calendar.getInstance().getTime());
			corpusUser.setHost(findCurrent().getHost());
			findCollection().save(corpusUser.toDbObject());
			break;
		}
		
		LOG.debug("[findCorpusUserByName] --- found {}", corpusUser);
		return corpusUser;
	}
	
	public CorpusUser findCurrent(){
		Subject subject = SecurityUtils.getSubject();
		CorpusUser corpusUser = new CorpusUser();
		if(subject != null){
			corpusUser.setUserName(""+subject.getPrincipal());
			corpusUser.setHost(subject.getSession().getHost());
		}
		return corpusUser;
		
	}
		

	@Override
	public String crteatCorpusUser(String userName, String password) {
		CorpusUser user = new CorpusUser();
		user.setUserName(userName);
		user.setPassword(password);
		DBObject object = user.toDbObject();
		WriteResult result = findCollection().save(object);
		return result.getField("_id").toString();
	}

	private DBCollection findCollection() {
		return CorpusClientMongo.getDB().getCollection(COLLECTION_NAME);
	}
	
}
