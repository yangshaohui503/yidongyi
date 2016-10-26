package com.sectong.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 短信pojo
 * 
 * @author jiekechoo
 *
 */
@Entity
public class Sms {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String mobile;
	private String vcode;
	private Date expiredDatetime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public Date getExpiredDatetime() {
		return expiredDatetime;
	}

	public void setExpiredDatetime(Date expiredDatetime) {
		this.expiredDatetime = expiredDatetime;
	}

	public void createSms(String mobile, String vcode, Date expiredDatetime) {
		this.mobile = mobile;
		this.vcode = vcode;
		this.expiredDatetime = expiredDatetime;
	}

}
