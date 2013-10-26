package org.spantus.speech.security.service.local;

import org.spantus.speech.security.dto.CorpusUser;
import org.spantus.speech.security.service.CorpusUserService;

public class CorpusUserServiceLocal implements CorpusUserService {

	@Override
	public CorpusUser findCorpusUserByName(String userName) {
		return new CorpusUser();
	}

	@Override
	public String crteatCorpusUser(String userName, String password) {
		return null;
	}

	@Override
	public CorpusUser findCurrent() {
		return new CorpusUser();
	}

}
