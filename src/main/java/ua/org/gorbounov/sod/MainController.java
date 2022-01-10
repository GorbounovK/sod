package ua.org.gorbounov.sod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.models.ApplicationProperties;

@Log4j2
@Controller
public class MainController {
	@Autowired
	private ApplicationProperties prop;

	@GetMapping(value = {"/","/index"})
	public String index(Model model) {
		log.debug("/index");
		log.trace("prop.isEmagEnable()={}",prop.isEmagEnabled());
		log.trace("prop.isRozetkaEnable()={}",prop.isRozetkaEnabled());
		model.addAttribute("prop", prop);
		return "index";
	}
	
	@GetMapping("/login")
    public String login(Model model) {
		log.debug("/login");
		model.addAttribute("prop", prop);      
		return "login";
    }
 
    @GetMapping("/403")
    public String error403() {
		log.debug("/403");
      return "error/403";
    }
}
