package ua.org.gorbounov.prom.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ExportService {

	@Value("${prom.ua.enabled:false}")
	private Boolean promUaEnabled;

	@Value("${prom.ua.1c.path}")
	String command;
	@Value("${prom.ua.products.upload.1c.user}")
	String upload1cUser;

	@Async
	@Scheduled(cron = "${prom.ua.products.upload.cron}")
	public void exportProductSheduledTask() {
		log.debug("promUaEnabled = " + promUaEnabled);
		if (promUaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			exec1cExportProducts();
		}
	}

	/**
	 * 
	 */
	public void exec1cExportProducts() {
		log.debug("------- exec1cExportProducts start -----------");
		Runtime r = Runtime.getRuntime();
		Process p = null;
//		String user1c = "prom_export";
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			log.debug(System.getProperty("os.name").toLowerCase());
			try {
				log.trace("command=" + command + upload1cUser);
				p = r.exec(command + upload1cUser);
				p.waitFor(120, TimeUnit.MINUTES);
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("Выгрука на prom.ua завершилась с кодом возврата - " + p.exitValue());
		} else {
			log.debug("не windows");
		}
		log.debug("------- exec1cExportProducts complete -----------");
	}

}
