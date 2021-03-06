package ua.org.gorbounov.sod.rozetka.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.Utils;
import ua.org.gorbounov.sod.rozetka.models.RozetkaExportPriceEntity;
import ua.org.gorbounov.sod.rozetka.models.RozetkaExportPriceInfo;
import ua.org.gorbounov.sod.rozetka.repositories.RozetkaExportPriceEnityRepozitories;

@Log4j2
@ToString
@Service
public class RozetkaExportService {

	@Value("${rozetka.enabled:false}")
	private Boolean rozetkaEnabled;

	@Value("${rozetka.ua.products.upload.script:}")
	String uploadScript;
	@Value("${rozetka.ua.products.upload.cron}")
	String rozetkaProductUploadCron;
	
	@Autowired
	private RozetkaExportPriceInfo rozetkaExportPriceInfo;
	@Autowired
	private RozetkaExportPriceEnityRepozitories repository;
	RozetkaExportPriceEntity rozetkaExportPriceEntity;

	@Async("threadPoolTaskExecutor")
	public void exportProductSheduledTask() {
		log.debug("rozetkaEnabled = " + rozetkaEnabled);
		long startTime = System.currentTimeMillis();
		rozetkaExportPriceEntity = new RozetkaExportPriceEntity();
		rozetkaExportPriceEntity.setLastExecution(new Date());
		if (rozetkaEnabled) {
			log.debug("getOrdersSheduledTask run successfully...");
			exec1cExportProducts();
		}
		long endTime = System.currentTimeMillis();
		log.debug("endTime {} - startTime {}", endTime, startTime);
		long executionTime = endTime - startTime;
		String durationString = Utils.millisToShortDHMS(executionTime);
		log.info("Total execution time: " + durationString);
		log.debug("------- exportProductSheduledTask complete -----------");
		rozetkaExportPriceEntity.setExecutionTime(durationString);
		repository.save(rozetkaExportPriceEntity);
		log.trace("rozetkaExportPriceEntity={}",rozetkaExportPriceEntity.toString());
		log.debug("------- exportProductSheduledTask complete -----------");
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
					// true - ?????????????? ?????????????????? ????????????????????
					log.trace("?????????????? ???????????????????? ??????????????????");
				} else {
					// false - ???? ?????????? ??????????????????????
					log.trace("?????????? ??????????, ?????????????? ???? ????????????????????. ??????????????");
					p.destroy();
				}
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("???????????????? ???? rozetka.ua ?????????????????????? ?? ?????????? ???????????????? - " + p.exitValue());
			rozetkaExportPriceEntity.setResultExecution(
					rozetkaExportPriceEntity.getResultExecution() + "1C ?????????????????????? ?? ?????????? ???????????????? - " + p.exitValue());
		} else {
			log.debug("???? windows");
			rozetkaExportPriceEntity.setResultExecution(rozetkaExportPriceEntity.getResultExecution() + "???? windows");
		}
		log.debug("------- exec1cExportProducts complete -----------");
	}
//	@Override
//	public String toString() {
//		return "prom.ExportService " + System.lineSeparator()+
//				"promUaEnabled=" + promUaEnabled + System.lineSeparator()+
//				"prom.ua.products.upload.cron=" + promProductUploadCron + System.lineSeparator()+
//				"prom.ua.products.upload.scrip=" + uploadScript + System.lineSeparator()+
//				"-------------------";
//	}
	@PostConstruct
	public void init() {
		log.info(toString());
//		log.debug("promExportPriceCron=" + promProductUploadCron);
//		promExportPriceInfo.setCron(promProductUploadCron);
	}

}
