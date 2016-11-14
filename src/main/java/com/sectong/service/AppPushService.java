package com.sectong.service;

import java.io.IOException;

public interface AppPushService {

	void savePushConfig(String appID, String appKey, String masterSecret);

	Boolean sendPushMsg(String title, String text, String openUrl) throws IOException;

}
