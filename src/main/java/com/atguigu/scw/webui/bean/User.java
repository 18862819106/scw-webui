package com.atguigu.scw.webui.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor //无参构造器
@AllArgsConstructor // 所有参数的构造器 //如果注解可以用代表 依赖引入成功， 如果注解用了 没有生成对应的方法，证明sts没有安装lombok插件
@Data // get set方法
@ToString // toString方法
@Slf4j  // 在当前类中引入slf4j的 log对象  用来打印日志
public class User implements Serializable{
	private Integer id;
	private String username;
	private String password;
	private String email;


}
