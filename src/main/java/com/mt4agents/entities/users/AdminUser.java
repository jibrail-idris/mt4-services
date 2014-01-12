package com.mt4agents.entities.users;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@DiscriminatorValue("admin")
public class AdminUser extends User {
	private static final long serialVersionUID = -6483456531022145528L;
	public static final String ROLE = "admin";
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(ROLE));
		return authorities;
	}
}
