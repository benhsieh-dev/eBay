package dto;

public class UserDTO {

	private int userId;

	private String username;

	private String address;

	private int mobile;

	public UserDTO(int userId, String username, String address, int mobile) {
		super();
		this.userId = userId;
		this.username = username;
		this.address = address;
		this.mobile = mobile;
	}

	public UserDTO() {
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
		return "UserDTO [userId=" + userId + ", username=" + username + ", address=" + address + ", mobile=" + mobile
				+ "]";
	}

}
