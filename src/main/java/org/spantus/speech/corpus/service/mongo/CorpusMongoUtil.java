package org.spantus.speech.corpus.service.mongo;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CorpusMongoUtil {
	transient private static final Logger LOG = LoggerFactory
			.getLogger(CorpusEntryMongo.class);
	transient private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mmZ");

	private CorpusMongoUtil() {
	}

	public static Date extractDate(Object object) {
		try {
			return object == null ? null : DATE_FORMAT.parse(object.toString());
		} catch (ParseException e) {
			LOG.error("Date parse error " + object, e);
		}
		return null;
	}

	public static Long extractLong(Object object) {
		return object == null ? null : Long.valueOf(object.toString());
	}

	public static Integer extractInt(Object object) {
		return object == null ? null : Integer.valueOf(object.toString());
	}

	public static Float extractFloat(Object object) {
		return object == null ? null : Float.valueOf(object.toString());
	}

	public static String extractString(Object object) {
		return object == null ? null : object.toString();
	}
	public static String formatDate(Date date) {
		return date!=null?DATE_FORMAT.format(date):null;
	}
	
}
