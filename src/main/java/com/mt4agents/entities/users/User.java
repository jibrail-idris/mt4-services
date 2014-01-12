package com.mt4agents.entities.users;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "username" }))
@DynamicUpdate
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 20)
@SequenceGenerator(name = "UserGenerator")
public abstract class User implements UserDetails {

	private static final long serialVersionUID = 6619257841212296185L;

	@Id
	@GeneratedValue(generator = "UserGenerator", strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 50)
	@Type(type = "string")
	private String username;

	@Column(nullable = false, length = 50)
	@Type(type = "string")
	private String password;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}
