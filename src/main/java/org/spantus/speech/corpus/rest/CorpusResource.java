package org.spantus.speech.corpus.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.corpus.dto.CorpusEntry;
import org.spantus.speech.corpus.service.ICorpusService;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/corpus")
public class CorpusResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(CorpusResource.class);
	
    private ICorpusService corpusService;
    

    @Inject
    CorpusResource(ICorpusService corpusService) {
        this.corpusService = corpusService;
    }

    @GET
    @Path("/entry/{entryId}")
    public CorpusEntry findEntryById(@PathParam("entryId") String entryId) throws Exception {
    	LOG.error("[findEntryById] entry: {}", entryId);
        return corpusService.findEntryById(entryId);
    }
    
    @DELETE
    @Path("/entry/{entryId}")
    public CorpusEntry deleteEntryById(@PathParam("entryId") String entryId) throws Exception {
    	LOG.error("[deleteEntryById] entry: {}", entryId);
        return corpusService.deleteEntryById(entryId);
    }

    @GET
    @Path("/")
    public List<CorpusEntry> findTopEntries() throws Exception {
    	LOG.error("[findTopEntries]");
        return corpusService.findTopEntries();
    }
    
    @GET
    @Path("/count")
    public Integer countEntries() throws Exception {
    	LOG.error("[findTopEntries]");
        return corpusService.countEntries();
    }
    
    @GET
    @Path("/entries")
    public List<CorpusEntry> findTopEntries(@QueryParam("searchText") String searchText) throws Exception {
    	LOG.error("[findTopEntries] searchText: {}", searchText);
        return corpusService.findEntriesByText(searchText);
    }


    
    @GET
    @Path("/entry/{entryId}/stream")
    @Produces({"audio/wav"})
    public StreamingOutput findAudioStream(@PathParam("entryId") String entryId) throws Exception{
    	final String fileName;
    	CorpusEntry entry = corpusService.findEntryById(entryId);
    	if(entry == null){
    		fileName = entryId;
    	}else{
    		fileName = entry.fileName;
    	}
    	LOG.debug("[findAudioStream] fileName:{}", fileName);
    	return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                	corpusService.retrieveAudioStreamByName(fileName,output);
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };
    	
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/entry/{entryId}")
    public CorpusEntry updateEntity(@PathParam("entryId") String entryId, CorpusEntry corpusEntry) throws Exception  {
    	LOG.debug("[updateEntity] {}", corpusEntry);
    	LOG.debug("[updateEntity] User{}", SecurityUtils.getSubject().getPrincipal());
    	CorpusEntry foundEntry = findEntryById(entryId);
    	if(corpusEntry.manualTranscript != null){
    		foundEntry.manualTranscript = corpusEntry.manualTranscript;
    		foundEntry.autoTranscript = corpusEntry.autoTranscript;
    		foundEntry.grammar = corpusEntry.grammar;
    		LOG.debug("[updateEntity] manualTranscript: {}, autoTranscript {}", foundEntry.manualTranscript, foundEntry.autoTranscript);
    	}
        return corpusService.updateEntry(entryId, foundEntry);
    }
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/stream")
	public CorpusEntry uploadFile (
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception{
    	CorpusEntry corpusEntry = new CorpusEntry();
		LOG.debug("[uploadFile] fileDetail: {}", fileDetail);
		return corpusService.createEntry(corpusEntry, uploadedInputStream);
	}
    

}
