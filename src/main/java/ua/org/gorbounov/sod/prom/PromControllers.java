package ua.org.gorbounov.sod.prom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.models.PromImportOrdersInfo;

@Controller
@Log4j2
@RequestMapping("/prom")
public class PromControllers {

	@Autowired
	private PromImportOrdersInfo promImportOrdersInfo;

	@PostMapping("/save_cron_import_orders")
	public String save_cron_import_orders(@ModelAttribute PromImportOrdersInfo promImportOrdersInfo, Model model) {
		log.debug("prom/save_cron_import_orders");
		log.debug("cron_import_orders="+promImportOrdersInfo.getCron());
		return "prom/ImportOrders";

	}
	@GetMapping("/importOrders")
	public String importOrders(Model model) {
		log.debug("prom/ImportOrders");
		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		return "prom/ImportOrders";

	}
	
	@GetMapping("/exportPrice")
	public String exportPrice() {
		log.debug("prom/ExportPrice");
		return "prom/ExportPrice";

	}
}
