package ua.org.gorbounov.sod.prom.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.SodUtils;
import ua.org.gorbounov.sod.prom.models.PromProduct;
import ua.org.gorbounov.sod.prom.repositories.PromProductRepozitories;

@Log4j2
@Service
public class ImportProductFromProm {
	@Value("${prom.ua.images.local.path}")
	private String localPathImages;

	@Value("${prom.ua.products.count.max}")
	int MAX_COUNT_XML = 10;

	@Value("${prom.importProduct.xml.url}")
	private String xmlUrl;

	@Value("${prom.importProduct.xml.filePath}")
	private String xmlFilePath;

	@Value("${prom.ua.products.import.images.local.path}")
	private String imgLocalPathRoot;

	@Value("${prom.ua.products.import.images.enableDownloadImage:false}")
	private boolean isEnableDownloadImage;
	
	@Autowired
	private PromProductRepozitories repository;

	public Page<PromProduct> getAllProducts(Pageable pageable) {
		log.debug("getAllProducts");
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

		Pageable first20 = PageRequest.of(0, 10);
		Pageable last10 = PageRequest.of(0, 10, Sort.by("id").descending());
//		Pageable last10 = PageRequest.of(0, 10, Sort.by("id"));

		Page<PromProduct> pagedResult = repository.findAll(pageable);
		log.trace("pagedResult.size {}", pagedResult.getSize());
//		if (pagedResult.hasContent()) {
//			return pagedResult.getContent();
//		} else {
//			return (List<PromProduct>) new ArrayList<PromProduct>();
//		}
		return pagedResult;
	}
	
