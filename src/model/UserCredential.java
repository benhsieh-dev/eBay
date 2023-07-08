package model;

import org.hibernate.validator.constraints.Email;

public class UserCredential {

//	@NotEmpty
	@Email
	private String email;

//	@NotEmpty
//	@username
	private String username;

//	@NotEMpty
//	@password
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

}
