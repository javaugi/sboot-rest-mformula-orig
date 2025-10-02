package com.spring5.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("prod")
@RestController
@RequestMapping(path = "/private")
public class RedirectController {

	@GetMapping
	public String redirectToRoot() {
		return "redirect:/";
	}

}
