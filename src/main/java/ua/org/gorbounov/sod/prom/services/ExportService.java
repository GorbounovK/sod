package ua.org.gorbounov.sod.prom.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

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

	@Value("${prom.ua.1c.path:}")
	String path_1c;
	@Value("${prom.ua.products.import.1c.base:}")
	String import1cBase;
	@Value("${prom.ua.products.upload.1c.user:}")
	String upload1cUser;
	@Value("${prom.ua.products.upload.script:}")
	String uploadScript;
	@Value("${prom.ua.products.upload.cron}")
	String promProductUploadCron;

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
		Process p = null;
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			log.debug(System.getProperty("os.name").toLowerCase());
			try {
//				log.trace("command=" + path_1c+ " ENTERPRISE"+ " /D"+import1cBase+" /N"+upload1cUser);
//				p = new ProcessBuilder().command(command).start();
//				p = new ProcessBuilder().command(path_1c, "ENTERPRISE", "/D"+import1cBase,"/N"+upload1cUser).start();
				log.trace("uploadScript" +uploadScript);
				p = new ProcessBuilder().command(uploadScript).start();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "cp866"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					log.debug("out:"+line);
				}
				if (p.waitFor(120, TimeUnit.MINUTES)) {
					// true - процесс нормально завершился
					log.trace("процесс завершился нормально");
				} else {
					// false - не успел завершиться
					log.trace("время вышло, процесс не завершился. Убиваем");
					p.destroy();
				}
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("Выгрузка на prom.ua завершилась с кодом возврата - " + p.exitValue());
		} else {
			log.debug("не windows");
		}
		log.debug("------- exec1cExportProducts complete -----------");
	}
	@Override
	public String toString() {
		return "prom.ExportService " + System.lineSeparator()+
				"promUaEnabled=" + promUaEnabled + System.lineSeparator()+
				"prom.ua.products.upload.cron=" + promProductUploadCron + System.lineSeparator()+
				"prom.ua.products.upload.scrip=" + uploadScript + System.lineSeparator()+
				"-------------------";
	}
	@PostConstruct
	public void init() {
		log.info(toString());
	}

}
