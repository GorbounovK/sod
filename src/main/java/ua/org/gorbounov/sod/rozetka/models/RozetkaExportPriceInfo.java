package ua.org.gorbounov.sod.rozetka.models;

import org.springframework.stereotype.Component;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * @author gk
 *
 */
@Log4j2
@ToString
@Component
public class RozetkaExportPriceInfo {
	private String cron;
	
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
