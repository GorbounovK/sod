package ua.org.gorbounov.sod.prom.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ToString
@Entity
public class PromOrdersEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastExecution;
	
	private String resultExecution = "";
	private String executionTime = "";

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

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}
	
	
}
