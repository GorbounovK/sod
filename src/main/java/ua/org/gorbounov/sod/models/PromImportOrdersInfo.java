package ua.org.gorbounov.sod.models;

import java.util.Calendar;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

@Component
public class PromImportOrdersInfo {
	private String cron;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastExecution;
	
	private String resultExecution;

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Calendar getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Calendar lastExecution) {
		this.lastExecution = lastExecution;
	}

	public String getResultExecution() {
		return resultExecution;
	}

	public void setResultExecution(String resultExecution) {
		this.resultExecution = resultExecution;
	}
	
	
}
