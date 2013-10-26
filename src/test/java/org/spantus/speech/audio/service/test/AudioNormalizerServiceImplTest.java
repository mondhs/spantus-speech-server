package org.spantus.speech.audio.service.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.speech.audio.service.AudioNormalizerServiceImpl;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;

import com.google.common.io.Files;

public class AudioNormalizerServiceImplTest {
	
	private AudioNormalizerServiceImpl audioNormalizerServiceImpl;
	
	@Before
	public void setUp() throws Exception {
		audioNormalizerServiceImpl = new AudioNormalizerServiceImpl();
	}

	@Test
	public void testConvert() throws UnsupportedAudioFileException, IOException {
		//given
		Float fTargetSampleRate = 16000F;
		File sourceFile = new File("./src/test/resources/vienas-du-trys.wav");
		File targetFile = new File("./target/test.wav");
		//then
		audioNormalizerServiceImpl.convert(fTargetSampleRate, sourceFile, targetFile);
		//when
		AudioFileFormat actualAudioFileFormat = AudioSystem.getAudioFileFormat(targetFile);
		Assert.assertEquals(1, actualAudioFileFormat.getFormat().getChannels());
		Assert.assertEquals(16000F, actualAudioFileFormat.getFormat().getSampleRate(),0);
	}
	
	public void testNormalize() throws UnsupportedAudioFileException, IOException {
		//given
		File sourceFile = new File("./src/test/resources/vienas-du-trys.wav");

		//then
		InputStream stream = audioNormalizerServiceImpl.normalize(Files.newInputStreamSupplier(sourceFile).getInput());
		//when
		
		AudioFileFormat format = AudioSystem.getAudioFileFormat(stream);
		
		Assert.assertEquals(1, format.getFormat().getChannels());
		Assert.assertEquals(16000F, format.getFormat().getSampleRate(),0);
	}

	

	
}
