/**
 * 
 */
package ua.org.gorbounov.sod.rozetka.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.models.ApplicationProperties;
import ua.org.gorbounov.sod.rozetka.models.RozetkaExportPriceEntity;
import ua.org.gorbounov.sod.rozetka.services.RozetkaExportPriceInfoService;
import ua.org.gorbounov.sod.rozetka.services.RozetkaExportService;


/**
 * @author gk
 *
 */
@Controller
@Log4j2
@RequestMapping("/rozetka")
public class RozetkaControllers {
	@Autowired
	private ApplicationProperties prop;
	@Autowired
	private RozetkaExportPriceInfoService rozetkaPriceInfoService;
	@Autowired
	private RozetkaExportService exportService;


	
	@GetMapping("/exportPriceInfo")
	public String exportPriceInfo(Model model) {
		log.debug("rozetka/ExportPriceInfo");
		List<RozetkaExportPriceEntity> exportPriceEntity = rozetkaPriceInfoService.getAllImportOrdersInfo();
		model.addAttribute("exportPriceEntity",exportPriceEntity);
//		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		model.addAttribute("prop", prop);
	return "rozetka/ExportPriceInfo";
		
	}

	@GetMapping("/exportPrice")
	public String exportPrice(Model model) {
		log.debug("rozetka/ExportPrice");
		exportService.exportProductSheduledTask();
//		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		model.addAttribute("prop", prop);
		return "rozetka/ExportPriceInfo";
	}
	
	@GetMapping("/refreshLogsExportPrice")
	public String refreshLogsExportPric(Model model) {
		String res = this.exportPriceInfo( model);
		log.debug("/refreshLogsExportPrice - "+res);
		model.addAttribute("prop", prop);
		return res;
	}

}
