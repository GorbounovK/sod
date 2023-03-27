/**
 * 
 */
package ua.org.gorbounov.sod.prom.models;

import org.springframework.stereotype.Component;

import lombok.ToString;

/**
 * @author gk
 *
 */

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
