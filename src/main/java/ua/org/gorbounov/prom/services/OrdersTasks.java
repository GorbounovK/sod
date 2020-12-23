package ua.org.gorbounov.prom.services;

import java.io.IOException;
import java.util.Optional;
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
public class OrdersTasks {
	@Value("${prom.ua.orders.url}")
	private String promUaOrdersUrl;

	@Async
	@Scheduled(cron = "${prom.ua.orders.download.cron}")
	public void getOrdersSheduledTask() {
		log.debug("getOrdersSheduledTask run successfully...");
		exec1cCreateOrders();
	}

	@Async
	@Scheduled(cron = "${prom.ua.products.upload.cron}")
	public void exportProductSheduledTask() {
		log.debug("getOrdersSheduledTask run successfully...");
		exec1cExportProducts();
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

				/*
				 * SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss"); Date
				 * now = new Date();
				 * 
				 * String spot2r_path = mProps.getProperty("spot2r_path"); String filePath =
				 * spot2r_path + File.separator + "down_zip" + File.separator +
				 * formatter.format(now) + ".zip"; // "./file.zip"; log.trace(filePath);
				 * 
				 * if (!fileNameString.equals("empty.zip") & !fileNameString.isEmpty()) {
				 * log.debug("fileNameString=" + fileNameString); InputStream is =
				 * entity.getContent(); FileOutputStream fos = new FileOutputStream(new
				 * File(filePath));
				 * 
				 * int inByte; while ((inByte = is.read()) != -1) { fos.write(inByte); }
				 * 
				 * is.close(); fos.close(); log.info("Файл " + filePath + " записан."); result =
				 * filePath;
				 * 
				 * } else { log.debug("Возвращен empty.zip - загрузка пропущена."); result = "";
				 * }
				 */
			}
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			log.error(e);
		}

		return result;
	}
	
	/**
	 * 
	 */
	public void exec1cCreateOrders() {
		log.debug("------- exec1cCreateOrders start -----------");
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String user1c="prom_get_orders";
		try {
			String command = "\"C:\\Program Files (x86)\\1Cv77\\BIN\\1cv7s.exe\" ENTERPRISE /DD:\\base\\cron /N"+user1c;
			log.trace("command=" + command);
			p = r.exec(command);
			p.waitFor();
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		log.debug("Код возврата - " + p.exitValue());

		log.debug("------- exec1cCreateOrders complete -----------");
	}

	/**
	 * 
	 */
	public void exec1cExportProducts() {
		log.debug("------- exec1cExportProducts start -----------");
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String user1c="prom_export";
		try {
			String command = "\"C:\\Program Files (x86)\\1Cv77\\BIN\\1cv7s.exe\" ENTERPRISE /DD:\\base\\cron /N"+user1c;
			log.trace("command=" + command);
			p = r.exec(command);
			p.waitFor();
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		log.debug("Код возврата - " + p.exitValue());

		log.debug("------- exec1cExportProducts complete -----------");
	}

}
