package ua.org.gorbounov.sod.prom.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Objects;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.httpclient.HttpsURL;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.client.HttpClient;
import org.apache.http.NameValuePair;

import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.models.PromOrdersEntity;
import ua.org.gorbounov.sod.repositories.PromOrdersEntityRepozitories;

@Log4j2
@Component
public class OrdersService {
	@Value("${prom.ua.orders.url}")
	private String promUaOrdersUrl;
	@Value("${prom.ua.enabled:false}")
	private Boolean promUaEnabled;

	@Autowired
	private PromImportOrdersInfo promImportOrdersInfo;
	@Autowired
	PromOrdersEntityRepozitories repository;

	@Value("${prom.ua.1c.path}")
	String path_1c;
	@Value("${prom.ua.products.import.1c.base}")
	String import1cBase;
	@Value("${prom.ua.products.import.1c.user}")
	String import1cUser;
	@Value("${prom.ua.products.import.script}")
	String importScript;

	@Value("${prom.ua.orders.download.cron}")
	String promOrdersDownloadCron;

	private int countProcess = 0;
	private int countNewOrders = 0;
	PromOrdersEntity promImportOrdersEntity;
	private int countAcceptedOrders;
	private int countPaidOrders;

	@PostConstruct
	public void init() {
		log.info(toString());
		log.debug("promOrdersDownloadCron=" + promOrdersDownloadCron);
		// TODO
		promImportOrdersInfo.setCron(promOrdersDownloadCron);
	}

	@Async
	@Scheduled(cron = "${prom.ua.orders.download.cron}")
	public void getOrdersSheduledTask() {
		long startTime = System.currentTimeMillis();
		promImportOrdersEntity = new PromOrdersEntity();
		promImportOrdersEntity.setLastExecution(new Date());

		if (promUaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			try {
				getXmlFromUrl();
			}catch (CertPathValidatorException e){
				log.error("Reaso "+e.getReason());
				log.error("stack : "+e.getStackTrace());
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				log.error("stack : "+e.getStackTrace());
			}
			exec1cCreateOrders();
		}
		long endTime = System.currentTimeMillis();
		log.debug("endTime {} - startTime {}", endTime, startTime);
		log.info("Total execution time: " + (endTime - startTime) + "ms");
		log.debug("------- getOrdersSheduledTask complete -----------");
		repository.save(promImportOrdersEntity);

	}

	/**
	*
	*/
	private void dowloadOrders() {
		log.debug("------- start dowloadOrders -------");
		try {
			String result = sendPOST();
			log.trace("result =" + result);
			String outPutFolder = "${prom.ua.orders.download.cron}"; // mProps.getProperty("spot2r_path") +
																		// File.separator + "orders" + File.separator;
			log.trace("outPutFolder =" + outPutFolder);

		} catch (Exception e) {
			log.error(e);
		}
		log.debug("------- dowloadOrders Complete -----------");

	}

	/*
	 * 
	 * 
	 */
	private String sendPOST() throws IOException {

		String result = "";
		String url = promUaOrdersUrl;// "http://" + mProps.getProperty("spot2_server") + "/getfiles";
		log.trace("url=" + url);

		HttpPost post = new HttpPost(url);

		try {
			HttpClient httpClient = HttpClients.createDefault();
			HttpResponse response = httpClient.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();
			log.trace("responseCode=" + responseCode);

			if (responseCode >= 200 & responseCode < 300) {
				HttpEntity entity = response.getEntity();
//				Header headers = entity.getContentType();
//				log.trace("ContentType="+headers);
//
//				long contentLength = entity.getContentLength();
//				log.trace("contentLength=" + contentLength);

				log.trace("<HEADERS>");
				Header[] allHeaders = response.getAllHeaders();
				for (Header header : allHeaders) {
					log.trace(header.getName() + "=" + header.getValue());
				}
				log.trace("</HEADERS>");

//				String name = response.getFirstHeader("Content-Disposition").getValue();
				Optional<String> fileName = Arrays.stream(response.getFirstHeader("Content-Disposition").getElements())
						.map(element -> element.getParameterByName("filename")).filter(Objects::nonNull)
						.map(NameValuePair::getValue).findFirst();
				String fileNameString;
				if (fileName.isPresent()) {
					fileNameString = fileName.get();
				} else {
					fileNameString = "";
				}
				log.trace(fileNameString);

			}
		} catch (Exception e) {
			log.error(e);
		}

		return result;
	}

