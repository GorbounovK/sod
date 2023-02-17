package ua.org.gorbounov.sod.prom.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.SodUtils;
import ua.org.gorbounov.sod.prom.models.PromExportPriceEntity;
import ua.org.gorbounov.sod.prom.models.PromExportPriceInfo;
import ua.org.gorbounov.sod.prom.models.PromImportOrdersInfo;
import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.repositories.PromExportPriceEnityRepozitories;
import ua.org.gorbounov.sod.prom.repositories.PromOrdersEntityRepozitories;

@Log4j2
@Service
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
	
	@Autowired
	private PromExportPriceInfo promExportPriceInfo;
	@Autowired
	private PromExportPriceEnityRepozitories repository;
	PromExportPriceEntity promExportPriceEntity;

	@Async("threadPoolTaskExecutor")
	public void exportProductSheduledTask() {
		log.debug("promUaEnabled = " + promUaEnabled);
		long startTime = System.currentTimeMillis();
		promExportPriceEntity = new PromExportPriceEntity();
		promExportPriceEntity.setLastExecution(new Date());
		if (promUaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			exec1cExportProducts();
		}
		long endTime = System.currentTimeMillis();
		log.debug("endTime {} - startTime {}", endTime, startTime);
		long executionTime = endTime - startTime;
		String durationString;
			durationString = SodUtils.millisToShortDHMS(executionTime);
		log.info("Total execution time: " + durationString);
		log.debug("------- exportProductSheduledTask complete -----------");
		promExportPriceEntity.setExecutionTime(durationString);
		repository.save(promExportPriceEntity);

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
			promExportPriceEntity.setResultExecution(
					promExportPriceEntity.getResultExecution() + "1C завершилась с кодом возврата - " + p.exitValue());
		} else {
			log.debug("не windows");
			promExportPriceEntity.setResultExecution(promExportPriceEntity.getResultExecution() + "Не windows");
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
		log.debug("promExportPriceCron=" + promProductUploadCron);
		promExportPriceInfo.setCron(promProductUploadCron);
	}

}
