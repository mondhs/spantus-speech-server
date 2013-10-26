package org.spantus.speech.corpus.service.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.corpus.dto.CorpusEntry;
import org.spantus.speech.corpus.service.ICorpusService;

import com.google.common.io.Files;


public class CorpusServiceMem implements ICorpusService {
    Map<String, CorpusEntry> corpus = new TreeMap<String, CorpusEntry>();
    private static final Logger LOG = LoggerFactory.getLogger(CorpusServiceMem.class);

    CorpusServiceMem() {
    	createEntry(new CorpusEntry("dot"));
    	createEntry(new CorpusEntry("cat"));

    }



    @Override
    public CorpusEntry createEntry(CorpusEntry corpusEntry) {
        if (corpus.containsKey(corpusEntry.manualTranscript)) {
            throw new IllegalArgumentException("Word already exists: " + corpusEntry.manualTranscript);
        }

        CorpusEntry newEntry = null;
		try {
			newEntry = (CorpusEntry) corpusEntry.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Cannot clone", e);
		}
        corpus.put(newEntry.manualTranscript, newEntry);
        return newEntry;
    }
    @Override
    public CorpusEntry updateEntry(String id,CorpusEntry corpusEntry) {
    	CorpusEntry storedEntry = findEntryById(id);
    	storedEntry.id = corpusEntry.id;
    	storedEntry.manualTranscript = corpusEntry.manualTranscript;
    	storedEntry.fileName = corpusEntry.fileName;
		return corpusEntry;
    	
    }

    @Override
    public CorpusEntry findEntryById(String id) {
        return corpus.get(id);
    }

    @Override
    public List<CorpusEntry> findTopEntries() {
        return new ArrayList<CorpusEntry>(corpus.values());
    }



	@Override
	public CorpusEntry createEntry(CorpusEntry corpusEntry,
			InputStream uploadedInputStream) {
		File uploadedFile = new File(Files.createTempDir(), corpusEntry.fileName);
		corpusEntry.fileName = uploadedFile.getAbsolutePath();
		 
		// save it
		writeToFile(uploadedInputStream, uploadedFile);

		return createEntry(corpusEntry);
	}
	/**
	 * save uploaded file to new location
	 * @param uploadedInputStream
	 * @param uploadedFile
	 */
	private void writeToFile(InputStream uploadedInputStream,
		File uploadedFile) {
 
		try {
			OutputStream out = new FileOutputStream(uploadedFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			LOG.error("Something bad happended", e);
		}
	}



	@Override
	public void retrieveAudioStreamByName(String fileName, OutputStream output) {
		File file = new File(fileName);
		if(!fileName.endsWith("wav")){
			throw new IllegalArgumentException("What you are looking for? there is no access:" + fileName);
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = fis.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}
			output.flush();
			output.close();
			fis.close();
		} catch (FileNotFoundException e) {
			LOG.error("There is no file: " + fileName,e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LOG.error("I/O exception: " + fileName,e);
			throw new IllegalArgumentException(e);
		}
	}



	@Override
	public CorpusEntry deleteEntryById(String entryId) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<CorpusEntry> findEntriesByText(String searchText){
		return findTopEntries();
	}
	
	@Override
	public Integer countEntries() {
		return corpus.size();
	}




}
