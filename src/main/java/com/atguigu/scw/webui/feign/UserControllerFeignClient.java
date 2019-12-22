package com.atguigu.scw.webui.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.vo.UserAddressVo;
@FeignClient(value="SCW-USER" ,fallback=UserControllerFeignClientExceptionHandler.class)
public interface UserControllerFeignClient {
	/**
	 * 远程调用时，feignclient中的方法如果有参数
	 * 		简单类型的参数：
	 * 				必须使用 @RequestParam("name")绑定参数  ，如果不绑定报错
	 * 						IllegalStateException: Method has too many Body parameters
	 * 		复杂类型的参数：
	 * 				对象集合，必须使用@RequestBody 绑定参数，指定参数以JSON的方式传递
	 * 	
	 * 	
	 * @param loginacct
	 * @param userpswd
	 * @return
	 */
	
	@PostMapping("/user/doLogin")
	public AppResponse<Object> doLogin(@RequestParam("loginacct")String loginacct , @RequestParam("userpswd")String userpswd);

	@GetMapping("/user/address")
	public AppResponse<List<UserAddressVo>> address(@RequestParam("accessToken")String accessToken);
}
