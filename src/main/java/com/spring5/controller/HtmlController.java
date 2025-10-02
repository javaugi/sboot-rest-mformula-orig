/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class HtmlController {

	@RequestMapping("/{page:^(?!.*[.].*$).*$}")
	public Mono<String> requestPage(@PathVariable("page") String page) {
		String htmlPage = "/" + page + ".html";
		log.debug("forwarding request to {}", htmlPage);
		return Mono.just(htmlPage);
	}

}
