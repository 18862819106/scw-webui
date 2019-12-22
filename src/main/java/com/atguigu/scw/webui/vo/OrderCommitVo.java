package com.atguigu.scw.webui.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderCommitVo {
	//address(收货地址), invoice(是否需要发票 1需要，0不需要)
	//invoictitle(发票抬头)，remark(订单备注),token(防止表单重复提交的token)
	private String address;
	private String token;
	private String invoice;
	private String invoictitle;
	private String remark;
	
}
