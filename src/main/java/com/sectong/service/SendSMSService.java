package com.sectong.service;

import com.sectong.domain.Sms;

public interface SendSMSService {

	String send(String mobile);

	Sms findByUsernameAndVcode(String mobile, String vcode);

	void saveSmsConfig(String username, String password);

	Boolean checkSmsAccountStatus(String account, String password);

}
