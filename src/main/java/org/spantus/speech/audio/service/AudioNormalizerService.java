package org.spantus.speech.audio.service;

import java.io.InputStream;

public interface AudioNormalizerService {
	public InputStream normalize(InputStream inputStream);
}
