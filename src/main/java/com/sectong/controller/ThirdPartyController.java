package com.sectong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sectong.message.Message;
import com.sectong.service.SendSMSService;
import com.sectong.validator.ValidatorUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/api/v1", name = "第三方API")
@Api(description = "第三方API")
public class ThirdPartyController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyController.class);
	private Message message = new Message();

	private SendSMSService sendSMSService;

	@Autowired
	public ThirdPartyController(SendSMSService sendSMSService) {
		this.sendSMSService = sendSMSService;
	}

	/**
	 * 请求短信验证码
	 * 
	 * @param mobile
	 * @return
	 */
	@PostMapping("requestSMS")
	@ApiOperation(value = "请求短信验证码接口", notes = "请求短信验证码接口，POST请求，参数mobile必须为11位手机号码")
	public ResponseEntity<Message> requestSMS(@RequestParam String mobile) {

		if (mobile.isEmpty()) {
			LOGGER.info("手机号码输入为空");
			message.setMsg(0, "手机号码不能为空");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}
		if (!ValidatorUtil.isMobile(mobile)) {
			LOGGER.info("手机号码错误：{}", mobile);
			message.setMsg(0, "手机号码错误");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}

		String ret = sendSMSService.send(mobile);
		if (ret.equals("0")) {
			LOGGER.info("验证码发送成功：{}", mobile);
			message.setMsg(1, "验证码发送成功");
		} else {
			LOGGER.info("验证码发送失败：{}", mobile);
			message.setMsg(0, "验证码发送失败：" + ret);
		}

		LOGGER.info("Access ThirdPartyController.requestSMS");
		return new ResponseEntity<Message>(message, HttpStatus.OK);

	}

}
