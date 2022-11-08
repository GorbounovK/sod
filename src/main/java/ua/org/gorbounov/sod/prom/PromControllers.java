package ua.org.gorbounov.sod.prom;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.SodUtils;
import ua.org.gorbounov.sod.models.ApplicationProperties;
import ua.org.gorbounov.sod.models.ImageEntity;
import ua.org.gorbounov.sod.prom.models.ProductSearch;
import ua.org.gorbounov.sod.prom.models.PromExportPriceEntity;
import ua.org.gorbounov.sod.prom.models.PromExportPriceInfo;
import ua.org.gorbounov.sod.prom.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.models.PromProduct;
import ua.org.gorbounov.sod.prom.repositories.PromOrdersEntityRepozitories;
import ua.org.gorbounov.sod.prom.services.ExportPriceInfoService;
import ua.org.gorbounov.sod.prom.services.ExportService;
import ua.org.gorbounov.sod.prom.services.ImagesInfoService;
import ua.org.gorbounov.sod.prom.services.ImagesService;
import ua.org.gorbounov.sod.prom.services.ImportOrdersInfoService;
import ua.org.gorbounov.sod.prom.services.ImportProductFromProm;
import ua.org.gorbounov.sod.prom.services.OrdersService;

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
	@Autowired
	private ImagesInfoService imagesInfoService;
	@Autowired
	private ApplicationProperties prop;

	@Autowired
	private ImportProductFromProm importProductFromProm;

	@Autowired
	private ProductSearch productSearch;

	/**
	 * @param promImportOrdersInfo
	 * @param model
	 * @return
	 */
	@PostMapping("/save_cron_import_orders")
	public String save_cron_import_orders(@ModelAttribute PromImportOrdersInfo promImportOrdersInfo, Model model) {
		log.debug("prom/save_cron_import_orders");
		log.debug("cron_import_orders=" + promImportOrdersInfo.getCron());
		model.addAttribute("prop", prop);
		return "prom/ImportOrders";
	}

	@GetMapping("/importOrders")
	public String importOrders(Model model) {
		log.debug("prom/ImportOrders");
//		String promImportOrdersInfo=promImportOrdersInfo;
//		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo(1, 20, "id");
		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo();
		log.debug("ordersInfoEntity.size {}", ordersInfoEntity.size());
		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		model.addAttribute("ordersInfoEntity", ordersInfoEntity);
		model.addAttribute("prop", prop);
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
		model.addAttribute("prop", prop);
		return "prom/ImportOrders";

	}

	@GetMapping("/listProducts")
	public String listProducts(Model model, @RequestParam("page") Optional<Integer> pageCurrent,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = 0;
		int pageSize = size.orElse(10);
		if (pageCurrent.isPresent()) {
			currentPage = pageCurrent.get() - 1;
		}

		log.debug("prom/listProducts, page=" + pageCurrent + " size=" + size);
//		List<PromOrdersEntity> ordersInfoEntity = ordersInfoService.getAllImportOrdersInfo();
//		log.debug("ordersInfoEntity.size {}",ordersInfoEntity.size());
//		model.addAttribute("promImportOrdersInfo", promImportOrdersInfo);
		model.addAttribute("productSearch", productSearch);
		Page<PromProduct> productsPage = importProductFromProm.getAllProducts(PageRequest.of(currentPage, pageSize));
		List<PromProduct> listProducts = productsPage.getContent();
		int[] pages = SodUtils.paginatorView(productsPage.getTotalPages(), currentPage + 1);
		model.addAttribute("products", listProducts);
		model.addAttribute("currentPage", pageCurrent);
		model.addAttribute("body", pages);
		model.addAttribute("page", productsPage);
		model.addAttribute("prop", prop);

		log.debug("totalPages=" + productsPage.getTotalPages() + " currentPage=" + currentPage);
		// productsPage.getTotalPages()
		return "prom/ImportProducts";
	}

	// getProduct
	@GetMapping("/getProducts")
	public String getProducts(Model model) {
		log.debug("prom/getProducts");
		importProductFromProm.importProductFromXml();
//		String res = this.listProducts(model,1, 10);
		String res = "prom/ImportProducts";
		return res;
	}

	@PostMapping("/product")
	public String productSearchByBarcode(@ModelAttribute ProductSearch productSearch, Model model) {
		log.trace("search barcode = " + productSearch.getBarcode());
		int currentPage = 0;
		int pageSize = 10;
//	    if(pageCurrent.isPresent()) {
//	       	currentPage = pageCurrent.get()-1;
//	    }

		Page<PromProduct> productsPage = importProductFromProm.getProductsByBarcode(productSearch.getBarcode(),
				PageRequest.of(currentPage, pageSize));
		List<PromProduct> listProducts = productsPage.getContent();
		int[] pages = SodUtils.paginatorView(productsPage.getTotalPages(), currentPage + 1);
		model.addAttribute("products", listProducts);
		model.addAttribute("prop", prop);

		model.addAttribute("currentPage", currentPage);
		model.addAttribute("body", pages);
		model.addAttribute("page", productsPage);
		return "prom/ImportProducts";

	}

	// ProductInfo
	@GetMapping("/product/{id}")
	public String productInfo(@PathVariable("id") long id, Model model) {
		log.debug("prom/productInfo. id = "+id);
		PromProduct product = importProductFromProm.getProductById(id);
		log.debug("product = "+product.toString());
		model.addAttribute("product", product);
		model.addAttribute("prop", prop);
		String res = "prom/product";
		return res;
	}
	
	@GetMapping("/exportPriceInfo")
	public String exportPriceInfo(Model model) {
		log.debug("prom/ExportPriceInfo");
		List<PromExportPriceEntity> exportPriceEntity = exportPriceInfoService.getAllImportOrdersInfo();
		model.addAttribute("exportPriceEntity", exportPriceEntity);
		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		model.addAttribute("prop", prop);
		return "prom/ExportPriceInfo";

	}

	@GetMapping("/exportPrice")
	public String exportPrice(Model model) {
		log.debug("prom/ExportPrice");
		exportService.exportProductSheduledTask();
		model.addAttribute("promExportPriceInfo", promExportPriceInfo);
		model.addAttribute("prop", prop);
		return "prom/ExportPriceInfo";
	}

	@GetMapping("/exportImagesInfo")
	public String exportImagesInfo(Model model) {
		log.debug("prom/exportImagesInfo");
		List<ImageEntity> imageEntity = imagesInfoService.getAllImportOrdersInfo();
		log.info("imageEntity.size()={}", imageEntity.size());
		model.addAttribute("imageInfoEntity", imageEntity);
		model.addAttribute("prop", prop);
		return "prom/ExportImagesInfo";
	}

	@GetMapping("/exportImages")
	public String exportImages(Model model) {
		log.debug("prom/exportImages");
		imageService.imagesScheduledTask();
		model.addAttribute("prop", prop);
		return "prom/ExportImagesInfo"; // ExportImagesInfo.html
	}

	@GetMapping("/exportAllImages")
	public String exportAllImages(Model model) {
		log.debug("prom/exportAllImages");
		imageService.sendAllImagesToFtp();
		model.addAttribute("prop", prop);
		return "prom/ExportImagesInfo"; // ExportImagesInfo.html
	}

	@GetMapping("/refreshLogsImages")
	public String refreshLogsImages(Model model) {
		String res = this.exportImagesInfo(model);
		model.addAttribute("prop", prop);
		return res;
	}

	@GetMapping("/refreshLogsOrders")
	public String refreshLogs(Model model) {
		String res = this.importOrders(model);
		model.addAttribute("prop", prop);
		return res;
	}

	@GetMapping("/refreshLogsExportPrice")
	public String refreshLogsExportPric(Model model) {
		String res = this.exportPriceInfo(model);
		log.debug("/refreshLogsExportPrice - " + res);
		model.addAttribute("prop", prop);
		return res;
	}

//	@GetMapping("/importProductFromProm")
//	public String importProductFromProm(Model model) {
//		importProductFromProm.importProductFromXml(10);
//		log.debug("/importProductFromProm - ");
//		model.addAttribute("prop", prop);
//		return "index";
//	}
}
