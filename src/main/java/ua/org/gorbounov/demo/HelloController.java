package ua.org.gorbounov.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
	private static Logger LOG = LogManager.getLogger(HelloController.class);	
	
	@GetMapping("/hello-world")
	public String sayHello() {
		LOG.info("/hello-world");
		return "hello_world";
	}
}
