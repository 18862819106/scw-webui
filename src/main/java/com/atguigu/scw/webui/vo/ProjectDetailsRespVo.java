package com.atguigu.scw.webui.vo;

import java.io.Serializable;
import java.util.List;


import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class ProjectDetailsRespVo implements Serializable{
	private Integer memberid;// 会员id
	private List<Integer> typeids; // 项目的分类id
	private List<Integer> tagids; // 项目的标签id
	private String name;// 项目名称
	private String remark;// 项目简介
	private Long money;// 筹资金额
	private Integer day;// 筹资天数
    private String status;//手动设置    项目状态

    private String deploydate;// 审核通过的日期

    private Long supportmoney;//已经得到的支持金额

    private Integer supporter;//支持者数量

    private Integer completion;//支持完成度  [supportmoney/money]


    private String createdate;//手动设置  项目创建时间

    private Integer follower; //关注用户的数量

	private String headerImage;// 项目头部图片
	private List<String> detailsImage;// 项目详情图片
	private List<TReturn> projectReturns;// 项目回报
	// 发起人信息：自我介绍，详细自我介绍，联系电话，客服电话
	private TProjectInitiator projectInitiator;// 项目发起人信息
}