	/**
	 * @param xmlDocumentUrl
	 * @return
	 * @throws Exception
	 */
	private Document getDocumentFromUrl(String xmlDocumentUrl) throws Exception {
		log.debug("url xml.url = " + xmlDocumentUrl);
		countNewOrders = 0;
		countAcceptedOrders = 0;
		countPaidOrders = 0;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		URL url = new URL(xmlDocumentUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setHostnameVerifier(new NoopHostnameVerifier());
//		print_https_cert(connection);
//		print_content(connection);

		InputStream is = connection.getInputStream();

//		Document doc = dBuilder.parse(new URL(xmlDocumentUrl).openStream());
		Document doc = dBuilder.parse(is);
		Element eElement = (Element) doc.getElementsByTagName("orders").item(0);
		log.debug("XML date = " + eElement.getAttribute("date"));
		NodeList orders = doc.getElementsByTagName("order");
		int ordersCount = orders.getLength();
		log.debug("ordersCount = {}", ordersCount);
		for (int i = 0; i < orders.getLength(); i++) {
			Node order = orders.item(i);
			Element eOrder = (Element) order;
			String id = eOrder.getAttribute("id");
			String state = eOrder.getAttribute("state");
			if (state.equals("new")) {
				log.debug("Новый заказ {}", id);
				countNewOrders++;
			} else if (state.equals("accepted")) {
				countAcceptedOrders++;
			} else if (state.equals("paid")) {
				countPaidOrders++;
			}
		}
		promImportOrdersEntity.setResultExecution("Новых заказов " + countNewOrders + ", " + "принятых "
				+ countAcceptedOrders + ", оплаченных " + countPaidOrders + ". ");
		log.info("Новых заказов " + countNewOrders + ", " + "принятых "
				+ countAcceptedOrders + ", оплаченных " + countPaidOrders + ". ");
		//
//	    XMLParserLiaison xpathSupport = new XMLParserLiaisonDefault();
//	    XPathProcessor xpathParser = new XPathProcessorImpl(xpathSupport);
//	    PrefixResolver prefixResolver =new PrefixResolverDefault(source.getDocumentElement());
		is.close();
		connection.disconnect();
		log.debug("connection {}",connection);
		return doc;
	}

	private void print_content(HttpsURLConnection con) {
		if (con != null) {

			try {

				log.debug("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null) {
					log.debug(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void print_https_cert(HttpsURLConnection con) {
		if (con != null) {

			try {

				log.debug("Response Code : " + con.getResponseCode());
				log.debug("Cipher Suite : " + con.getCipherSuite());
				log.debug("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					log.debug("Cert Type : " + cert.getType());
					log.debug("Cert Hash Code : " + cert.hashCode());
					log.debug("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					log.debug("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					log.debug("\n");
					log.debug("Cert toString : "+cert.toString());
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void getXmlFromUrl() throws Exception {
//
//		try {
		Document doc = getDocumentFromUrl(promUaOrdersUrl);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * 
	 */
	public void exec1cCreateOrders() {
		log.debug("------- exec1cCreateOrders start -----------");

		Process p = null;
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			log.debug(System.getProperty("os.name").toLowerCase());
			try {
				log.trace("importScript=" + importScript);
				p = new ProcessBuilder().command(importScript).start();
				countProcess++;

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "cp866"));
				BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream(), "cp866"));
				String line = "";
				while ((line = reader.readLine()) != null) {
					log.debug("out:" + line);
				}
				while ((line = error.readLine()) != null) {
					log.error("error:" + line);
				}

				if (p.waitFor(5, TimeUnit.MINUTES)) {
					// true - процесс нормально завершился
					log.trace("процесс завершился нормально");
					countProcess--;
				} else {
					// false - не успел завершиться
					log.error("время вышло, процесс не завершился. Убиваем");
					p.destroy();
					countProcess--;
				}

				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("1C завершилась с кодом возврата - " + p.exitValue());
			promImportOrdersEntity.setResultExecution(
					promImportOrdersEntity.getResultExecution() + "1C завершилась с кодом возврата - " + p.exitValue());
		} else {
			log.debug("не windows");

			promImportOrdersEntity.setResultExecution(promImportOrdersEntity.getResultExecution() + "Не windows");
		}

		log.debug("установлено время запуска = " + promImportOrdersEntity.getLastExecution());
		log.debug("countProcess = {}", countProcess);
		log.debug("------- exec1cCreateOrders complete -----------");

	}

	@Override
	public String toString() {
		return "OrdersTasks " + System.lineSeparator() + "prom.ua.orders.download.cron = " + promOrdersDownloadCron
				+ System.lineSeparator() + "prom.ua.orders.url =" + promUaOrdersUrl + System.lineSeparator()
				+ "prom.ua.enabled =" + promUaEnabled + System.lineSeparator() + "prom.ua.products.import.script = "
				+ importScript + System.lineSeparator() + "----------------";
	}

//	public String getResultAction() {
//		return "all right";
//	}
	

}
