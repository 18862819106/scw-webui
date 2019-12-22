package com.atguigu.scw.webui.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HotProjectRespVo {
	//项目头图、项目name、项目的简介、项目的id
	private Integer id;
	private String name;
	private String remark;
	private String imgurl;
	
}
