package org.spantus.speech.audio.service;

/*
 *	SampleRateConverter.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2003 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public class AudioNormalizerServiceImpl implements AudioNormalizerService{

	private static final Logger LOG = LoggerFactory
			.getLogger(AudioNormalizerServiceImpl.class);
	private static final int MONO_CHANNEL = 1;
	private int fileMaxSize = Integer.MAX_VALUE;

	@Override
	public InputStream normalize(InputStream inputStream) {
		AudioInputStream audioInputStream;
		try {
			File parentTmpDid = Files.createTempDir();
			File incommingFile = new File(parentTmpDid,"incomming.wav");
			File outgoingFile = new File(parentTmpDid,"outgoing.wav");
			ByteStreams.copy(inputStream, new FileOutputStream(incommingFile));
			audioInputStream = AudioSystem.getAudioInputStream(incommingFile);
			LOG.debug("[normalize] input audio format {}", audioInputStream.getFormat());
			audioInputStream = convert(16000F, audioInputStream);
			LOG.debug("[normalize] output audio format {}", audioInputStream.getFormat());
			Type targetFileType = AudioSystem.getAudioFileFormat(incommingFile).getType();
			AudioSystem.write(audioInputStream, targetFileType,
					outgoingFile);
			return Files.newInputStreamSupplier(outgoingFile).getInput();
		} catch (UnsupportedAudioFileException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	
	public void convert(Float fTargetSampleRate, File sourceFile,
			File targetFile) {
		LOG.debug("target sample rate: {}", fTargetSampleRate);
		/*
		 * We try to use the same audio file type for the target file as the
		 * source file. So we first have to find out about the source file's
		 * properties.
		 */
		AudioFileFormat sourceFileFormat;
		try {
			sourceFileFormat = AudioSystem.getAudioFileFormat(sourceFile);
			/*
			 * Here, we are reading the source file.
			 */
			AudioInputStream sourceStream = null;
			sourceStream = AudioSystem.getAudioInputStream(sourceFile);
			if (sourceStream == null) {
				throw new IllegalArgumentException(
						"cannot open source audio file: " + sourceFile);
			}
			AudioFormat sourceFormat = sourceStream.getFormat();
			AudioFileFormat.Type targetFileType = sourceFileFormat.getType();
			LOG.debug("source format: {}", sourceFormat);

			/*
			 * Currently, the only known and working sample rate converter for
			 * Java Sound requires that the encoding of the source stream is PCM
			 * (signed or unsigned). So as a measure of convenience, we check if
			 * this holds here.
			 */
			AudioFormat.Encoding encoding = sourceFormat.getEncoding();
			if (!AudioCommon.isPcm(encoding)) {
				throw new IllegalArgumentException(
						"encoding of source audio data is not PCM; conversion not possible: " + sourceFile);
			}

			AudioInputStream targetStream = convert(fTargetSampleRate, sourceStream);

			/*
			 * And finally, we are trying to write the converted audio data to a
			 * new file.
			 */
			int nWrittenBytes = 0;
			nWrittenBytes = AudioSystem.write(targetStream, targetFileType,
					targetFile);
			if(nWrittenBytes > getFileMaxSize()){
				throw new IllegalArgumentException(MessageFormat.format("File is longer ({0}) than max sized ({1}) allowed",nWrittenBytes , getFileMaxSize()) );
			}
			LOG.debug("Written bytes: " + nWrittenBytes);

		} catch (UnsupportedAudioFileException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}
	
	public int getFileMaxSize() {
		return fileMaxSize;
	}


	public AudioInputStream convert(Float fTargetSampleRate, AudioInputStream sourceStream){
		AudioInputStream targetStream = sourceStream;
		targetStream = convertToMono(targetStream);
		targetStream = convertMonoToSampleRate(fTargetSampleRate, targetStream);
		return targetStream;
	}
	
	/**
	 * 
	 * @param sourceStream
	 * @return
	 */
	private AudioInputStream convertToMono(AudioInputStream sourceStream) {

		AudioFormat sourceFormat = sourceStream.getFormat();
		
		if(sourceStream.getFormat().getChannels() == 1){
			LOG.debug("[convertToMono] is alredy mono");
			return sourceStream;
		}
		
		/*
		 * Here, we are constructing the desired format of the audio data
		 * (as the result of the conversion should be). We take over all
		 * values besides the sample/frame rate.
		 */

		AudioFormat targetFormat = new AudioFormat(
				sourceFormat.getEncoding(), sourceFormat.getSampleRate(), 
				sourceFormat.getSampleSizeInBits(),
				MONO_CHANNEL, sourceFormat.getFrameSize(),
				sourceFormat.getFrameRate(), sourceFormat.isBigEndian());
		LOG.debug("[convertToMono] target format {}", targetFormat);

		// Try to mono first
		PCM2PCMConversionProvider converter = new PCM2PCMConversionProvider();
		AudioInputStream targetStream = converter.getAudioInputStream(targetFormat, sourceStream);
		
		return targetStream;
	}
	/**
	 * 
	 * @param fTargetSampleRate
	 * @param sourceStream
	 * @return
	 */
	private AudioInputStream convertMonoToSampleRate(Float fTargetSampleRate, AudioInputStream sourceStream) {
		AudioFormat sourceFormat = sourceStream.getFormat();

		/*
		 * Here, we are constructing the desired format of the audio data
		 * (as the result of the conversion should be). We take over all
		 * values besides the sample/frame rate.
		 */
		AudioFormat targetFormat = new AudioFormat(
				sourceFormat.getEncoding(), fTargetSampleRate,
				sourceFormat.getSampleSizeInBits(),
				MONO_CHANNEL, sourceFormat.getFrameSize(),
				fTargetSampleRate, sourceFormat.isBigEndian());

		LOG.debug("[convertMonoToSampleRate]desired target format: " + targetFormat);

		
		/*
		 * Now, the conversion takes place.
		 */
		AudioInputStream targetStream = AudioSystem.getAudioInputStream(
				targetFormat, sourceStream);
		LOG.debug("targetStream: " + targetStream);		
		return targetStream;
	}


	public void setFileMaxSize(int fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}


}

/*** SampleRateConverter.java ***/

