package controller;

import org.springframework.stereotype.Controller;

import service.UserService;

@Controller
public class MyController {

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;

	}

	public UserService getUserService() {
		return userService;
	}

}
