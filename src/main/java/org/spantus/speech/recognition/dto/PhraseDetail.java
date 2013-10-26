package org.spantus.speech.recognition.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class PhraseDetail {

	@JsonProperty
	private String transcribe;
	@JsonProperty
	private String grammar;

	public PhraseDetail() {
	}

	public PhraseDetail(String grammar,String phrase) {
		this.transcribe = phrase;
		this.grammar = grammar;
	}

	public String getTranscribe() {
		return transcribe;
	}

	public void setTranscribe(String phrase) {
		this.transcribe = phrase;
	}

	public String getGrammar() {
		return grammar;
	}

	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}
}
