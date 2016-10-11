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
import com.sectong.repository.UserRepository;
import com.sectong.service.UserService;

@Controller
public class AdminController {

	private UserRepository userRepository;
	private NewsRepository newsRepository;
	private UserService userService;

	@Autowired
	public AdminController(NewsRepository newsRepository, UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.newsRepository = newsRepository;
		this.userService = userService;
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/news/del")
	public String delNews(Model model, @RequestParam Long id) {
		newsRepository.delete(id);
		return "redirect:/admin/news";

	}
}
