package com.atguigu.scw.webui.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.feign.UserControllerFeignClient;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserControllerFeignClient userControllerFeign;
	//1、处理用户登录的请求
	@PostMapping("/doLogin")
	public String doLogin(HttpSession session,String loginacct , String userpswd , Model model) {
		
		//远程调用scw-user项目 处理登录的业务
		//步骤：1、根据远程调用的Controller创建接口(FeignClient)  2、自动装配 FeignClient  ，使用它实现远程调用
		AppResponse<Object> response = userControllerFeign.doLogin(loginacct, userpswd);
		//登录成功，重定向到首页响应给用户
		if("00000".equals(response.getCode())){
			//将登录成功的信息存到session域中
			session.setAttribute("user", response.getData());//response的data属性值  使用Object泛型接受，webui项目会自动将data转为Map
			
			//如果是从订单支付页面跳转到支付页面的，登录成功后希望回到登录前的页面
			String ref = (String) session.getAttribute("ref");
			if(ref!=null) {
				//从订单操作跳转过来登录的
				session.removeAttribute("ref");
				session.removeAttribute("errorMsg");
				return "redirect:"+ref;
			}else {
				//正常登录
				return "redirect:/index";
			}
		}else {
			//登录失败，设置错误消息到域中，转发到登录页面提示
			String errorMsg = response.getMessage();
			model.addAttribute("errorMsg", errorMsg);
			return "pages/user/login";
			
		}
		
		
	}
	
	
}
