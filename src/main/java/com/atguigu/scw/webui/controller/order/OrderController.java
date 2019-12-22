package com.atguigu.scw.webui.controller.order;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.scw.common.bean.AppResponse;
import com.atguigu.scw.common.utils.AppDateUtils;
import com.atguigu.scw.webui.config.AlipayConfig;
import com.atguigu.scw.webui.feign.OrderControllerFeignClient;
import com.atguigu.scw.webui.feign.UserControllerFeignClient;
import com.atguigu.scw.webui.vo.OrderCommitVo;
import com.atguigu.scw.webui.vo.ProjectDetailsRespVo;
import com.atguigu.scw.webui.vo.TOrder;
import com.atguigu.scw.webui.vo.TReturn;
import com.atguigu.scw.webui.vo.UserAddressVo;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {
	
	@Autowired
	OrderControllerFeignClient orderControllerFeignClient;
	@Autowired
	UserControllerFeignClient userControllerFeignClient;
	//4、处理支付宝同步响应的方法:支付宝临时返回的支付状态
	@GetMapping(value="/return_url", produces="text/html")
	@ResponseBody
	public String returnurl(HttpServletRequest request) {
		//判断用户支付是否成功，给出响应
		try {
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map<String,String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用
				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			//验签
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

			//——请在这里编写您的程序（以下代码仅作参考）——
			if(signVerified) {
				//商户订单号
				String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			
				//支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
			
				//付款金额
				String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
				//用户支付成功
				//给用户响应成功页面
				return "trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount;
				//out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
			}else {
				//out.println("验签失败");//支付宝响应的支付结果可能被拦截修改，或者我们配置的公钥私钥有问题
				//验签失败： 配置的公钥私钥不匹配
				return "验签失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//支付异常
			return "支付失败，出现异常";
		}
	}
	//5、处理支付宝异步响应的方法：  订单最终的支付状态
	@RequestMapping("/notify_url")
	@ResponseBody
	public String notifyurl(HttpServletRequest request) {
		//获取支付宝GET过来反馈信息
		try {
			Map<String,String> params = new HashMap<String,String>();
			Map<String,String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用
				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
			//System.out.println("notify_url : "+params+" , "+signVerified);		
			//支付成功。更新订单的状态，修改为已支付.
			//商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			orderControllerFeignClient.updateOrder(out_trade_no, "1");
			System.out.println("out_trade_no:" +out_trade_no);
			//——请在这里编写您的程序（以下代码仅作参考）——
			
			/* 实际验证过程建议商户务必添加以下校验：
			1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
			2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
			3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
			4、验证app_id是否为该商户本身。
			*/
			if(signVerified) {//验证成功
				//商户订单号
				//String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			
				//支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
			
				//交易状态
				String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
				
				System.out.println("notify_url : "+trade_status+"," + trade_no+" , "+out_trade_no);		
				
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				}else if (trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
					
					//注意：
					//付款完成后，支付宝系统发送该交易状态通知
				}
				
				//out.println("success");
				
			}else {//验证失败
				//out.println("fail");
			
				//调试用，写文本函数记录程序运行情况是否正常
				//String sWord = AlipaySignature.getSignCheckContentV1(params);
				//AlipayConfig.logResult(sWord);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}
	
	//3、立即付款：
	//接受数据：
	//address(收货地址), invoice(是否需要发票 1需要，0不需要)
	//invoictitle(发票抬头)，remark(订单备注),token(防止表单重复提交的token)
	@PostMapping(value="/checkout" , produces="text/html")
	@ResponseBody //将响应的内容直接交给浏览器[响应的字符串是html+js代码，希望浏览器能够按照html页面的方式解析]
	public String checkout(OrderCommitVo vo,HttpSession session) {
		//设置订单需要的其他信息  memberid(购买订单的用户的id),  ordernum(订单编号),createdate(订单创建时间),projectid(订单购买的项目id),returnid(回报id), 
		// ,money(订单总金额),rtncount(回报数量)，status(订单状态)
		String clientToken = vo.getToken();
		String serverToken = (String) session.getAttribute("token");
		if(clientToken!=null && clientToken.equals(serverToken)) {
			session.removeAttribute("token");
			Map usermap = (Map) session.getAttribute("user");
			Integer memberid = (Integer) usermap.get("id");
			String ordernum = System.currentTimeMillis()+"9527"+memberid;
			String createdate = AppDateUtils.getFormatTime();
			Integer rtncount = (Integer) session.getAttribute("rtncount");
			ProjectDetailsRespVo project = (ProjectDetailsRespVo) session.getAttribute("project");
			TReturn rtn = (TReturn) session.getAttribute("rtn");
			String status = "0";//默认未支付状态
			int money = rtncount*rtn.getSupportmoney() + rtn.getFreight();
			Integer projectid = rtn.getProjectid();
			Integer rtnid = rtn.getId();
			//double   BigDecimal(String)
			//创建订单对象，获取域中的参数  将需要手动设置的参数设置给订单对象
			TOrder order = new TOrder(null, memberid, projectid, rtnid, ordernum, createdate, money, rtncount, status, vo.getAddress(), vo.getInvoice(), vo.getInvoictitle(), vo.getRemark());
			
			log.info("订单对象：{}",order);
			
			AppResponse<Object> appResponse = orderControllerFeignClient.createOrder(order);
			
			log.info("订单创建：{}",appResponse);
			if("00000".equals(appResponse.getCode())) {
			
				//订单创建成功
				//调用支付宝的API生成付款页面给用户
				//获得初始化的AlipayClient
				AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
				
				//设置请求参数
				AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
				alipayRequest.setReturnUrl(AlipayConfig.return_url);
				alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
				
				//商户订单号，商户网站订单系统中唯一订单号，必填
				String out_trade_no = ordernum;
				//付款金额，必填
				String total_amount = money+"";
				//订单名称，必填
				String subject = project.getName();
				//商品描述，可空
				String body = rtn.getContent();
				
				alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
						+ "\"total_amount\":\""+ total_amount +"\"," 
						+ "\"subject\":\""+ subject +"\"," 
						+ "\"body\":\""+ body +"\"," 
						+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
				
				//请求
				String result = "";
				try {
					result = alipayClient.pageExecute(alipayRequest).getBody();
				} catch (AlipayApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//输出到给浏览器的响应报文的响应体中[浏览器会自动解析]
				//out.println(result);
				return result;
			}else {
				return "订单创建失败";
			}
			
			
		}else {
			//表单重复提交
			return "";
			
		}
	}
	
	//2、跳转到确认订单的页面
	@GetMapping("/pay-step-2.html")
	public String paystep2(Integer count,Model model,HttpSession session,@RequestHeader("referer")String ref) {
		//判断用户是否登录
		Map usermap = (Map) session.getAttribute("user");
		if(CollectionUtils.isEmpty(usermap)) {
			//重定向到登录页面让用户登录
			session.setAttribute("errorMsg", "订单操作必须登录");
			//获取登录之前的页面,登录成功后需要使用该地址
			session.setAttribute("ref", ref);
			return "redirect:/user/login.html";
		}
		
		//查询当前用户的地址信息，必须保证该用户已经登录
		//提交用户登录的token，后台可以验证身份查询用户的信息
		String  accessToken = (String) usermap.get("accessToken");
		//远程调用user项目实现地址列表的查询
		AppResponse<List<UserAddressVo>> appResponse = userControllerFeignClient.address(accessToken);
		List<UserAddressVo> list = appResponse.getData();
		model.addAttribute("addresses", list);
		
		
		//用户需要结账
		
		model.addAttribute("rtncount", count);
		//生成防止订单提交页面的重复提交的token:解决订单重复创建的问题
		String token =  UUID.randomUUID().toString();
		session.setAttribute("token", token);
		session.setAttribute("rtncount", count);
		return "pages/order/pay-step-2";
	}
	
	
	//1、用户在project页面点击支持按钮  跳转到 订单信息收集的页面[页面需要显示项目、回报、项目发起人信息  ]
	@GetMapping("/pay-step-1.html")
	public String paystep1(Integer rtnid,HttpSession session) {
		//从session中获取project对象 和  rtnid对应的回报对象
		ProjectDetailsRespVo project = (ProjectDetailsRespVo) session.getAttribute("project");
		List<TReturn> returns = project.getProjectReturns();
		TReturn rtn = null;
		
		for (TReturn tReturn : returns) {
			
			if(tReturn.getId() == (int)rtnid) {
				log.info("-----要支持的项目回报id为{},正在遍历的returnid:{}" , rtnid,tReturn.getId());
				rtn = tReturn;
				break;
			}
		}
		log.info("要支持的项目:{}", project);
		log.info("要支持的项目回报id为{},内容:{}" , rtnid,rtn);
		//将rtn、存到 session域中
		session.setAttribute("rtn", rtn);
		return "pages/order/pay-step-1";
	}
}
