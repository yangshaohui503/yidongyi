package com.sectong.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sectong.domain.ResetPasswordForm;
import com.sectong.domain.User;
import com.sectong.domain.UserCreateForm;
import com.sectong.message.Message;
import com.sectong.repository.UserRepository;
import com.sectong.service.SendSMSService;
import com.sectong.service.UserService;
import com.sectong.validator.UserCreateFormValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 处理用户类接口
 * 
 * @author jiekechoo
 *
 */
@RestController
@PropertySource("classpath:message.properties")
@RequestMapping(value = "/api/v1/", name = "用户API")
@Api(description = "用户API")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	private UserService userService;
	private UserCreateFormValidator userCreateFormValidator;
	private UserRepository userRepository;
	private SendSMSService sendSMSService;

	private Message message = new Message();

	@Autowired
	public UserController(UserService userService, UserCreateFormValidator userCreateFormValidator,
			UserRepository userRepository, SendSMSService sendSMSService) {
		this.userService = userService;
		this.userCreateFormValidator = userCreateFormValidator;
		this.userRepository = userRepository;
		this.sendSMSService = sendSMSService;
	}

	// 装载用户认证Manager
	@Autowired
	protected AuthenticationManager authenticationManager;

	/**
	 * 创建用户验证表单
	 * 
	 * @param binder
	 */
	@InitBinder("userCreateForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(userCreateFormValidator);
	}

	/**
	 * APP登录用接口
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "i/userLogin", method = RequestMethod.POST)
	@ApiOperation(value = "用户登录接口", notes = "用户登录，接口POST请求，采用HttpBasic认证方式")
	public ResponseEntity<Message> userLogin(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		if (user == null) {
			message.setMsg(0, "用户登录失败");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		} else {
			message.setMsg(1, "用户登录成功", user);
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}

	}

	/**
	 * 创建用户接口
	 * 
	 * @param form
	 * @param bindingResult
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ApiOperation(value = "创建用户接口", notes = "创建用户，接口POST请求，提交用户创建json")
	public ResponseEntity<Message> handleUserCreateForm(@Valid @RequestBody UserCreateForm form,
			BindingResult bindingResult) {

		if (sendSMSService.findByUsernameAndVcode(form.getUsername(), form.getVcode()) == null) {
			LOGGER.info("验证码错误，或找不到");
			message.setMsg(0, "验证码错误或过期");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}

		LOGGER.debug("Processing user create form={}, bindingResult={}", form, bindingResult);
		if (bindingResult.hasErrors()) {
			// failed validation
			message.setMsg(0, "用户创建失败");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}
		try {
			userService.create(form);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("Exception occurred when trying to save the user, assuming duplicate username", e);
			bindingResult.reject("username.exists", "username already exists");
			message.setMsg(0, "创建用户失败：用户名已存在");
			return new ResponseEntity<Message>(message, HttpStatus.OK);

		}
		// ok, redirect
		message.setMsg(1, "用户创建成功");
		return new ResponseEntity<Message>(message, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "i/user/resetPassword", method = RequestMethod.POST)
	@ApiOperation(value = "重置密码接口", notes = "重置密码，接口POST请求，参数为ResetPasswordForm类")
	public ResponseEntity<Message> resetPassword(@Valid @RequestBody ResetPasswordForm form,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
		if (userService.getUserByUsername(form.getMobile()) == null) {
			LOGGER.info("用户不存在");
			message.setMsg(0, "用户不存在");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}
		if (sendSMSService.findByUsernameAndVcode(form.getMobile(), form.getVcode()) == null) {
			LOGGER.info("验证码错误，或找不到");
			message.setMsg(0, "验证码错误或过期");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}

		try {
			User user = userService.resetPassword(form);
			authenticateUserAndSetSession(form.getMobile(), form.getPassword(), request);
			message.setMsg(1, "用户修改、重置密码成功", user);
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("数据提交错误，请遵守验证规则", e);
			message.setMsg(2, "数据提交错误，请遵守验证规则");
			return new ResponseEntity<Message>(message, HttpStatus.OK);
		}

	}

	/**
	 * 使用 ResponseBody作为结果 200
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "i/user/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取用户信息接口", notes = "获取用户信息，接口GET请求，{id}参数为用户id号")
	public ResponseEntity<Message> findByUserId(@PathVariable long id) {
		User user = userRepository.findOne(id);
		HttpStatus status = user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

		if (user == null) {
			message.setMsg(0, "未找到用户");
		} else {
			message.setMsg(1, "用户信息", user);
		}
		return new ResponseEntity<Message>(message, status);
	}

	/**
	 * 上传用户头像
	 * 
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "i/uploadImage", method = RequestMethod.POST)
	@ApiOperation(value = "上传用户头像接口", notes = "上传用户头像，接口采用MultipartFile上传图片文件")
	public ResponseEntity<?> uploadImage(@RequestParam MultipartFile file, HttpServletRequest request) {
		message.setMsg(1, "用户上传头像成功", userService.uploadImage(file, request));
		return new ResponseEntity<Message>(message, HttpStatus.OK);

	}

	/**
	 * 获取用户列表
	 * 
	 * @param current
	 * @param rowCount
	 * @param searchPhrase
	 * @return
	 */
	@RequestMapping(value = "getUserList", method = RequestMethod.POST)
	public Object getUserList(@RequestParam(required = false) int current, @RequestParam(required = false) int rowCount,
			@RequestParam(required = false) String searchPhrase) {
		return userService.getUserList(current, rowCount, searchPhrase);

	}

	/**
	 * 自动登录，获得session和x-auth-token
	 * 
	 * @param username
	 * @param password
	 * @param request
	 */
	private void authenticateUserAndSetSession(String username, String password, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		// generate session if one doesn't exist
		request.getSession();

		token.setDetails(new WebAuthenticationDetails(request));
		Authentication authenticatedUser = authenticationManager.authenticate(token);
		LOGGER.info("Auto login with {}", authenticatedUser.getPrincipal());

		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
	}

}
