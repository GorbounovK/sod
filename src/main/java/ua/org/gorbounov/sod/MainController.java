package ua.org.gorbounov.sod;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class MainController {

	@GetMapping(value = {"/","/index"})
	public String index() {
		log.debug("/index");
		return "index";
	}
	
	@GetMapping("/login")
    public String login() {
		log.debug("/login");
       return "login";
    }
 
    @GetMapping("/403")
    public String error403() {
		log.debug("/403");
      return "error/403";
    }
}
