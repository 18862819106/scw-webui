package com.atguigu.scw.webui.feign;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.webui.vo.UserAddressVo;
@Service
public class UserControllerFeignClientExceptionHandler implements UserControllerFeignClient{

	@Override
	public AppResponse<Object> doLogin(String loginacct, String userpswd) {
		
		return AppResponse.fail("登录失败", "远程调用超时，连接失败");
	}

	@Override
	public AppResponse<List<UserAddressVo>> address(String accessToken) {
		return AppResponse.fail(null, "远程调用超时，连接失败");
	}

}
