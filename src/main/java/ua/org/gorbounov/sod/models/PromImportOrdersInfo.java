package ua.org.gorbounov.sod.models;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class PromImportOrdersInfo {
	private String cron;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastExecution;
	
	private String resultExecution = "";

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Date getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	public String getResultExecution() {
		return resultExecution;
	}

	public void setResultExecution(String resultExecution) {
		this.resultExecution = resultExecution;
	}
	
	
}
