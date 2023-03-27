package ua.org.gorbounov.sod.rozetka.models;

import org.springframework.stereotype.Component;

import lombok.ToString;

/**
 * @author gk
 *
 */

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
