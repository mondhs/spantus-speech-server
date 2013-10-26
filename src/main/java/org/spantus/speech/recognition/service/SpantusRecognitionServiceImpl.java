package org.spantus.speech.recognition.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.recognition.dto.RecognitionResultItem;
import org.spantus.speech.recognition.dto.RecognitionResultWord;
import org.spantus.speech.recognition.dto.RecognitionSummary;

import com.google.common.base.Strings;

import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SpantusRecognitionServiceImpl {
	private static final Logger LOG = LoggerFactory
			.getLogger(SpantusRecognitionServiceImpl.class);

	public enum RecognizerConfigGrammar{
		lt_robotas, lt_kalkuliatorius, lt_numeriai, en_digits
	};
	
	public RecognitionSummary recognize(ByteArrayOutputStream output,
			String streamName, String grammar) {
		LOG.debug("[recognize] +++ {}", streamName);
		RecognizerConfigGrammar grammarDefined = RecognizerConfigGrammar.lt_robotas;
		try{
			grammarDefined = RecognizerConfigGrammar.valueOf(grammar);
		}catch(IllegalArgumentException iae){
			LOG.error("value not found", iae);
		}
		Recognizer recognizer = setupRecognizer(output, streamName, grammarDefined.toString());

		// Loop until last utterance in the audio file has been decoded, in
		// which case the recognizer will return null.
		Result result;
		StringBuilder sb = new StringBuilder();
		String separator = "";
		RecognitionSummary recognitionSummary = new RecognitionSummary();
		recognitionSummary.setResultItems(new LinkedList<RecognitionResultItem>());
		while ((result = recognizer.recognize()) != null) {
			RecognitionResultItem resultItem = parseRecognitionResult(result, recognitionSummary);
			LOG.debug("[recognize]  resultItem.getBestResultNoFiller() {}", resultItem.getBestResultNoFiller());
			if(!Strings.isNullOrEmpty(resultItem.getBestResultNoFiller())){
				LOG.debug("[recognize]  add {}", resultItem.getBestResultNoFiller());
				sb.append(separator).append(resultItem.getBestResultNoFiller());
				recognitionSummary.getResultItems().add(resultItem);
			}
			separator = ",";
		}
		recognitionSummary.setResult(sb.toString());
		if(Strings.isNullOrEmpty(recognitionSummary.getResult())){
			recognitionSummary.setResult("-");
		}

		LOG.debug("[recognize] --- {}", streamName);
		return recognitionSummary;
	}

	private Recognizer setupRecognizer(ByteArrayOutputStream output, String streamName, String configGrammar) {
		// get a recognizer configured for the Transcriber task
//		TranscriberConfiguration config = createConfig();
		
//		Recognizer recognizer = config.getRecognizer();

		// allocate the resource necessary for the recognizer
//		recognizer.allocate();

		// configure the audio input for the recognizer
//		AudioFileDataSource audioSource = config.getAudioFileDataSource();
		
		URL configURL = this.getClass()
				.getResource("config-"+configGrammar+".xml");
		ConfigurationManager cm = new ConfigurationManager(configURL);
		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		AudioFileDataSource audioSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");
		
		audioSource.setInputStream(createInputStream(output), streamName);

		return recognizer;
	}

	private RecognitionResultItem parseRecognitionResult(Result result,
			RecognitionSummary recognitionSummary) {
		RecognitionResultItem item = new RecognitionResultItem();
		Token bestToken = result.getBestActiveToken();
		LOG.debug("[parseRecognitionResult] recognize: {}", result);
		LOG.debug("[parseRecognitionResult] getBestFinalToken: {}", result.getBestFinalToken());
		LOG.debug("[parseRecognitionResult] getBestActiveToken: {}", result.getBestActiveToken());
		LOG.debug("[parseRecognitionResult] getBestToken: {}", result.getBestToken());

		LOG.debug("[parseRecognitionResult] getBestToken exploded: getAcousticScore()={} + getLanguageScore()={} + getInsertionScore()={}", 
				 bestToken.getAcousticScore(), bestToken.getLanguageScore(), bestToken.getInsertionScore());
		LOG.debug("[parseRecognitionResult] getBestFinalResultNoFiller: {}", result.getBestFinalResultNoFiller());
		LOG.debug("[parseRecognitionResult] getBestResultNoFiller: {}", result.getBestResultNoFiller());
		LOG.debug("[parseRecognitionResult] getBestPronunciationResult: {}", result.getBestPronunciationResult());
		LOG.debug("[parseRecognitionResult] getReferenceText: {}", result.getReferenceText());
		LOG.debug("[parseRecognitionResult] getWords: {}", result.getWords());
		item.setBestResultNoFiller(result.getBestResultNoFiller());
		item.setBestFinalResultNoFiller(result.getBestFinalResultNoFiller());
		item.setBestPronunciationResult(result.getBestPronunciationResult());
		item.setReferenceText(result.getReferenceText());
		if(bestToken.getPredecessor()!= null){
			item.setEntryScore(bestToken.getPredecessor().getScore());	
		}
		item.setAcousticScore(bestToken.getAcousticScore());
		item.setLanguageScore(bestToken.getLanguageScore());
		item.setInsertionScore(bestToken.getInsertionScore());
		item.setWords(new LinkedList<RecognitionResultWord>());
		for (WordResult word : result.getWords()) {
			RecognitionResultWord resultWord = new  RecognitionResultWord();
			resultWord.setConfidence(word.getConfidence());
			resultWord.setScore(word.getScore());
			resultWord.setStartFrame(word.getStartFrame());
			resultWord.setEndFrame(word.getEndFrame());
			resultWord.setPronunciation(word.getPronunciation().toString());
			item.getRecognitionWords().add(resultWord);
		}
		
		return item;
	}

	@SuppressWarnings("unused")
	private TranscriberConfiguration createConfig() {
		LOG.debug("[createConfig]");
		try {
			TranscriberConfiguration config = new TranscriberConfiguration();
			return config;
		} catch (MalformedURLException e) {
			LOG.error("URL bad format", e);
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			LOG.error("Class not found", e);
			throw new IllegalArgumentException(e);
		} catch (URISyntaxException e) {
			LOG.error("URI bad format", e);
			throw new IllegalArgumentException(e);
		}
	}

	private AudioInputStream createInputStream(ByteArrayOutputStream output) {
		LOG.debug("[createInputStream]");
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new ByteArrayInputStream(output
							.toByteArray()));
			LOG.debug("[createInputStream] audio format {}",
					audioInputStream.getFormat());
			return audioInputStream;
		} catch (UnsupportedAudioFileException e) {
			LOG.error("format not supported", e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LOG.error("I/O error", e);
			throw new IllegalArgumentException(e);
		}

	}


}
