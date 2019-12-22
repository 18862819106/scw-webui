package com.atguigu.scw.webui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//跳转到首页 ，首页中需要查询项目列表显示
		/*registry.addViewController("/").setViewName("pages/index");
		registry.addViewController("/index").setViewName("pages/index");
		registry.addViewController("/index.html").setViewName("pages/index");*/
		//跳转到登录页面
		registry.addViewController("/user/login.html").setViewName("pages/user/login");
		
	}
}
