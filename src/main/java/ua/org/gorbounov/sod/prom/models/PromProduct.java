package ua.org.gorbounov.sod.prom.models;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ToString
@Entity
public class PromProduct {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String promId;
	private String url;
	private BigDecimal price;
	private String currencyId;
	private String categoryId;
	private String picture;
	private String pickup;
	private String delivery;
	private String name;
	private String name_ua;
	private String vendor;
	private String vendorCode;
	private String country_of_origin;
	@Lob
	private String description;
	@Lob
	private String description_ua;
	private String sales_notes;
	//относительно корневой папки картинок
	private String localPathImg;
	//
	private String barcode;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getPickup() {
		return pickup;
	}
	public void setPickup(String pickup) {
		this.pickup = pickup;
	}
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName_ua() {
		return name_ua;
	}
	public void setName_ua(String name_ua) {
		this.name_ua = name_ua;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getCountry_of_origin() {
		return country_of_origin;
	}
	public void setCountry_of_origin(String country_of_origin) {
		this.country_of_origin = country_of_origin;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription_ua() {
		return description_ua;
	}
	public void setDescription_ua(String description_ua) {
		this.description_ua = description_ua;
	}
	public String getSales_notes() {
		return sales_notes;
	}
	public void setSales_notes(String sales_notes) {
		this.sales_notes = sales_notes;
	}
	public String getLocalPathImg() {
		return localPathImg;
	}
	public void setLocalPathImg(String localPathImg) {
		this.localPathImg = localPathImg;
	}
	public String getPromId() {
		return promId;
	}
	public void setPromId(String promId) {
		this.promId = promId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	//param;
}
