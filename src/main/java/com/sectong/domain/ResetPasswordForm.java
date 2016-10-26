package com.sectong.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 修改、重置密码POJO表单
 * 
 * @author jiekechoo
 *
 */
public class ResetPasswordForm {

	// 手机号码
	@NotEmpty
	@Size(min = 11, max = 11)
	private String mobile;

	// 重置密码
	@NotEmpty
	@Size(min = 6, max = 32)
	private String password;

	// 手机短信验证码
	@NotEmpty
	@Size(min = 6, max = 6)
	private String vcode;

	public String getMobile() {
		return mobile;
	}

	public String getPassword() {
		return password;
	}

	public String getVcode() {
		return vcode;
	}

}
