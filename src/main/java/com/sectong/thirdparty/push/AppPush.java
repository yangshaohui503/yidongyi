package com.sectong.thirdparty.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;

public class AppPush {

	private static String url = "http://sdk.open.api.igexin.com/apiex.htm";// 推送服务器地址

	public static void SendAppPush(String appId, String appKey, String masterSecret, String title, String text,
			String openUrl) throws IOException {

		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();

		// 定义"点击链接打开通知模板"，并设置标题、内容、链接
		LinkTemplate template = new LinkTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setTitle(title);
		template.setText(text);
		template.setUrl(openUrl);

		List<String> appIds = new ArrayList<String>();
		appIds.add(appId);

		// 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
		AppMessage message = new AppMessage();
		message.setData(template);
		message.setAppIdList(appIds);
		message.setOffline(true);
		message.setOfflineExpireTime(1000 * 600);

		IPushResult ret = push.pushMessageToApp(message);
		System.out.println(ret.getResponse().toString());
	}
}
