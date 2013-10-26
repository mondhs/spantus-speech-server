package org.spantus.speech.recognition.service.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.speech.recognition.dto.PhraseDetail;
import org.spantus.speech.recognition.service.PhraseRepositoryImpl;

public class PhraseRepositoryImplTest {

	PhraseRepositoryImpl phraseRepositoryImpl;
	
	@Before
	public void onSetup() {
		phraseRepositoryImpl = new PhraseRepositoryImpl();
	}
	
	@Test
	public void test() {
		//given
		//when 
		PhraseDetail first = phraseRepositoryImpl.findNextPhrase();
		PhraseDetail second =phraseRepositoryImpl.findNextPhrase();
		PhraseDetail third = phraseRepositoryImpl.findNextPhrase();
		for (int i = 0; i < 100; i++) {
			phraseRepositoryImpl.findNextPhrase().getTranscribe();
		}
		//then
		Assert.assertTrue(!first.getTranscribe().equals(second.getTranscribe()));
		Assert.assertTrue(!second.getTranscribe().equals(third.getTranscribe()));
		Assert.assertTrue(!first.getTranscribe().equals(third.getTranscribe()));
	}

}
