package org.spantus.speech.grammar.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.grammar.dto.GenusEnum;
import org.spantus.speech.grammar.service.GraphemeToPhonemeMapperLithuanianImpl;
import org.spantus.speech.grammar.service.LithuanianGrammarHelperImpl;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/grammar/lt")
public class LithuanianGrammarResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(LithuanianGrammarResource.class);
	private GraphemeToPhonemeMapperLithuanianImpl phonemeMapper;
	private LithuanianGrammarHelperImpl grammarHelper;

	public LithuanianGrammarResource() {
		phonemeMapper = new GraphemeToPhonemeMapperLithuanianImpl();
		grammarHelper = new LithuanianGrammarHelperImpl();
	}

	@GET
	@Path("/phonemes/{word}")
	public List<String> transformToPhonemes(@PathParam("word") String word)
			throws Exception {
		LOG.error("[transformToPhonemes] word {}", word);
		return phonemeMapper.transform(resolveIfNumber(word, GenusEnum.masculine));
	}

	@GET
	@Path("/phonemes/{word}/multiple")
	public Map<String, List<String>> transformMultipleToPhonemes(
			@PathParam("word") String joinedWords) throws Exception {
		LOG.error("[transformMultipleToPhonemes] word {}", joinedWords);
		Iterable<String> words = Splitter.onPattern("[\\s,\\.\\n\\r]+")
				.omitEmptyStrings().trimResults().split(joinedWords);
		words = Iterables.transform(words, new Function<String, String>() {
			@Override
			public String apply(@Nullable String input) {
				return resolveIfNumber(input, GenusEnum.masculine);
			}

		});
		return phonemeMapper.transform(words);
	}

	@GET
	@Path("/noun/{word}/case/dative/singularis")
	public String transformToDativeSingularis(
			@PathParam("word") String aNounNominative) throws Exception {
		LOG.error("[transformToDativeSingularis] word {}", aNounNominative);
		return grammarHelper.makeNounToDatSng(aNounNominative.toUpperCase());
	}

	@GET
	@Path("/noun/{word}/case/nominative/pluralis")
	public String trnsformToNominativePluralis(
			@PathParam("word") String aNounNominative) throws Exception {
		LOG.error("[trnsformToNominativePluralis] word {}", aNounNominative);
		return grammarHelper.makeNounToNomPlr(aNounNominative.toUpperCase());
	}

	@GET
	@Path("/noun/{word}/case/genitive/pluralis")
	public String transformToGenitivePluralis(
			@PathParam("word") String aNounNominative) throws Exception {
		LOG.error("[transformToGenitivePluralis] word {}", aNounNominative);
		return grammarHelper.makeNounToGenPlr(aNounNominative.toUpperCase());
	}

	@GET
	@Path("/noun/{word}/numerales/{number}")
	public String matchNounToNumerales(
			@PathParam("word") String nounSingularNominative,
			@PathParam("number") int number) throws Exception {
		LOG.error("[matchNounToNumerales] word {} number {}",
				nounSingularNominative, number);
		return grammarHelper.matchNounToNumerales(number,
				nounSingularNominative);
	}
	private String resolveIfNumber(String input, GenusEnum genus) {
		if (input.matches("\\d+")) {
			LOG.error("[resolveIfNumber] is number {}", input);
			return grammarHelper.resolveNumber(Integer.valueOf(input),
					genus).toUpperCase();
		}
		return input;
	}
}
