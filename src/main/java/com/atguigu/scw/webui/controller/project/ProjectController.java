package com.atguigu.scw.webui.controller.project;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.feign.ProjectControllerFeignClient;
import com.atguigu.scw.webui.vo.ProjectDetailsRespVo;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/project")
@Slf4j
public class ProjectController {
	
	@Autowired
	ProjectControllerFeignClient projectControllerFeignClient;
	
	//1、查询指定id的项目详情的方法：项目的所有信息(project、projectimages、project发起人、return、分类标签)
	@GetMapping("/project.html")
	public String getProjectDetails(Integer id,HttpSession session) {
		//远程调用project模块 查询id对应的项目详情
		AppResponse<ProjectDetailsRespVo> appResponse = projectControllerFeignClient.detail(id);
		log.info("获取id为{}项目详情{}", id , appResponse);
		//将对象存到域中
		ProjectDetailsRespVo projectDetailsRespVo = appResponse.getData();
		//model.addAttribute("project", projectDetailsRespVo);
		
		session.setAttribute("project", projectDetailsRespVo);
		//转发到项目详情页获取显示
		return "pages/project/project";
	}
}
