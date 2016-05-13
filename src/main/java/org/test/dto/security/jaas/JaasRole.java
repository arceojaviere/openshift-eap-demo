package org.test.dto.security.jaas;

import java.security.Principal;

import org.test.dto.security.Role;

public class JaasRole extends Role implements Principal{
	
	@Override
	public String getName() {
		return this.getId();
	}
}
