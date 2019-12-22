package com.atguigu.scw.webui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.feign.ProjectControllerFeignClient;
import com.atguigu.scw.webui.vo.HotProjectRespVo;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DispatcherController {
	
	@Autowired
	ProjectControllerFeignClient projectControllerFeignClient;
	//处理跳转到首页和 错误页面的请求
	@GetMapping(value= {"/" , "/index.html" , "/index"})
	public String toIndexPage(Model model) {
		//远程调用项目模块 查询所有的项目列表显示到页面中
		// 显示数据：  项目头图、项目name、项目的简介、项目的id
		AppResponse<List<HotProjectRespVo>> response = projectControllerFeignClient.index();
		log.info("查询热门项目列表：{}", response);
		if("00000".equals(response.getCode())) {
			model.addAttribute("hotProjects", response.getData());
			return "pages/index";
		}else {
			model.addAttribute("errorMsg", response.getMessage());
			return "pages/error/500";
		}
		
	}
	// <mvc:view-controller  url="xxx"   viewname="pages/index" ></mvc:view-controller>  可以简写没有业务的controller的方法
}
