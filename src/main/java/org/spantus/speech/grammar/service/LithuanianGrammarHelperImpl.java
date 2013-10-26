package org.spantus.speech.grammar.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.grammar.dto.GenusEnum;


public class LithuanianGrammarHelperImpl {
	
	Logger LOG = LoggerFactory.getLogger(LithuanianGrammarHelperImpl.class);
	
	public static final Map<Integer,String> resolveNumberDivMap = new HashMap<Integer,String>();
	/**
	 * http://ualgiman.dtiltas.lt/skaitvardis.html
	 */
	public static final Map<Integer,String> resolveNumberModFemininumMap = new HashMap<Integer,String>();
	public static final Map<Integer,String> resolveNumberModMasculinumMap = new HashMap<Integer,String>();
	public static Map<String, String> genitivePluralis = new LinkedHashMap<String, String>();
	public static Map<String, String> dativeSingularis = new LinkedHashMap<String, String>();
	public static Map<String, String> nominativePluralis = new LinkedHashMap<String, String>();

	/**
	 * Cases:
	 * Nominative - vardininkas
	 * Genitive - kilmininkas
	 * Dative - Naudininkas
	 * Accusative - Galininkas
	 * Ablative - Įnagininkas
	 * Vocative - Vietininkas
	 * 
	 */
	static {
		
		dativeSingularis.put("IS", "IUI");//laipsnis laipsniams
		nominativePluralis.put("IS", "IAI");//laipsniai
		genitivePluralis.put("IS", "IŲ");//laipsnių
		
		dativeSingularis.put("US", "UI");//laipsnis laipsniams
		nominativePluralis.put("US", "AI");//laipsniai
		genitivePluralis.put("US", "Ų");//laipsnių
		
		dativeSingularis.put("AS", "UI");//darbas darbui
		nominativePluralis.put("AS", "AI");// darbai
		genitivePluralis.put("AS", "Ų");//darbų

		dativeSingularis.put("TĖ", "TEI");//minutė minutei
		nominativePluralis.put("TĖ", "TĖS");// minutės
		genitivePluralis.put("TĖ", "ČIŲ");//minučių
		
		dativeSingularis.put("Ė", "EI");//tinginė tinginei
		nominativePluralis.put("Ė", "ĖS");// tinginės
		genitivePluralis.put("Ė", "IŲ");//tingių
		
		
		dativeSingularis.put("A", "AI");//valanda valandai
		nominativePluralis.put("A", "OS");// valandos
		genitivePluralis.put("A", "Ų");//valandų
		
		

		
		
		resolveNumberDivMap.put(2, "dvidešimt");
		resolveNumberDivMap.put(3, "trisdešimt");
		resolveNumberDivMap.put(4, "keturesdešimt");
		resolveNumberDivMap.put(5, "penkiasdešimt");

		resolveNumberModFemininumMap.put(0, "nulis");
		resolveNumberModFemininumMap.put(1, "viena");
		resolveNumberModFemininumMap.put(2, "dvi");
		resolveNumberModFemininumMap.put(3, "trys");
		resolveNumberModFemininumMap.put(4, "keturios");
		resolveNumberModFemininumMap.put(5, "penkios");
		resolveNumberModFemininumMap.put(6, "šešios");
		resolveNumberModFemininumMap.put(7, "septynios");
		resolveNumberModFemininumMap.put(8, "aštuonios");
		resolveNumberModFemininumMap.put(9, "devynios");
		resolveNumberModFemininumMap.put(10, "dešimt");
		resolveNumberModFemininumMap.put(11, "vienuolika");
		resolveNumberModFemininumMap.put(12, "dvylika");
		resolveNumberModFemininumMap.put(13, "trylika");
		resolveNumberModFemininumMap.put(14, "keturiolika");
		resolveNumberModFemininumMap.put(15, "penkiolika");
		resolveNumberModFemininumMap.put(16, "šešiolika");
		resolveNumberModFemininumMap.put(17, "septyniolika");
		resolveNumberModFemininumMap.put(18, "aštuoniolika");
		resolveNumberModFemininumMap.put(19, "devyniolika");
		
		resolveNumberModMasculinumMap.put(0, "nulis");
		resolveNumberModMasculinumMap.put(1, "vienas");
		resolveNumberModMasculinumMap.put(2, "du");
		resolveNumberModMasculinumMap.put(3, "trys");
		resolveNumberModMasculinumMap.put(4, "keturi");
		resolveNumberModMasculinumMap.put(5, "penki");
		resolveNumberModMasculinumMap.put(6, "šeši");
		resolveNumberModMasculinumMap.put(7, "septyni");
		resolveNumberModMasculinumMap.put(8, "aštuoni");
		resolveNumberModMasculinumMap.put(9, "devyni");
		resolveNumberModMasculinumMap.put(10, "dešimt");
		resolveNumberModMasculinumMap.put(11, "vienuolika");
		resolveNumberModMasculinumMap.put(12, "dvylika");
		resolveNumberModMasculinumMap.put(13, "trylika");
		resolveNumberModMasculinumMap.put(14, "keturiolika");
		resolveNumberModMasculinumMap.put(15, "penkiolika");
		resolveNumberModMasculinumMap.put(16, "šešiolika");
		resolveNumberModMasculinumMap.put(17, "septyniolika");
		resolveNumberModMasculinumMap.put(18, "aštuoniolika");
		resolveNumberModMasculinumMap.put(19, "devyniolika");
	}

