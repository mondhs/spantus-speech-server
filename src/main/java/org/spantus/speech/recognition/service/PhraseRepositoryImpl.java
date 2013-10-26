package org.spantus.speech.recognition.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.spantus.speech.recognition.dto.PhraseDetail;

import com.google.common.collect.Lists;

public class PhraseRepositoryImpl {

	private static final List<PhraseDetail> KNOWN_PHRASES = Lists.newArrayList();
	
	static{
		String lt_numeriai = SpantusRecognitionServiceImpl.RecognizerConfigGrammar.lt_numeriai.toString();
		String en_digits = SpantusRecognitionServiceImpl.RecognizerConfigGrammar.en_digits.toString();
		String lt_robotas = SpantusRecognitionServiceImpl.RecognizerConfigGrammar.lt_robotas.toString();
		KNOWN_PHRASES.add(new PhraseDetail(lt_numeriai, "vienas du trys"));
		KNOWN_PHRASES.add(new PhraseDetail(lt_numeriai, "penki devyni šeši"));
		KNOWN_PHRASES.add(new PhraseDetail(en_digits, "one two three"));
		KNOWN_PHRASES.add(new PhraseDetail(en_digits, "four five six"));
		KNOWN_PHRASES.add(new PhraseDetail(lt_robotas, "suk kairėn"));
		KNOWN_PHRASES.add(new PhraseDetail(lt_robotas, "suk dešinėn"));
		KNOWN_PHRASES.add(new PhraseDetail(lt_robotas, "varyk pirmyn"));
		KNOWN_PHRASES.add(new PhraseDetail(lt_robotas, "varyk atgal"));
	}
	
	private LinkedList<PhraseDetail> phrases = Lists.newLinkedList();
	
	
	public PhraseDetail findNextPhrase(){
		if(phrases.size() == 0){
			phrases.addAll(KNOWN_PHRASES);
			Collections.shuffle(phrases);
		}
		 return phrases.removeFirst();
	}
}
