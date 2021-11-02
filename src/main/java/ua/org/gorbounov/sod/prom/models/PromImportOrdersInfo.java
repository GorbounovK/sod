/**
 * 
 */
package ua.org.gorbounov.sod.prom.models;

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
public class PromImportOrdersInfo {
	private String cron;
	
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
