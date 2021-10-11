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
import ua.org.gorbounov.sod.models.PromExportPriceEntity;
import ua.org.gorbounov.sod.models.PromExportPriceInfo;
import ua.org.gorbounov.sod.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.services.ExportPriceInfoService;
import ua.org.gorbounov.sod.prom.services.ExportService;
import ua.org.gorbounov.sod.prom.services.ImagesService;
import ua.org.gorbounov.sod.prom.services.ImportOrdersInfoService;
import ua.org.gorbounov.sod.prom.services.OrdersService;
import ua.org.gorbounov.sod.repositories.PromOrdersEntityRepozitories;

/**
 * @author gk
 *
 */
@Controller
@Log4j2
@RequestMapping("/prom")
public class PromControllers {

	@Autowired
	private PromImportOrdersInfo promImportOrdersInfo;
	
	@Autowired
	private PromExportPriceInfo promExportPriceInfo;
	
	@Autowired
	private ImportOrdersInfoService ordersInfoService;
//	private PromOrdersEntityRepozitories promImportOrdersRepositories;
	@Autowired
	private ExportPriceInfoService exportPriceInfoService;
	
	@Autowired
	private OrdersService ordersService;
	@Autowired
	private ExportService exportService;
	
	@Autowired
	private ImagesService imageService;


	/**
	 * @param promImportOrdersInfo
	 * @param model
	 * @return
	 */
	@PostMapping("/save_cron_import_orders")
	public String save_cron_import_orders(@ModelAttribute PromImportOrdersInfo promImportOrdersInfo, Model model) {
		log.debug("prom/save_cron_import_orders");
		log.debug("cron_import_orders="+promImportOrdersInfo.getCron());
		return "prom/ImportOrders";
	}

	@GetMapping("/importOrders")
	public String importOrders(Model model) {
		log.debug("prom/ImportOrders");
//		String promImportOrdersInfo=promImportOrdersInfo;
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
		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo();
		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		model.addAttribute("ordersInfoEntity", ordersInfoEntity);
		return "prom/ImportOrders";
	
	}
	
	@GetMapping("/exportPriceInfo")
	public String exportPriceInfo(Model model) {
		log.debug("prom/ExportPriceInfo");
		List<PromExportPriceEntity> exportPriceEntity = exportPriceInfoService.getAllImportOrdersInfo();
		model.addAttribute("exportPriceEntity",exportPriceEntity);
		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		return "prom/ExportPriceInfo";
		
	}
	
	
	@GetMapping("/exportPrice")
	public String exportPrice(Model model) {
		log.debug("prom/ExportPrice");
		exportService.exportProductSheduledTask();
		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		return "prom/ExportPriceInfo";
	}

	@GetMapping("/exportImagesInfo")
	public String exportImagesInfo() {
		log.debug("prom/exportImagesInfo");
		return "prom/ExportImagesInfo";
	}
	
	@GetMapping("/exportImages")
	public String exportImages() {
		log.debug("prom/exportImages");
		imageService.imagesScheduledTask();
		return "prom/ExportImagesInfo";  //ExportImagesInfo.html
	}

	@GetMapping("/refreshLogsOrders")
	public String refreshLogs(Model model) {
		String res = this.importOrders(model);
		return res;
	}
	@GetMapping("/refreshLogsExportPrice")
	public String refreshLogsExportPric(Model model) {
		String res = this.exportPriceInfo( model);
		log.debug("/refreshLogsExportPrice - "+res);
		return res;
	}

}
