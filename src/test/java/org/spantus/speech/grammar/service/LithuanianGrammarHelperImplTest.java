package org.spantus.speech.grammar.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.speech.grammar.dto.GenusEnum;
import org.spantus.speech.grammar.service.LithuanianGrammarHelperImpl;

public class LithuanianGrammarHelperImplTest {

	private LithuanianGrammarHelperImpl helper;

	@Before
	public void setUp() throws Exception {
		helper = new LithuanianGrammarHelperImpl();
	}

	@Test
	public void testMakeNounFromDatToNomAndBack() {
		//given
		Map<String, String> nounDativeMap = new LinkedHashMap<String, String>();
		nounDativeMap.put("LAIPSNIS", "LAIPSNIUI");
		nounDativeMap.put("STALAS", "STALUI");
		nounDativeMap.put("VALANDA", "VALANDAI");
		nounDativeMap.put("MINUTĖ", "MINUTEI");
		nounDativeMap.put("TINGINĖ", "TINGINEI");
		Map<String, String> datResult = new LinkedHashMap<String, String>();
		//then
		for (Entry<String, String> nounNomDat : nounDativeMap.entrySet()) {
			String aNounNominative = nounNomDat.getKey();
			datResult.put(aNounNominative,helper.makeNounToDatSng(aNounNominative));
		}
		//when
		for (Entry<String, String> nounDative : nounDativeMap.entrySet()) {
			String aNoun = nounDative.getKey();
			Assert.assertEquals(nounDative.getValue(), datResult.get(aNoun));
		}
	}
	@Test
	public void testStripDatEnding() {
		//given
		Map<String, String> nounDativeMap = new LinkedHashMap<String, String>();
		nounDativeMap.put("LAIPSNIUI","LAIPSN");
		nounDativeMap.put("STALUI","STAL");
		nounDativeMap.put("VALANDAI", "VALAND");
		nounDativeMap.put("MINUTEI", "MINU");
		Map<String, String> nomResult = new LinkedHashMap<String, String>();
		//then
		for (Entry<String, String> nounNomDat : nounDativeMap.entrySet()) {
			String aNounDative = nounNomDat.getKey();
			nomResult.put(aNounDative, helper.stripDatEnding(aNounDative));
		}
		//when
		for (Entry<String, String> nounDative : nounDativeMap.entrySet()) {
			String aNoun = nounDative.getKey();
			Assert.assertEquals(nounDative.getValue(), nomResult.get(aNoun));
		}
	}
	
	
	
	@Test
	public void testmMtchNounToNumerales() {
		//given
		Map<String, List<String>> expectedMap = new HashMap<String, List<String>>();
		expectedMap.put("VALANDA", Arrays.asList(
				/*10*/ "VALANDA", "VALANDOS","VALANDOS",  "VALANDOS","VALANDOS", "VALANDOS","VALANDOS", "VALANDOS","VALANDOS", "VALANDŲ",
				/*20*/ "VALANDŲ", "VALANDŲ", "VALANDŲ", "VALANDŲ","VALANDŲ", "VALANDŲ", "VALANDŲ", "VALANDŲ", "VALANDŲ", "VALANDŲ",
				/*30*/ "VALANDA", "VALANDOS","VALANDOS", "VALANDOS","VALANDOS", "VALANDOS", "VALANDOS", "VALANDOS","VALANDOS", "VALANDŲ",
				"VALANDA", "VALANDOS"));
		expectedMap.put("MINUTĖ",  Arrays.asList(
				/*10*/"MINUTĖ", "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUTĖS" , "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUČIŲ",
				/*20*/"MINUČIŲ", "MINUČIŲ", "MINUČIŲ", "MINUČIŲ", "MINUČIŲ" , "MINUČIŲ", "MINUČIŲ", "MINUČIŲ", "MINUČIŲ", "MINUČIŲ",
				/*30*/"MINUTĖ", "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUTĖS" , "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUTĖS", "MINUČIŲ"
				));
		Map<String, List<String>> actualResult = new HashMap<String, List<String>>();
		//then
		for (Entry<String, List<String>> nounDative : expectedMap.entrySet()) {
			String aNoun = nounDative.getKey();
			List<String> matchedCollection = new ArrayList<String>(nounDative.getValue().size());
			actualResult.put(aNoun, matchedCollection);
			for (int i = 1; i <= nounDative.getValue().size(); i++) {
				matchedCollection.add(helper.matchNounToNumerales(i, aNoun));
			} 
			
		}
		//when
		for (Entry<String, List<String>> expectedEntry : expectedMap.entrySet()) {
			String aNoun = expectedEntry.getKey();
			List<String> expectedList = expectedEntry.getValue();
			List<String> actualList = actualResult.get(aNoun);
//			Assert.assertEquals(actualList, expectedList);
			Iterator<String> expectedIterator = expectedList.iterator();
			int index = 1;
			for (Iterator<String> actualIterator = actualList.iterator(); actualIterator.hasNext();) {
				String actual = (String) actualIterator.next();
				String expected = (String) expectedIterator.next();
				Assert.assertEquals(""+index ,actual, expected);	
				index++;
			}
			
		}
	}
	@Test
	public void testResolveNumber() {
		//given
		Map<Integer, String> expectedNumberMap = new HashMap<Integer, String>();
		expectedNumberMap.put(21, "dvidešimt viena");
		expectedNumberMap.put(32, "trisdešimt dvi");
		expectedNumberMap.put(55, "penkiasdešimt penkios");
		Map<Integer, String> actualNumberMap = new HashMap<Integer, String>();
		//then
		for (Entry<Integer, String> numberEntry : expectedNumberMap.entrySet()) {
			Integer number = numberEntry.getKey();
			actualNumberMap.put(number,helper.resolveNumber(number, GenusEnum.feminine));	
		}
		//when
		for (Entry<Integer, String> expectedEntry : expectedNumberMap.entrySet()) {
			Integer number = expectedEntry.getKey();
			Assert.assertEquals(expectedEntry.getValue(), actualNumberMap.get(number));
		}
	}
	
}
