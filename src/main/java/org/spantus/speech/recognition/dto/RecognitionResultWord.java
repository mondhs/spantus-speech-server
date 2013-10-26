package org.spantus.speech.recognition.dto;

import java.io.Serializable;

public class RecognitionResultWord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5234466351610035043L;
	private double confidence;
	private double score;
	private int startFrame;
	private int endFrame;
	private String pronunciation;

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}

	public int getStartFrame() {
		return startFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setPronunciation(String pronunciation) {
		this.pronunciation = pronunciation;
	}

	public String getPronunciation() {
		return pronunciation;
	}

}
