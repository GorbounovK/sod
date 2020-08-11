package ua.org.gorbounov.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/first")
public class FirstController {
	@GetMapping("/hello")
	public String helloPage() {
		return "first/hello";
	}
	
	@GetMapping("/goodbye")
	public String gooByePage() {
		return "first/goodbye";
	
	}
}
