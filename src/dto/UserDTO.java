package dto;

public class UserDTO {

	private int userId;

	private String userFirstName;

	private String userLastName;

	private String address;

	private String mobile;

	private String username;

	private String password;

	public UserDTO(int userId, String userFirstName, String userLastName, String address, String mobile,
			String username, String password) {
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.address = address;
		this.mobile = mobile;
		this.username = username;
		this.password = password;
	}

	public UserDTO() {

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
		return "UserDTO [userId=" + userId + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName
				+ ", address=" + address + ", mobile=" + mobile + ", username=" + username + ", password=" + password
				+ "]";
	}

}
