/**
 * 
 */
package ua.org.gorbounov.sod.models;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * @author gk
 *
 */
@Log4j2
@Component
public class PromImportOrdersInfo {
	private String cron;
	
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
