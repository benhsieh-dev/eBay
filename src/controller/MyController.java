package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import model.User;
import model.UserCredential;
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

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homePage() {
		return "home";
	}

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String loginPage(Model model) {
		model.addAttribute("userCredential", new UserCredential());
		return "login";

	}

	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String registerPage(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@RequestMapping(value = "/registerSuccess", method = RequestMethod.POST)
	public ModelAndView registerSuccess(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("register");
		}
		getUserService().registerUser(user);
		ModelAndView modelAndView = new ModelAndView("welcome");
		modelAndView.addObject("user", user);
		return modelAndView;
	}

	@RequestMapping(value = "/loginSuccess", method = RequestMethod.POST)
	public ModelAndView loginSuccess(@Valid @ModelAttribute("userCredential") UserCredential userCredential,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("welcome");
		User user = getUserService().validateUserCredential(userCredential.getEmail(), UserCredential.getPassword());
		if (user != null) {
			modelAndView.addObject("user", user);
			return modelAndView;
		} else {
			modelAndView = new ModelAndView("notFound");
		}
		return modelAndView;
	}

	@ModelAttribute
	public void headerMessage(Model model) {

	}
}