	public PromProduct getProductById(long id) {
		PromProduct product = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));;
		return product;		
				
	}

	/*
	 * @param i
	 */
	public void importProductFromXml() {
		try {
//			MAX_COUNT_XML =  i;
//			Document doc=getDocumentFromUrl(xmlUrl);
			log.info("xmlFilePath = " + xmlFilePath);
			Document doc = getDocumentFromFile(xmlFilePath);
			Map<String, PromProduct> mapProductFromXML = getProductsMapFromXml(doc);
//			Map<String, ReadableProduct> mapProductFromApi = getProductsMapFromApi();
			log.info("mapProductFromXML.size = " + mapProductFromXML.size());
			for (String sku : mapProductFromXML.keySet()) {
				PromProduct productXml = mapProductFromXML.get(sku);
				// есть ли товар с таким sku в мапеАПИ
//				ReadableProduct productApi = mapProductFromApi.get(sku);
				if (productXml != null) {
					// товары отличаются - обновляем
//					log.trace("товар sku=" + sku + " изменился - обновляем");
//					this.updateProductToApi(productXml, productApi.getId());
//					log.trace("товар sku=" + sku + " изменился - удаляем");
//					this.deleteProductToApi(productApi.getId());
				} else {
					log.trace("товар sku=" + sku + " не найден - пропускаем");
//					this.deleteProductToApi(productApi.getId());
				}
//				List list = repository.findByBarcodeContaining(productXml.getBarcode());
//				log.trace(productXml.getBarcode()+ " найдено " + list.size());
				repository.save(productXml);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.trace("error " + e.getLocalizedMessage());
		}
	}

	/**
	 * @param doc
	 * @return Map<sku, PromProduct>
	 */
	private Map<String, PromProduct> getProductsMapFromXml(Document doc) {
		long startTime = System.currentTimeMillis();
		log.info("getProductsMapFromXml - start");
		Map<String, PromProduct> mapProductFromXML = new HashMap<>();
		int countNode = 0;// rows

		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("offer");
		log.info("Прочитано " + nList.getLength() + " товаров");

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);

			// получаем товар из xml node
			PromProduct product = nodeToProduct(nNode);
			mapProductFromXML.put(product.getPromId(), product);

			// postProductToApiServer(product);

			log.info("Line " + i + " ********************");
			countNode++;// rows

			if (countNode == MAX_COUNT_XML) {
				log.info("Reached MAX_COUNT_XML [" + MAX_COUNT_XML + "]");
				break;
			}
		}
		log.debug("mapProductFromXML.size = " + mapProductFromXML.size());

		long endTime = System.currentTimeMillis();
		long timeExecution = endTime - startTime;

	//	SodUtils su = new SodUtils();
		log.debug("Total execution getProductsMapFromXml time: " + SodUtils.getDurationToString(startTime, endTime));
		log.debug("Total execution getProductsMapFromXml time: " + SodUtils.millisToShortDHMS(timeExecution));

		return mapProductFromXML;
	}



	/**
	 * @param nNode
	 * @return PersistableProduct
	 */
	private PromProduct nodeToProduct(Node nNode) {
		long start = System.currentTimeMillis();
		String urlImage;

		PromProduct product = new PromProduct();
		try {
//			product.setCreationDate(new Date().toString());
//
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				log.trace("---------------------------");
				log.trace("id = " + eElement.getAttribute("id"));
				log.trace("available = " + eElement.getAttribute("available"));
				product.setPromId(eElement.getAttribute("id"));
//				// код товара, sku,
//				// <vendorCode>400014</vendorCode>
				product.setVendorCode(eElement.getElementsByTagName("vendorCode").item(0).getTextContent());
//				// код категории
//				// <categoryId>74</categoryId>
				product.setCategoryId(eElement.getElementsByTagName("categoryId").item(0).getTextContent());
				product.setUrl(eElement.getElementsByTagName("url").item(0).getTextContent());
				product.setCurrencyId(eElement.getElementsByTagName("currencyId").item(0).getTextContent());
				urlImage = getStringElementByTagName(eElement, "picture");
				product.setPicture(urlImage);
				// categoryId
				product.setCategoryId(eElement.getElementsByTagName("categoryId").item(0).getTextContent());
				// pickup
				product.setPickup(eElement.getElementsByTagName("pickup").item(0).getTextContent());
				// delivery
				product.setDelivery(eElement.getElementsByTagName("delivery").item(0).getTextContent());
				// name
				product.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
				// name_ua
				product.setName_ua(eElement.getElementsByTagName("name_ua").item(0).getTextContent());
				// vendor
				product.setVendor(getStringElementByTagName(eElement, "vendor"));
				// country_of_origin
				product.setCountry_of_origin(getStringElementByTagName(eElement, "country_of_origin"));
				// description
				product.setDescription(getStringElementByTagName(eElement, "description"));
				// description_ua
				product.setDescription_ua(getStringElementByTagName(eElement, "description_ua"));
				// sales_notes
				product.setSales_notes(getStringElementByTagName(eElement, "sales_notes"));
				// param

				product.setPrice(new BigDecimal(eElement.getElementsByTagName("price").item(0).getTextContent()));
				log.trace("price = " + product.getPrice());
//
				String imageLocalFilePath = getLocalImagePath(urlImage);
				if(isEnableDownloadImage) {
					String fn = saveImageLocal(urlImage, imageLocalFilePath);
				}
				product.setLocalPathImg(imageLocalFilePath);

				String bc = "";
				NodeList nl = eElement.getElementsByTagName("param");
				log.trace("nl.getLength()=" + nl.getLength());
				// перебираем параметры
				if (nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {

						Element el = (Element) nl.item(i);
						String attName = el.getAttribute("name");
						log.trace("attName=" + attName);
						if (attName.equals("Штрихкод (barcode)")) {
							bc = el.getTextContent();
							log.trace("bc = " + bc);
						} else {
							log.trace("el.getAttribute = " + el.getAttribute("name"));
						}
					}
					log.trace("barcode = " + bc);
				}
				product.setBarcode(bc);
//				// <picture>http://gorbounov.cf/images/400/img400014_0.jpg</picture>
//				List<PersistableImage> images = this.getImages(eElement);
//				product.setImages(images);
//
//			// <vendor>La Rive</vendor>
//				String manufacturer = eElement.getElementsByTagName("vendor").item(0).getTextContent();// brand -
//																										// manufacturer
//																										// ...
//				ProductSpecification specs = new ProductSpecification();
//				log.trace("manufacturer = " + manufacturer); // code manufacturer
//				manufacturerImportService.importManufacturer(manufacturer);
//				specs.setManufacturer(manufacturer);
//				specs.setModel("50мл");
//				product.setProductSpecifications(specs);
//
//				// идентификатор справочной системы
//				product.setRefSku(eElement.getElementsByTagName("barcode").item(0).getTextContent());
//
//				product.setAvailable(true);// force availability
//				product.setProductVirtual(false);// force tangible good
//				product.setQuantityOrderMinimum(1);// force to 1 minimum when ordering
//				product.setProductShipeable(true);// all items are shipeable
//				product.setQuantity(1);
//				log.info("product: code=" + product.getId() + ", name="
//						+ eElement.getElementsByTagName("name").item(0).getTextContent());
//
			}
		} catch (Exception e) {
			log.error("Ошибка чтения товара, " + e + e.getLocalizedMessage());
		}
		long end = System.currentTimeMillis();
		log.debug("Время обработки товара (sku:" + product.getVendorCode() + ") " + (end - start) + "ms");
		return product;
	}

	/**
	 * @param urlImage
	 * @return
	 * @throws Exception
	 */
	private String getLocalImagePath(String urlImage) throws Exception {
		// TODO Auto-generated method stub
		URL url = new URL(urlImage);
		String fileName = FilenameUtils.getName(url.getPath());
		log.trace("fileName = " + fileName);
		log.trace("fullfileName = " + imgLocalPathRoot + "/" + fileName);
		return fileName;
	}

	/**
	 * Сохраняем изображение локально
	 * 
	 * @param urlImg
	 * @return путь относительно корня локальной папки
	 */
	private String saveImageLocal(String urlImg, String fileName) {
		String path = "";
		// This will get input data from the server
		InputStream inputStream = null;

		// This will read the data from the server;
		OutputStream outputStream = null;

		try {
			// This will open a socket from client to server
			URL url = new URL(urlImg);

			// This user agent is for if the server wants real humans to visit
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

			// This socket type will allow to set user_agent
			URLConnection con = url.openConnection();

			// Setting the user agent
			con.setRequestProperty("User-Agent", USER_AGENT);

			// Getting content Length
			int contentLength = con.getContentLength();
			// System.out.println("File contentLength = " + contentLength + " bytes");

			// Requesting input data from server
			inputStream = con.getInputStream();

			path = imgLocalPathRoot + "/" + fileName;
			// Open local file writer
			outputStream = new FileOutputStream(path);

			// Limiting byte written to file per loop
			byte[] buffer = new byte[2048];

			// Increments file size
			int length;
			int downloaded = 0;

			// Looping until server finishes
			while ((length = inputStream.read(buffer)) != -1) {
				// Writing data
				outputStream.write(buffer, 0, length);
				downloaded += length;
				// System.out.println("Downlad Status: " + (downloaded * 100) / (contentLength *
				// 1.0) + "%");

			}
			outputStream.close();
			inputStream.close();
		} catch (Exception ex) {
			// Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
			log.error(ex + ex.getLocalizedMessage());
		}

		// closing used resources
		// The computer will not be able to use the image
		// This is a must
		return fileName;
	}

	/**
	 * @param el
	 * @param tagName
	 * @return
	 */
	private String getStringElementByTagName(Element el, String tagName) {
		String res = "";
		NodeList nl = el.getElementsByTagName(tagName);
		if (nl.getLength() > 0) {
			res = nl.item(0).getTextContent();
		}
		return res;
	}

	/**
	 * @param xmlDocumentUrl
	 * @return
	 * @throws Exception
	 */
	private Document getDocumentFromUrl(String xmlDocumentUrl) throws Exception {
		log.debug("url xml.file = " + xmlDocumentUrl);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new URL(xmlDocumentUrl).openStream());
		Element eElement = (Element) doc.getElementsByTagName("yml_catalog").item(0);
		log.debug("XML date = " + eElement.getAttribute("date"));
		return doc;
	}

	private Document getDocumentFromFile(String fileName) throws Exception {
		log.debug("xml.file = " + fileName);
		File fXmlFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		return doc;
	}

	/**
	 * @param filePath
	 * @return
	 * @throws IOException Проверяем существование каталога для хранения
	 *                     изображений, если не существует - создаем
	 */
	boolean checkLocalParentDirectoryExists(String filePath) throws IOException {
		File dest = new File(filePath);
		// Проверяем, есть ли каталог
		if (!dest.getParentFile().exists()) {
			// Если файл не существует, заново создайте новый файл, чтобы предотвратить
			// ненормальное возникновение
			dest.getParentFile().mkdirs();
		}

		return true;
	}

	@Override
	public String toString() {
		return "ImportProductFromProm " + System.lineSeparator() + "prom.ua.images.local.path =" + localPathImages
				+ System.lineSeparator() + "----------------";
	}

	@PostConstruct
	public void init() {
		log.info(toString());
	}

	public Page<PromProduct> getProductsByBarcode(String barcode,Pageable pageable) {
		log.debug("getProductsByBarcode");
//		Pageable last10 = PageRequest.of(0, 10, Sort.by("id").descending());
//		Pageable last10 = PageRequest.of(0, 10, Sort.by("id"));

		Page<PromProduct> pagedResult = repository.findByBarcodeContaining(barcode, pageable);
		log.trace("pagedResult.size {}", pagedResult.getSize());
//		if (pagedResult.hasContent()) {
			return pagedResult;
//		} else {
//			return (Page<PromProduct>) new Page<PromProduct>();
//		}
	}
}
