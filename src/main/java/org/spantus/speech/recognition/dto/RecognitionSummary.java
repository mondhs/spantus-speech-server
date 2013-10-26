package org.spantus.speech.recognition.dto;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RecognitionSummary implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8391113429465176314L;
	@JsonProperty
	private String result;
	@JsonProperty
	private List<RecognitionResultItem> resultItems;

	public void setResult(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResultItems(List<RecognitionResultItem> resultItems) {
		this.resultItems = resultItems; 
	}

	public List<RecognitionResultItem> getResultItems() {
		return resultItems;
	}

}
