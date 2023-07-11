package main.java.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProductController {

	@RequestMapping(value = "/showProduct", method = RequestMethod.GET)
	public String showProductList() {

		return "product-list";
	}

}
