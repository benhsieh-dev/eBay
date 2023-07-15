package main.java.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

	@GetMapping(value = "/products")
	public String showProductList() {

		return "product-list";
	}

}
