package ua.org.gorbounov.sod.emag.services;

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
public class EmagTasks {
	@Value("${emag.enabled:false}")
	private Boolean emagEnabled;

	@Value("${prom.ua.1c.path}")
	String path_1c;
	@Value("${prom.ua.products.import.1c.base}")
	String import1cBase;
	@Value("${emag.orders.user1C}")
	private String orders1Cuser;

	@Value("${emag.upload.user1C}")
	private String upload1Cuser;

	@PostConstruct
	public void init() {
		log.info(toString());
	}

	@Override
	public String toString() {
		return "EmagTasks [emagEnabled=" + emagEnabled + ", orders1Cuser=" + orders1Cuser + ", upload1Cuser="
				+ upload1Cuser + "]";
	}

	@Async
	@Scheduled(cron = "${emag.orders.download.cron}")
	public void getOrdersSheduledTask() {
		if (emagEnabled) {
			log.debug("emag.getOrdersSheduledTask run successfully...");
			exec1cDownload();
		}
	}

	@Async
	@Scheduled(cron = "${emag.upload.cron}")
	public void uploadScheduledTask() {
		if (emagEnabled) {
			log.debug("emag.getOrdersSheduledTask run successfully...");
			exec1cUpload();
		}
	}

	/**
	 * 
	 */
	public void exec1cDownload() {
		if (emagEnabled) {
			log.debug("------- exec1cDownload start -----------");
//			Runtime r = Runtime.getRuntime();
			Process p = null;
			try {
				String command = "\"C:\\Program Files (x86)\\1Cv77\\BIN\\1cv7s.exe\" ENTERPRISE /DD:\\base\\cron /N"
						+ orders1Cuser;
				log.trace("command=" + command);

//				p = new ProcessBuilder().command(command).start();
				p = new ProcessBuilder().command(path_1c, "ENTERPRISE", "/D"+import1cBase,"/N"+orders1Cuser).start();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					log.debug("out:"+line);
				}

				if (p.waitFor(5, TimeUnit.MINUTES)) {
					// true - процесс нормально завершился
					log.debug("процесс завершился нормально");
				} else {
					// false - не успел завершиться
					log.debug("время вышло, процесс не завершился");
					p.destroy();
				}
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.debug("Код возврата - " + p.exitValue());
			log.info("Загрузка с e-mag.c.ua завершилась с кодом возврата - " + p.exitValue());

			log.debug("------- exec1cDownload complete -----------");
		}
	}

	/**
	 * 
	 */
	public void exec1cUpload() {
		if (emagEnabled) {
			String user1c = upload1Cuser;
			log.debug("------- exec1cUpload start -----------");
			Runtime r = Runtime.getRuntime();
			Process p = null;
			try {
				String command = "\"C:\\Program Files (x86)\\1Cv77\\BIN\\1cv7s.exe\" ENTERPRISE /DD:\\base\\cron /N"
						+ user1c;
				log.trace("command=" + command);
				p = r.exec(command);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					log.debug("out:"+line);
				}

				if (p.waitFor(15, TimeUnit.MINUTES)) {
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
			log.debug("Код возврата - " + p.exitValue());
			log.info("Выгрузка на e-mag.c.ua завершилась с кодом возврата - " + p.exitValue());

			log.debug("------- exec1cUpload complete -----------");
		}
	}

}
