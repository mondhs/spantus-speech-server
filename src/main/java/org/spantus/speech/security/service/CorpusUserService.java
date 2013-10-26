package org.spantus.speech.security.service;

import org.spantus.speech.security.dto.CorpusUser;

public interface CorpusUserService {
	CorpusUser findCorpusUserByName(String userName);
	String crteatCorpusUser(String userName, String password);
	CorpusUser findCurrent();
}
