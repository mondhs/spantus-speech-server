package org.spantus.speech.recognition.service.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.speech.recognition.dto.RecognitionSummary;
import org.spantus.speech.recognition.service.SpantusRecognitionServiceImpl;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SpantusRecognitionServiceImplTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(SpantusRecognitionServiceImplTest.class);

	SpantusRecognitionServiceImpl recognitionServiceImpl;

	@Before
	public void onSetup() {
		recognitionServiceImpl = new SpantusRecognitionServiceImpl();
	}

	@Test
	public void testRecognizeCalculatorConfig() throws URISyntaxException {
		// give
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		retrieveAudioStreamByName("/vienas-plius-du.wav", stream);
		// when
		RecognitionSummary actual = recognitionServiceImpl.recognize(stream, "test", 
				SpantusRecognitionServiceImpl.RecognizerConfigGrammar.lt_numeriai.name());
		// then
		Assert.assertEquals("matches", "devyni,vienas,vienas", actual.getResult());
	}

	@Test
	public void testRecognizeRobotConfig() throws URISyntaxException {
		// give
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		retrieveAudioStreamByName("/test1.wav", stream);
		// when
		RecognitionSummary actual = recognitionServiceImpl.recognize(stream, "test", 
				SpantusRecognitionServiceImpl.RecognizerConfigGrammar.lt_robotas.name());
		// then
		Assert.assertEquals("matches", "eik pirmyn", actual.getResult());
	}
	
	@Test
	public void testRecognizeDigitsConfig() {
		// give
		URL audioURL = SpantusRecognitionServiceImpl.class
				.getResource("/10001-90210-01803.wav");
		URL configURL = SpantusRecognitionServiceImpl.class
				.getResource("config-en_digits.xml");
		ConfigurationManager cm = new ConfigurationManager(configURL);
		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		AudioFileDataSource dataSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");
		dataSource.setAudioFile(audioURL, null);
		Result result;
		StringBuilder sb=  new StringBuilder();
		String separator = "";
		while ((result = recognizer.recognize()) != null) {
			String resultText = result.getBestResultNoFiller();
			sb.append(separator).append(resultText);
			separator=",";
		}
		
		Assert.assertEquals("one zero zero zero one,nine oh two one oh,zero one eight zero three,", sb.toString());

	}

	public void retrieveAudioStreamByName(String fileName, OutputStream output) throws URISyntaxException {
		URL audioURL = SpantusRecognitionServiceImpl.class
				.getResource(fileName);
		File file = new File(audioURL.toURI());
		if (!fileName.endsWith("wav")) {
			throw new IllegalArgumentException(
					"What you are looking for? there is no access:" + fileName);
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = fis.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}
			output.flush();
			output.close();
			fis.close();
		} catch (FileNotFoundException e) {
			LOG.error("There is no file: " + fileName, e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LOG.error("I/O exception: " + fileName, e);
			throw new IllegalArgumentException(e);
		}
	}

}
