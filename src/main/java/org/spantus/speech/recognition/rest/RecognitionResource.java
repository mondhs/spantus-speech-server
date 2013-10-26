package org.spantus.speech.recognition.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.corpus.dto.CorpusEntry;
import org.spantus.speech.corpus.service.ICorpusService;
import org.spantus.speech.recognition.dto.PhraseDetail;
import org.spantus.speech.recognition.dto.RecognitionSummary;
import org.spantus.speech.recognition.service.PhraseRepositoryImpl;
import org.spantus.speech.recognition.service.SpantusRecognitionServiceImpl;
import org.spantus.speech.recognition.service.SpantusRecognitionServiceImpl.RecognizerConfigGrammar;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/recognize")
public class RecognitionResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(RecognitionResource.class);

	private static final String DEFAULT_GRAMMAR = SpantusRecognitionServiceImpl.RecognizerConfigGrammar.lt_robotas.name();
	
	private SpantusRecognitionServiceImpl recognitionService;
	private PhraseRepositoryImpl phraseRepository ;
	private ICorpusService corpusService;

	
    @Inject
    RecognitionResource(ICorpusService corpusService) {
    	this.recognitionService = new SpantusRecognitionServiceImpl();
        this.corpusService = corpusService;
        this.phraseRepository = new PhraseRepositoryImpl();
    }
    
    /////////////////////////////////////////////////////////
    @GET
	@Path("/phrase")
	public PhraseDetail findRandomPhrase() throws Exception {
		return phraseRepository.findNextPhrase();
    }
    
    /////////////////////////////////////////////////////////    
    
	@GET
	@Path("/grammas")
	public List<String> findAvailableGrammas() throws Exception {
		SpantusRecognitionServiceImpl.RecognizerConfigGrammar.values();
		List<RecognizerConfigGrammar> listEnums = Arrays.asList(SpantusRecognitionServiceImpl.RecognizerConfigGrammar.values());
		Iterable<String> listStrings = Iterables.transform(listEnums, new Function<RecognizerConfigGrammar, String >() {
			@Override
			public String apply(@Nullable RecognizerConfigGrammar input) {
				return input.name();
			}
		});
		return Lists.newArrayList(listStrings);
	}

	@GET
	@Path("/entry/{entryId}/{grammar}")
	public RecognitionSummary recognizeAudioStream(
			@PathParam("entryId") String entryId, @PathParam("grammar") String grammar) throws Exception {
		LOG.debug("[recognizeAudioStream] fileName or entryId:{}", entryId);
    	final String fileName;
    	CorpusEntry entry = corpusService.findEntryById(entryId);
    	if(entry == null){
    		fileName = entryId;
    	}else{
    		fileName = entry.fileName;
    	}
    	if(grammar == null){
    		grammar = DEFAULT_GRAMMAR;
    	}
    	LOG.debug("[recognizeAudioStream] fileName: {}", fileName);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		corpusService.retrieveAudioStreamByName(fileName, output);
		return recognitionService.recognize(output, fileName,grammar);
	}

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/public/stream/{grammar}/{transcribe}")
	public RecognitionSummary recognizeStreamPublic(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("grammar") String grammar,
			@PathParam("transcribe") String transcribe)
			throws Exception {
		return recognizeStream(uploadedInputStream, fileDetail, grammar, transcribe);
	}
	
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/stream/{grammar}/{transcribe}")
	public RecognitionSummary recognizeStream(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("grammar") String grammar,
			@PathParam("transcribe") String transcribe)
			throws Exception {
		LOG.debug("[recognizeStream] grammar: {}", grammar);
		CorpusEntry corpusEntry = new CorpusEntry();
		corpusEntry.grammar = grammar;
		LOG.debug("[recognizeStream] fileDetail: {}", fileDetail);
		corpusEntry = corpusService.createEntry(corpusEntry,
				uploadedInputStream);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		corpusService.retrieveAudioStreamByName(corpusEntry.fileName, output);
		RecognitionSummary result = recognitionService.recognize(output,
				corpusEntry.fileName,grammar);
		corpusEntry.autoTranscript = result.getResult();
		corpusEntry.manualTranscript = transcribe;
		corpusService.updateEntry(corpusEntry.id, corpusEntry);
		return result;
	}
}
