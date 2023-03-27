/**
 * 
 */
package ua.org.gorbounov.sod.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.ToString;

/**
 * @author gk
 *
 */
@ToString
@Entity
public class ImageEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpload;
	
	//@Column(length = 1000)
	@Lob
	private String resultExecution = "";	
	private String executionTime = "";
	
	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public String getResultExecution() {
		return resultExecution;
	}

	public void setResultExecution(String resultExecution) {
		this.resultExecution = resultExecution;
	}

	public Date getLastUpload() {
		return lastUpload;
	}

	public void setLastUpload(Date date) {
		this.lastUpload = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



}
