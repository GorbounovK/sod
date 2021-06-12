/**
 * 
 */
package ua.org.gorbounov.sod.models;

import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author gk
 *
 */
@Entity
public class Image {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String localUrl;
	
	private String remoteUrl;
	
	private String filename;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastUpload;
	
	private long localSize;
	private long remoteSize;

	public Calendar getLastUpload() {
		return lastUpload;
	}

	public void setLastUpload(Calendar lastUpload) {
		this.lastUpload = lastUpload;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}



	public long getLocalSize() {
		return localSize;
	}

	public void setLocalSize(long localSize) {
		this.localSize = localSize;
	}

	public long getRemoteSize() {
		return remoteSize;
	}

	public void setRemoteSize(long remoteSize) {
		this.remoteSize = remoteSize;
	}

}
