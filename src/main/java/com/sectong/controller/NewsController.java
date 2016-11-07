package com.sectong.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sectong.domain.News;
import com.sectong.domain.NewsCreateForm;
import com.sectong.message.Message;
import com.sectong.service.NewsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1", name = "新闻API")
@Api(description = "新闻API")

public class NewsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);
	private Message message = new Message();

	private NewsService newsService;

	@Autowired
	public NewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@ResponseBody
	@PostMapping(value = "i/news/create")
	@ApiOperation(value = "创建新闻接口", notes = "创建新闻，接口提交json格式，字段参见form参数")
	public ResponseEntity<Message> createNews(@Valid @RequestBody NewsCreateForm form, BindingResult bindingResult) {
		try {
			News news = newsService.create(form);
			message.setMsg(1, "新闻创建成功", news);
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("create news error", e);
			message.setMsg(0, "创建新闻失败");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}
	}

	@GetMapping(value = "news/getNewsList")
	@ApiOperation(value = "获取新闻列表接口", notes = "获取新闻列表，接口startid默认0，下拉时根据最大值获取后续列表，可分页查询")
	public ResponseEntity<Message> getNewsList(@RequestParam(defaultValue = "0") Long startid, Pageable p) {
		Page<News> news = newsService.getNewsList(startid, p);
		message.setMsg(1, "获取新闻列表成功", news);
		return new ResponseEntity<Message>(message, HttpStatus.OK);
	}

}
