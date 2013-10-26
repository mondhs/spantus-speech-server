package org.spantus.speech.recognition.dto;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RecognitionResultItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8828009304483272309L;
	@JsonProperty
	private String bestResultNoFiller;
	@JsonProperty
	private String bestFinalResultNoFiller;
	@JsonProperty
	private String bestPronunciationResult;
	@JsonProperty
	private String referenceText;
	@JsonProperty
	private List<RecognitionResultWord> recognitionWords;
	private float entryScore;
	private float acousticScore;
	private float languageScore;
	private float insertionScore;

	public void setBestResultNoFiller(String bestResultNoFiller) {
		this.bestResultNoFiller = bestResultNoFiller;
	}

	public String getBestResultNoFiller() {
		return bestResultNoFiller;
	}

	public void setBestFinalResultNoFiller(String bestFinalResultNoFiller) {
		this.bestFinalResultNoFiller = bestFinalResultNoFiller;
	}

	public String getBestFinalResultNoFiller() {
		return bestFinalResultNoFiller;
	}

	public void setBestPronunciationResult(String bestPronunciationResult) {
		this.bestPronunciationResult = bestPronunciationResult;
	}

	public String getBestPronunciationResult() {
		return bestPronunciationResult;
	}

	public void setReferenceText(String referenceText) {
		this.referenceText = referenceText;
	}

	public String getReferenceText() {
		return referenceText;
	}

	public void setWords(List<RecognitionResultWord> recognitionWords) {
		this.recognitionWords = recognitionWords;
		
	}

	public List<RecognitionResultWord> getRecognitionWords() {
		return recognitionWords;
	}

	public void setRecognitionWords(List<RecognitionResultWord> recognitionWords) {
		this.recognitionWords = recognitionWords;
	}

	public void setEntryScore(float entryScore) {
		this.entryScore = entryScore;
	}

	public float getEntryScore() {
		return entryScore;
	}

	public void setAcousticScore(float acousticScore) {
		this.acousticScore = acousticScore;
	}

	public float getAcousticScore() {
		return acousticScore;
	}

	public void setLanguageScore(float languageScore) {
		this.languageScore = languageScore;
	}

	public float getLanguageScore() {
		return languageScore;
	}

	public void setInsertionScore(float insertionScore) {
		this.insertionScore = insertionScore;
	}

	public float getInsertionScore() {
		return insertionScore;
	}

}