	public String resolveNumber(int number, GenusEnum genus) {
		Map<Integer, String> resolveNumberMod = resolveNumberModFemininumMap;
		if(GenusEnum.masculine.equals(genus)){
			resolveNumberMod = resolveNumberModMasculinumMap;
		}
		String rtn = "";
		int numberDiv = number / 10;
		int numberMod = number % 10;
		if (number < 20) {
			rtn = resolveNumberMod.get(number);
		} else if (numberDiv == 0) {// 0-9
			rtn = resolveNumberMod.get(numberMod);
		} else if (numberDiv == 1) {// #10-19
			rtn = "" + number;
		} else if (numberMod == 0) {// 20,30,40,50
			rtn = resolveNumberDivMap.get(numberDiv);
		} else {
			rtn = "" + resolveNumberDivMap.get(numberDiv) + " "
					+ resolveNumberMod.get(numberMod);
		}

		return rtn;
	}
	
	/**
	 * lt: Naudininkas. en: Dative Case
	 * @param contact
	 * @return
	 */
	public String makeNounToDatSng(String aNounNominative) {
		String noun = aNounNominative;
		for (Entry<String, String> ending : dativeSingularis.entrySet()) {
			String regExp = MessageFormat.format(".*{0}$", ending.getKey());
			if(noun.matches(regExp)){
				regExp = MessageFormat.format("{0}$", ending.getKey());
				return noun.replaceFirst(regExp, ending.getValue());
			}
		}
		LOG.error("[makeNounToDatSng] match not found for {}", aNounNominative);
		return null;
	}
	
	/**
	 * lt: Vardininkas. en: Nominative Pluralis
	 * @param contact
	 * @return
	 */
	public String makeNounToNomPlr(String aNounNominative) {
		String noun = aNounNominative;
		for (Entry<String, String> ending : nominativePluralis.entrySet()) {
			String regExp = MessageFormat.format(".*{0}$", ending.getKey());
			if(noun.matches(regExp)){
				regExp = MessageFormat.format("{0}$", ending.getKey());
				return noun.replaceFirst(regExp, ending.getValue());
			}
		}
		LOG.error("[makeNounToNomPlr] match not found for {}", aNounNominative);
		return null;
	}
	
	/**
	 * lt: Vardininkas. en: Genitive Pluralis
	 * @param contact
	 * @return
	 */
	public String makeNounToGenPlr(String aNounNominative) {
		String noun = aNounNominative;
		for (Entry<String, String> ending : genitivePluralis.entrySet()) {
			String regExp = MessageFormat.format(".*{0}$", ending.getKey());
			if(noun.matches(regExp)){
				regExp = MessageFormat.format("{0}$", ending.getKey());
				return noun.replaceFirst(regExp, ending.getValue());
			}
		}
		LOG.error("[makeNounToGenPlr] match not found for {}", aNounNominative);
		return null;
	}

	
	
	/**
	 * 
	 * @param aNounDative
	 * @return
	 */
	public String stripDatEnding(String aNounDative) {
		String noun = aNounDative;
		for (Entry<String, String> ending : dativeSingularis.entrySet()) {
			String regExp = MessageFormat.format(".*{0}$", ending.getValue());
			if(noun.matches(regExp)){
				regExp = MessageFormat.format("{0}$", ending.getValue());
				return noun.replaceFirst(regExp, "");
			}
		}
		LOG.error("[stripDatEnding] match not found for {}", aNounDative);
		return null;
	}
	
	public String matchNounToNumerales(int number, String nounSingularNominative) {
		String nounNominativusPluralis = null; 
		String nounGenitivusSingularis = null;
				
		String rtn = nounSingularNominative.toUpperCase();
		for (Entry<String, String> ending : genitivePluralis.entrySet()) {
			String regExp = MessageFormat.format(".*{0}$", ending.getKey());
			if(rtn.matches(regExp)){
				regExp = MessageFormat.format("{0}$", ending.getKey());
				nounGenitivusSingularis = rtn.replaceFirst(regExp, ending.getValue());
				nounNominativusPluralis = rtn.replaceFirst(regExp, nominativePluralis.get(ending.getKey()));
				break;
			}
		}
		if(nounNominativusPluralis == null){
			return nounSingularNominative;
		}
		
		int minutesDiv = number / 10;
		int minutesMod = number % 10;
		if (minutesMod == 0) { // 0, 10, 20, 30, 40, 50
			rtn = nounGenitivusSingularis;//minučių
		} else if (minutesDiv == 1) {// 10-19
			rtn = nounGenitivusSingularis;//minučių, laipsnių, valandų
		} else if (minutesMod == 1) {// 1, 21,
			rtn = nounSingularNominative;//"minutė";
		}else{
			rtn = nounNominativusPluralis;//"minutės";
		}
		return rtn;
	}
	
}
