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

	@Column(name = "username", length = 255)
	private String username;

	@Column(name = "address", length = 255)
	private String address;

	@Column(name = "mobile", length = 20)
	private int mobile;

	public User(int userId, String username, String address, int mobile) {
		super();
		this.userId = userId;
		this.username = username;
		this.address = address;
		this.mobile = mobile;
	}

	public User() {
		super();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", address=" + address + ", mobile=" + mobile
				+ "]";
	}

}
