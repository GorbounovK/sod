package ua.org.gorbounov.emag.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class EmagTasks {
	@Value("${emag.orders.user1C}")
	private String orders1Cuser;

	@Value("${emag.upload.user1C}")
	private String upload1Cuser;

	@Async
	@Scheduled(cron="${emag.orders.download.cron}")
	public void getOrdersSheduledTask() {
		log.debug("emag.getOrdersSheduledTask run successfully...");
		exec1cDownload();
	}

	@Async
	@Scheduled(cron="${emag.upload.cron}")
	public void uploadScheduledTask() {
		log.debug("emag.getOrdersSheduledTask run successfully...");
		exec1cUpload();
	}
	/**
	 * 
	 */
	public void exec1cDownload() {
		String user1c=orders1Cuser;
		log.debug("------- exec1cDownload start -----------");
		Runtime r = Runtime.getRuntime();
		Process p = null;
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

		log.debug("------- exec1cDownload complete -----------");
	}

	/**
	 * 
	 */
	public void exec1cUpload() {
		String user1c=upload1Cuser;
		log.debug("------- exec1cUpload start -----------");
		Runtime r = Runtime.getRuntime();
		Process p = null;
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

		log.debug("------- exec1cUpload complete -----------");
	}

}
