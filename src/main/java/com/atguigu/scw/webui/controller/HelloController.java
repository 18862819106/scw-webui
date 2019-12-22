package com.atguigu.scw.webui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.atguigu.scw.webui.bean.User;

@Controller
public class HelloController {
	
	//webui项目用来和浏览器直接交互，一般转发到页面响应给浏览器
	@GetMapping("/test")
	public String test(Model model , HttpSession session) {
		//request
		model.addAttribute("reqKey", "reqVal");
		//session
		session.setAttribute("sessionKey", "sessionVal");
		//application
		ServletContext application = session.getServletContext();
		application.setAttribute("appKey", "appVal");
		
		//向session域中存入 对象的List集合，页面中遍历数据显示到table中
		List<User> users = new ArrayList<User>();
		users.add(new User(9527, "laoji", "123456", "laoji@atguigu.com"));
		users.add(new User(9528, "zhangchi", "123456", "zhangchi@atguigu.com"));
		users.add(new User(9529, "anni", "123456", "anni@atguigu.com"));
		users.add(new User(9530, "zhanglifang", "123456", "zhanglifang@atguigu.com"));
		users.add(new User(9531, "xiaochai", "123456", "xiaochai@atguigu.com"));
		users.add(new User(9532, "zhenguo", "123456", "zhenguo@atguigu.com"));
		
		session.setAttribute("users", users);
		model.addAttribute("user", new User(9527, "laoji", "123456", "laoji@atguigu.com"));
		return "testThymeleaf";//返回视图名  会交给thymeleaf的视图解析器解析，自动拼接前缀后缀
	}
	
}
