package com.atguigu.scw.webui.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.vo.HotProjectRespVo;
import com.atguigu.scw.webui.vo.ProjectDetailsRespVo;

@FeignClient(value="SCW-PROJECT")
public interface ProjectControllerFeignClient {
	@GetMapping("/project/info/getHotProjects")
	public AppResponse<List<HotProjectRespVo>> index();
	
	
	@GetMapping("/project/info/detail")
	public AppResponse<ProjectDetailsRespVo> detail(@RequestParam("id")Integer id);
	
	
}
