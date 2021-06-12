package ua.org.gorbounov.prom.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import java.util.Objects;

import java.util.Arrays;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.NameValuePair;

import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class OrdersService {
	@Value("${prom.ua.orders.url}")
	private String promUaOrdersUrl;
	@Value("${prom.ua.enabled:false}")
	private Boolean promUaEnabled;

	@Value("${prom.ua.1c.path}")
	String path_1c;
	@Value("${prom.ua.products.import.1c.base}")
	String import1cBase;
	@Value("${prom.ua.products.import.1c.user}")
	String import1cUser;
	@Value("${prom.ua.products.import.script}")
	String importScript;


	@PostConstruct
	public void init() {
		log.info(toString());
	}

	@Async
	@Scheduled(cron = "${prom.ua.orders.download.cron}")
	public void getOrdersSheduledTask() {
		if (promUaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			exec1cCreateOrders();
		}
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
			// TODO: handle exception
			log.error(e);
//                 e.printStackTrace();
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

		// add request parameters or form parameters
//		List<NameValuePair> urlParameters = new ArrayList<>();
//		urlParameters.add(new BasicNameValuePair("__login", mProps.getProperty("spot2_user")));
//		urlParameters.add(new BasicNameValuePair("__password", mProps.getProperty("spot2_password")));
//
//		post.setEntity(new UrlEncodedFormEntity(urlParameters));

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
	 * 
	 */
	public void exec1cCreateOrders() {
		log.debug("------- exec1cCreateOrders start -----------");
		Process p = null;
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			log.debug(System.getProperty("os.name").toLowerCase());
			try {
//				String command = path_1c+ " ENTERPRISE"+ " /D"+import1cBase+" /N"+import1cUser;
//				log.trace("command=" + command);
//				p = new ProcessBuilder(command).start();
//				p = new ProcessBuilder().command(path_1c, "ENTERPRISE", "/D"+import1cBase,"/N"+import1cUser).start();
				log.trace("importScript=" + importScript);
				p = new ProcessBuilder().command(importScript).start();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					log.debug("out:"+line);
				}
				while ((line = error.readLine()) != null) {
					log.error("error:"+line);
				}
				
				if (p.waitFor(5, TimeUnit.MINUTES)) {
					// true - процесс нормально завершился
					log.trace("процесс завершился нормально");
				} else {
					// false - не успел завершиться
					log.error("время вышло, процесс не завершился. Убиваем");
					p.destroy();
				}

				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("Загрузка с prom.ua завершилась с кодом возврата - " + p.exitValue());
		} else {
			log.debug("не windows");
		}

		log.debug("------- exec1cCreateOrders complete -----------");

	}

	@Override
	public String toString() {
		return "OrdersTasks [promUaOrdersUrl=" + promUaOrdersUrl + ", promUaEnabled=" + promUaEnabled + ", path_1c="
				+ path_1c + ",  import1cUser=" + import1cUser + "]";
	}

}
