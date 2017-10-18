package com.ece651.handymenserver.RestSvr;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsrService {

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}
}