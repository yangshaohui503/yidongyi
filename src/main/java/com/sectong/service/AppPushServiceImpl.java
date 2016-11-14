package com.sectong.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sectong.domain.ThirdParty;
import com.sectong.repository.ThirdpartyRepository;
import com.sectong.thirdparty.push.AppPush;

@Service
public class AppPushServiceImpl implements AppPushService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppPushServiceImpl.class);

	private ThirdpartyRepository thirdpartyRepository;

	@Autowired
	public AppPushServiceImpl(ThirdpartyRepository thirdpartyRepository) {
		this.thirdpartyRepository = thirdpartyRepository;
	}

	@Override
	public void savePushConfig(String appID, String appKey, String masterSecret) {
		ThirdParty thirdParty = new ThirdParty();
		thirdParty.createConfig("pushAppID", appID);
		thirdpartyRepository.save(thirdParty);
		thirdParty.createConfig("pushAppKey", appKey);
		thirdpartyRepository.save(thirdParty);
		thirdParty.createConfig("pushMasterSecret", masterSecret);
		thirdpartyRepository.save(thirdParty);

	}

	/**
	 * 发送测试推送信息
	 * 
	 * @param title
	 * @param text
	 * @param openUrl
	 * @return
	 * @throws IOException
	 */
	@Override
	public Boolean sendPushMsg(String title, String text, String openUrl) throws IOException {

		Boolean ret = false;

		String pushAppID = null, pushAppKey = null, pushMasterSecret = null;
		try {
			pushAppID = thirdpartyRepository.findOne("pushAppID").getValue();
			pushAppKey = thirdpartyRepository.findOne("pushAppKey").getValue();
			pushMasterSecret = thirdpartyRepository.findOne("pushMasterSecret").getValue();
			ret = true;
			LOGGER.info("appID: {}, appKey: {}, masterSecret: {}", pushAppID, pushAppKey, pushMasterSecret);
			AppPush.SendAppPush(pushAppID, pushAppKey, pushMasterSecret, title, text, openUrl);
			ret = true;
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

}
