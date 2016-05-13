package org.test.dto.security.jaas;

import java.security.Principal;

import org.test.dto.security.User;

public class JaasUser extends User implements Principal{

	@Override
	public String getName() {
		return this.getId();
	}

}
