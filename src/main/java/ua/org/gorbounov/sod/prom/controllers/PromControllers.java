package ua.org.gorbounov.sod.prom.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.services.ImportOrdersInfoService;
import ua.org.gorbounov.sod.prom.services.OrdersService;
import ua.org.gorbounov.sod.repositories.PromOrdersEntityRepozitories;

@Controller
@Log4j2
@RequestMapping("/prom")
public class PromControllers {

	@Autowired
	private PromImportOrdersInfo promImportOrdersInfo;
	
	@Autowired
	private ImportOrdersInfoService ordersInfoService;
//	private PromOrdersEntityRepozitories promImportOrdersRepositories;
	
	@Autowired
	private OrdersService ordersService;

	@PostMapping("/save_cron_import_orders")
	public String save_cron_import_orders(@ModelAttribute PromImportOrdersInfo promImportOrdersInfo, Model model) {
		log.debug("prom/save_cron_import_orders");
		log.debug("cron_import_orders="+promImportOrdersInfo.getCron());
		return "prom/ImportOrders";
	}

	@GetMapping("/importOrders")
	public String importOrders(Model model) {
		log.debug("prom/ImportOrders");
//		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo(1, 20, "id");
		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo();
		log.debug("ordersInfoEntity.size {}",ordersInfoEntity.size());
		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		model.addAttribute("ordersInfoEntity", ordersInfoEntity);
		return "prom/ImportOrders";
	}
	
	@GetMapping("/getOrders")
	public String getOrders(Model model) {
		log.debug("prom/getOrders");
		ordersService.getOrdersSheduledTask();
//		String result = ordersService.getResultAction();
		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo(1, 20, "id");
		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		model.addAttribute("ordersInfoEntity", ordersInfoEntity);
		return "prom/ImportOrders";
	
	}
	
	@GetMapping("/exportPrice")
	public String exportPrice() {
		log.debug("prom/ExportPrice");
		return "prom/ExportPrice";

	}
}
