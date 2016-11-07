package com.sectong.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sectong.domain.News;
import com.sectong.domain.User;
import com.sectong.repository.NewsRepository;
import com.sectong.repository.ThirdpartyRepository;
import com.sectong.repository.UserRepository;
import com.sectong.service.SendSMSService;
import com.sectong.service.UserService;

/**
 * 后台管理控制器
 * 
 * @author jiekechoo
 *
 */
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

	private UserRepository userRepository;
	private NewsRepository newsRepository;
	private UserService userService;
	private SendSMSService sendSMSService;
	private ThirdpartyRepository thirdpartyRepository;

	@Autowired
	public AdminController(NewsRepository newsRepository, UserRepository userRepository, UserService userService,
			SendSMSService sendSMSService, ThirdpartyRepository thirdpartyRepository) {
		this.userRepository = userRepository;
		this.newsRepository = newsRepository;
		this.userService = userService;
		this.sendSMSService = sendSMSService;
		this.thirdpartyRepository = thirdpartyRepository;
	}

	/**
	 * 管理主界面
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/")
	public String adminIndex(Model model) {
		model.addAttribute("dashboard", true);
		model.addAttribute("userscount", userRepository.count());
		model.addAttribute("newscount", newsRepository.count());
		return "admin/index";
	}

	/**
	 * 用户管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/user")
	public String adminUser(Model model) {
		model.addAttribute("user", true);
		return "admin/user";
	}

	/**
	 * 新闻管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/news")
	public String adminNews(Model model) {
		model.addAttribute("news", true);
		Iterable<News> newslist = newsRepository.findAll();
		model.addAttribute("newslist", newslist);
		return "admin/news";
	}

	/**
	 * 新闻增加表单
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/news/add")
	public String newsAdd(Model model) {
		model.addAttribute("newsAdd", new News());
		return "admin/newsAdd";
	}

	/**
	 * 新闻修改表单
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/news/edit")
	public String newsEdit(Model model, @RequestParam Long id) {
		model.addAttribute("newsEdit", newsRepository.findOne(id));
		return "admin/newsEdit";
	}

	/**
	 * 新闻修改提交操作
	 * 
	 * @param news
	 * @return
	 */
	@PostMapping("/admin/news/edit")
	public String newsSubmit(@ModelAttribute News news) {
		news.setDatetime(new Date());
		User user = userService.getCurrentUser();
		news.setUser(user);
		newsRepository.save(news);
		return "redirect:/admin/news";
	}

	/**
	 * 新闻删除操作
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/news/del")
	public String delNews(Model model, @RequestParam Long id) {
		newsRepository.delete(id);
		return "redirect:/admin/news";
	}

	/**
	 * 后台配置管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/configuration")
	public String configuration(Model model) {
		return "admin/configuration";
	}

	/**
	 * 第三方配置管理
	 * 
	 * @param model
	 * @return
	 */

	@GetMapping("/admin/thirdparty")
	public String thirdparty(Model model) {
		model.addAttribute("thirdparty", true);
		String smsUsername = null, smsPassword = null;
		try {
			smsUsername = thirdpartyRepository.findOne("smsUsername").getValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			smsPassword = thirdpartyRepository.findOne("smsPassword").getValue();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (!sendSMSService.checkSmsAccountStatus(smsUsername, smsPassword)) {
			model.addAttribute("error", true);// 状态不正常
		}
		if (smsUsername == null && smsPassword == null) {
			model.addAttribute("init", true);// 第一次初始化
			model.addAttribute("error", false);
		}
		model.addAttribute("smsUsername", smsUsername);
		model.addAttribute("smsPassword", smsPassword);
		return "admin/thirdparty";
	}

	/**
	 * 提交短信平台账号信息
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("/admin/thirdpartysms")
	public String thirdpartysms(@RequestParam String username, @RequestParam String password) {
		sendSMSService.saveSmsConfig(username, password);
		if (sendSMSService.checkSmsAccountStatus(username, password)) {
			return "redirect:/admin/thirdparty?ok";// 账号正常
		} else {
			return "redirect:/admin/thirdparty?error";// 状态不正常
		}

	}
}
