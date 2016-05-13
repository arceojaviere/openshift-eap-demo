package org.test.security;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("securityController")
@ApplicationScoped
public class SecurityController {
	public static String logout(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/secure/main.jsf?faces-redirect=true";
	}
	public String getUser(){
		String user;
		System.out.println("Principal: " + FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getClass());
		user = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName(); 
		return user;
	}
}
