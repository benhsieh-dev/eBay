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

}
