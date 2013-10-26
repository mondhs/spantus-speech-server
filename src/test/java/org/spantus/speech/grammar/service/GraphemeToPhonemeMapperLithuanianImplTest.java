package org.spantus.speech.grammar.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class GraphemeToPhonemeMapperLithuanianImplTest {
	private static final Logger LOG = LoggerFactory.getLogger(GraphemeToPhonemeMapperLithuanianImplTest.class);

	private GraphemeToPhonemeMapperLithuanianImpl mapperLithuanianImpl; 
	
	@Before
	public void setUp() throws Exception {
		mapperLithuanianImpl = new GraphemeToPhonemeMapperLithuanianImpl();
	}

	@Test
	public void testTransform() {
		//given
		Iterable<String> words = Splitter.on(",").trimResults().omitEmptyStrings().split("klausyti,žaisti,knygą," +
				"panoramą,kauno,žinias");
		Map<String,List<String>> result = new LinkedHashMap<String, List<String>>();
		Map<String,String> expected = new LinkedHashMap<String, String>();
		expected.put("KLAUSYTI", "K L AU S IY T IH");
		expected.put("ŽAISTI", "ZH AI S T IH");
		expected.put("KNYGĄ", "K N IY G AA");
		//when
		for (String word : words) {
			word = word.toUpperCase();
			List<String> transformed = mapperLithuanianImpl.transform(word);
			result.put(word, transformed);
		}
		//then
		StringBuilder sb = new StringBuilder();
		for (Entry<String, List<String>> entry : result.entrySet()) {
			String actualPhonemes = Joiner.on(" ").join(entry.getValue());
			String expectedPhonemes = expected.get(entry.getKey());
			if(expectedPhonemes!=null){
				Assert.assertEquals("Phonemes", expectedPhonemes, actualPhonemes);
			}
			sb.append(entry.getKey()).append("\t").append(actualPhonemes).append("\n");
		}
		LOG.debug("result:\n {}", sb);
	}

}
