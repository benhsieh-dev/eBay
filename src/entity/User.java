package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@Column(name = "user_id", length = 45)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userId;

	@Column(name = "user_first_name", length = 255)
	private String userFirstName;

	@Column(name = "user_last_name", length = 255)
	private String userLastName;

	@Column(name = "address", length = 255)
	private String address;

	@Column(name = "mobile", length = 20)
	private String mobile;

	@Column(name = "username", length = 255)
	private String username;

	@Column(name = "password", length = 255)
	private String password;

	public User(int userId, String userFirstName, String userLastName, String address, String mobile, String username,
			String password) {
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.address = address;
		this.mobile = mobile;
		this.username = username;
		this.password = password;
	}

	public User() {
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName
				+ ", address=" + address + ", mobile=" + mobile + ", username=" + username + ", password=" + password
				+ "]";
	}

}
