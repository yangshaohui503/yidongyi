package com.sectong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sectong.repository.NewsRepository;
import com.sectong.repository.UserRepository;

@Controller
public class AdminController {

	private UserRepository userRepository;
	private NewsRepository newsRepository;

	@Autowired
	public AdminController(NewsRepository newsRepository, UserRepository userRepository) {
		this.userRepository = userRepository;
		this.newsRepository = newsRepository;
	}

	@RequestMapping("/admin/")
	public String adminIndex(Model model) {
		model.addAttribute("dashboard", true);
		model.addAttribute("userscount", userRepository.count());
		model.addAttribute("newscount", newsRepository.count());
		return "admin/index";
	}

	@RequestMapping("/admin/user")
	public String adminUser(Model model) {
		model.addAttribute("user", true);
		return "admin/user";
	}

	@RequestMapping("/admin/news")
	public String adminNews(Model model) {
		model.addAttribute("news", true);
		return "admin/news";
	}
}
