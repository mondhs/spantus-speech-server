package org.spantus.speech.corpus.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.spantus.speech.corpus.dto.CorpusEntry;

public interface ICorpusService {

	CorpusEntry createEntry(CorpusEntry corpusEntry) throws Exception;

	CorpusEntry createEntry(CorpusEntry corpusEntry,
			InputStream uploadedInputStream);

	CorpusEntry updateEntry(String id, CorpusEntry corpusEntry)
			throws Exception;

	CorpusEntry findEntryById(String id) throws Exception;

	List<CorpusEntry> findTopEntries() throws Exception;

	void retrieveAudioStreamByName(String fileName, OutputStream output);

	CorpusEntry deleteEntryById(String entryId);

	List<CorpusEntry> findEntriesByText(String searchText);

	Integer countEntries();

}