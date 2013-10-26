package org.spantus.speech.corpus.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class CorpusEntry implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7032034322179684766L;

	@JsonProperty
	public String id;
	@JsonProperty
	public String manualTranscript;
	@JsonProperty
	public Long timeStamp;
	@JsonProperty
	public String fileName;
	@JsonProperty
	public Date createdEntryOn;
	@JsonProperty
	public Date updatedEntryOn;	
	@JsonProperty
	public Long fileSize;
	@JsonProperty
	public Float lengthInSec;
	@JsonProperty
	public Float sampleRate;
	@JsonProperty
	public Integer channels;
	@JsonProperty
	public Integer sampleSizeInBits;
	@JsonProperty
	public String autoTranscript;
	@JsonProperty
	public String grammar;
	@JsonProperty
	public String userName;
	@JsonProperty
	public String userHost;

	public CorpusEntry() {
	}

	public CorpusEntry(String manualTranscript) {
		this.manualTranscript = manualTranscript;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			return (CorpusEntry) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public String toString() {
		return "CorpusEntry [id=" + id + ", manualTranscript=" + manualTranscript
				+ ", autoTranscript=" + autoTranscript + ", fileName=" + fileName
				+ ", created=" + createdEntryOn + ", fileSize=" + fileSize
				+ ", lengthInSec=" + lengthInSec + ", sampleRate=" + sampleRate
				+ ", channels=" + channels + ", grammar="
				+ grammar + "]";
	}
	

}
