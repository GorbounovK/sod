package ua.org.gorbounov.sod.prom.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.Objects;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.Utils;
import ua.org.gorbounov.sod.prom.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.repositories.PromOrdersEntityRepozitories;

@Log4j2
//@Component
@Service
public class OrdersService {
	@Value("${prom.ua.orders.url}")
	private String promUaOrdersUrl;
	@Value("${prom.ua.enabled:false}")
	private Boolean promUaEnabled;

	@Autowired
	private PromImportOrdersInfo promImportOrdersInfo;
	@Autowired
	private PromOrdersEntityRepozitories repository;

	@Value("${prom.ua.1c.path}")
	String path_1c;
	@Value("${prom.ua.products.import.1c.base}")
	String import1cBase;
	@Value("${prom.ua.products.import.1c.user}")
	String import1cUser;
	@Value("${prom.ua.products.import.script}")
	String importScript;

	@Value("${prom.ua.path}")
	private String promUaPath;

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
		promImportOrdersInfo.setCron(promOrdersDownloadCron);
	}

	@Async("threadPoolTaskExecutor")
	public void getOrdersSheduledTask() {
		long startTime = System.currentTimeMillis();
		promImportOrdersEntity = new PromOrdersEntity();
		promImportOrdersEntity.setLastExecution(new Date());

		if (promUaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			try {
				getXmlFromUrl();

			} catch (IOException e) {
				log.error("IOException: " + e.getLocalizedMessage());
				log.error("stack : " + ExceptionUtils.getStackTrace(e));
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				log.error("stack : " + ExceptionUtils.getStackTrace(e));
			}
			exec1cCreateOrders();
		}
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		log.debug("endTime {} - startTime {}", endTime, startTime);
		String durationString = Utils.millisToShortDHMS(executionTime);
		log.info("Total execution time: " + durationString);
//		String executionTimeString = String.valueOf(executionTime);
		log.debug("------- getOrdersSheduledTask complete -----------");
		promImportOrdersEntity.setExecutionTime(durationString);
		repository.save(promImportOrdersEntity);

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

		Document doc = dBuilder.newDocument();
		try {
//			print_https_cert(connection);
//			print_content(connection);

			InputStream is = getInputStreamFromHttpsUrl(promUaOrdersUrl);
			doc = dBuilder.parse(is);
//			log.trace(doc.toString());
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
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			aTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			Source src = new DOMSource(doc);
			Result dest = new StreamResult(new File(promUaPath + "/orders.xml"));
			aTransformer.transform(src, dest);
			log.info("Файл " + promUaPath + "/orders.xml" + " записан.");

//			Writer stringWriter = new StringWriter();
//			StreamResult streamResult = new StreamResult(stringWriter);
//			aTransformer.transform(src, streamResult);        
//			String result = stringWriter.toString();	
//			log.trace(result);

			promImportOrdersEntity.setResultExecution("Новых заказов " + countNewOrders + ", " + "принятых "
					+ countAcceptedOrders + ", оплаченных " + countPaidOrders + ". ");
			log.info("Новых заказов " + countNewOrders + ", " + "принятых " + countAcceptedOrders + ", оплаченных "
					+ countPaidOrders + ". ");
			//

		} finally {
//			httpget.releaseConnection();
		}
		return doc;
	}

	/**
	 * @param xmlDocumentUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private InputStream getInputStreamFromHttpsUrl(String urlOverHttps) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

//		ResponseEntity<String> res = new RestTemplate(requestFactory).exchange(urlOverHttps, HttpMethod.GET, null,
//				String.class);
		ResponseEntity<Resource> res = new RestTemplate(requestFactory).exchange(urlOverHttps, HttpMethod.GET, null,
				Resource.class);

		log.trace("StatusCode {}", res.getStatusCodeValue());
//		log.trace("getBody {}", res.getBody());
//		InputStream is = IOUtils.toInputStream(res.getBody(), "utf-8");
//		InputStream is = new ByteArrayInputStream(res.getBody().getBytes(StandardCharsets.UTF_8));
		InputStream is = res.getBody().getInputStream();
//		String result = IOUtils.toString(is, "utf-8");
//		log.trace("InputStream {}", result);
		return is;
	}

	/**
	 * @param con
	 */
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
					log.debug("Cert toString : " + cert.toString());
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
