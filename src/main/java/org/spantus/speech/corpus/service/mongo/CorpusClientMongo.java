package org.spantus.speech.corpus.service.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class CorpusClientMongo {
	private static MongoClient mongoClient;
	private static MongoClientURI mongoClientUri;
	
	public static void setMongoClient(MongoClient mongoClient) {
		CorpusClientMongo.mongoClient = mongoClient;
	}
	
	public static void setMongoClientUri(MongoClientURI mongoClientUri) {
		CorpusClientMongo.mongoClientUri = mongoClientUri;
	}	

	public static DB getDB() {
		return mongoClient.getDB(mongoClientUri.getDatabase());
	}





}
